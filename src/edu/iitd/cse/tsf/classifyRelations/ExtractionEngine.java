package edu.iitd.cse.tsf.classifyRelations;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scala.collection.Seq;
import edu.iitd.cse.tsf.annotation.TextAnnotatorKBP;
import edu.iitd.cse.tsf.constants.KBPRelationTypeEnum;
import edu.iitd.cse.tsf.constants.OpenIEExtractionBOKBP;
import edu.iitd.cse.tsf.constants.PatternConstantEnumKBP;
import edu.iitd.cse.tsf.dependencyPattern.DependencyAnnotation;
import edu.iitd.cse.tsf.extractors.OpenIEExtractorKBP;
import edu.iitd.cse.tsf.extractors.SUTimeExtractorKBP;
import edu.iitd.cse.tsf.models.EntityOutputBO;
import edu.iitd.cse.tsf.models.KBPRelationClassificationOutputBO;
import edu.iitd.cse.tsf.models.QueryBO;
import edu.iitd.cse.tsf.models.RelationPattern;
import edu.iitd.cse.tsf.models.SentenceAnnotationBO;
import edu.iitd.cse.tsf.normalizer.PreProcessTextNormalization;
import edu.iitd.cse.tsf.patternMatcher.KBPRelationClassifier;
import edu.knowitall.openie.Instance;
import edu.stanford.nlp.time.SUTime.Temporal;


/**
 * @author abhishek
 *
 */
public class ExtractionEngine {

	public PreProcessTextNormalization textNormalizer;

	public SUTimeExtractorKBP suTimeExtractor;

	public LexicalClassifier lexicalClassifier;

	public KBPRelationClassifier relationClassifier;

	public TextAnnotatorKBP annotator;

	public OpenIEExtractorKBP openIEExtractor;

	public DependencyAnnotation dependencyAnnotation;

	public ExtractionEngine(String propertyFilePath) {
		super();

		lexicalClassifier = new LexicalClassifier(propertyFilePath);
		textNormalizer = new PreProcessTextNormalization(propertyFilePath);
		suTimeExtractor = new SUTimeExtractorKBP();

		annotator = new TextAnnotatorKBP();
		openIEExtractor = new OpenIEExtractorKBP();

		relationClassifier = new KBPRelationClassifier();
		relationClassifier.loadProperties(propertyFilePath);


		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(propertyFilePath);
			Properties properties = new Properties();
			properties.load(inputStream);

			String dependencyFilePath = properties.getProperty("dependencyPatternFilePath");
			System.out.println(dependencyFilePath);
			dependencyAnnotation = new DependencyAnnotation("",dependencyFilePath.trim());
		} catch (IOException e) {
			System.err.println("Error loading the properties or pattern files");
			e.printStackTrace();
		}
	}


	/**
	 * Function takes text, finds all the person and organization from the text and extracts all the relevant slotfills for the type
	 * @param text
	 * @return
	 * @throws IOException 
	 */
	public List<EntityOutputBO> processText(String text, boolean dependencyPatternOn) throws IOException {

		//Contains the list of all the entity and their coressponding slotfills.
		List<EntityOutputBO> entityOutputBOs = new ArrayList<EntityOutputBO>();

		//Perform Stanford annotation on the text
		List<SentenceAnnotationBO> sentenceAnnotationList = annotator.process(text);

		//Map of all the Person and Organization in the text
		Map<String,String> entityMap = new HashMap<String,String>();

		//Getting all the relevant entities in the entityMap
		for (SentenceAnnotationBO sentenceAnnotationBO : sentenceAnnotationList) {
			Map<String, String> nerMap = sentenceAnnotationBO.getNerMap();
			for (Entry<String, String> nerMapEntry : nerMap.entrySet()) {
				if(nerMapEntry.getValue().equalsIgnoreCase(KBPRelationTypeEnum.PERSON.toString()) || nerMapEntry.getValue().equalsIgnoreCase(KBPRelationTypeEnum.ORGANIZATION.toString())) {
					if(!entityMap.containsKey(nerMapEntry.getKey())) {
						entityMap.put(nerMapEntry.getKey(), nerMapEntry.getValue());
					}
				}
			}
		}

		List<String> othersEntityList = getOtherEntities(text);
		//Adding other entities to the entityMap
		for (String otherEntity : othersEntityList) {
			entityMap.put(otherEntity, KBPRelationTypeEnum.OTHERS.toString());
		}

		Set<String> completedEntity = new HashSet<String>();

		//Extraction for each entity
		for (Entry<String, String> entityMapEntry : entityMap.entrySet()) {

			String entity = entityMapEntry.getKey();
			String entityValue = entityMapEntry.getValue();

			//Skip the entity with partial name of entity already processed
			//Checking for duplicate entry
			boolean duplicateEntity = checkDuplicate(entity,entityMap.keySet());
			if(duplicateEntity) {
				continue;
			}

			completedEntity.add(entity);
			//Forming the query for the entity
			QueryBO queryBO = new QueryBO();
			queryBO.setId("123");
			queryBO.setEntity(entity);
			queryBO.setRelationTypeEnum(KBPRelationTypeEnum.fromValue(entityValue));

			//Map of relation type and slots fills
			Map<String, List<String>> entitySlotFills = new HashMap<String, List<String>>();

			//Map of the corresponding sentence for the slotfill
			Map<String, List<String>> entitySlotFillsSentences = new HashMap<String, List<String>>();

			//This set is used to store partial names and aliases
			Set<String> aliases = new HashSet<String>();

			//Adding partial entities to aliases
			String[] entitySplit = queryBO.getEntity().split(" ");
			for (String string : entitySplit) {
				aliases.add(string.toLowerCase());
			}
			aliases.add(queryBO.getEntity().toLowerCase());


			for (SentenceAnnotationBO sentenceAnnotationBO : sentenceAnnotationList) {

				Seq<Instance> seq = openIEExtractor.runExtraction(sentenceAnnotationBO.getSentence());
				List<OpenIEExtractionBOKBP> openIEExtractions = openIEExtractor.getOpenIEExtractions(seq, sentenceAnnotationBO.getSentence());

				for (OpenIEExtractionBOKBP openIEExtractionBO : openIEExtractions) {
					openIEExtractionBO.setOriginalText(sentenceAnnotationBO.getSentence());

					if(containsPartialEntiy(openIEExtractionBO, queryBO, aliases) || containsCoreferedEntityMention(openIEExtractionBO, queryBO, sentenceAnnotationBO, aliases)) {


						Map<String,List<String>> tagMap = new HashMap<String, List<String>>();
						Map<String,List<String>> tagMapArg1 = new HashMap<String, List<String>>();
						Map<String,List<String>> tagMapArg2 = new HashMap<String, List<String>>();


						String relationTerm = "", argument1 = "",argument2 = "";

						if(openIEExtractionBO.getRelation() != null)
							relationTerm = openIEExtractionBO.getRelation().toLowerCase();

						if(openIEExtractionBO.getArgument2() != null)
							argument2 = openIEExtractionBO.getArgument2().toLowerCase();

						if(openIEExtractionBO.getArgument1() != null)
							argument1 = openIEExtractionBO.getArgument1().toLowerCase();



						//Keyword normalization of relation term
						String normalizedRelationTerm = textNormalizer.performTextNormalization(relationTerm, sentenceAnnotationBO.getNerMap(), tagMap);

						//Keyword normalization of second argument
						String normalizedSecondArgument = textNormalizer.performTextNormalization(argument2, sentenceAnnotationBO.getNerMap(), tagMapArg2);

						//Keyword normalization of first argument
						String normalizedFirstArgument = textNormalizer.performTextNormalization(argument1, sentenceAnnotationBO.getNerMap(), tagMapArg1);

						//Generating the normalized OpenIE extractionBO
						OpenIEExtractionBOKBP normalizedOpenIEExtractionBO = new OpenIEExtractionBOKBP();
						normalizedOpenIEExtractionBO.setArgument1(normalizedFirstArgument);
						normalizedOpenIEExtractionBO.setArgument2(normalizedSecondArgument);
						normalizedOpenIEExtractionBO.setRelation(normalizedRelationTerm);


						//List<KBPRelationClassificationOutputBO> outputBOs = relationClassifier.classify(normalizedOpenIEExtractionBO,sentenceAnnotationBO.getNerMap(),queryBO.getRelationTypeEnum());
						List<KBPRelationClassificationOutputBO> outputBOs = relationClassifier.classifyForAllTypes(normalizedOpenIEExtractionBO,sentenceAnnotationBO.getNerMap());

						if(outputBOs != null && outputBOs.size() > 0) {

							for (KBPRelationClassificationOutputBO outputBO : outputBOs) {

								outputBO.setOpenIEExtractionBO(openIEExtractionBO);

								//To check whether the entity is in the desired slot of the pattern
								if(!containsEntityIndesiredArgument(outputBO, aliases,sentenceAnnotationBO)) {
									continue;
								}

								String relationType = outputBO.getPattern().getRelationType();
								String slotFill = getSlotFill4(outputBO, sentenceAnnotationBO, tagMap, tagMapArg1, tagMapArg2);

								//Slotfill cannot be the query argument or the slotfill is already present in aliases and slotfill cannot be null
								if(aliases.contains(slotFill.toLowerCase())|| slotFill == "" || slotFill == null) {
									continue;
								}

								if(!entitySlotFills.containsKey(relationType)){
									List<String> slotFillSet = new ArrayList<String>();
									slotFillSet.add(slotFill);
									entitySlotFills.put(relationType, slotFillSet);

									//Added to add sentence
									List<String> slotFillSetSentence = new ArrayList<String>();
									slotFillSetSentence.add(sentenceAnnotationBO.getSentence());
									entitySlotFillsSentences.put(relationType, slotFillSetSentence);

								} else {
									List<String> slotFillSet = entitySlotFills.get(relationType);
									slotFillSet.add(slotFill);

									//Added to add sentence
									List<String> slotFillListSentence = entitySlotFillsSentences.get(relationType);
									slotFillListSentence.add(sentenceAnnotationBO.getSentence());
								}

								System.out.println(outputBO.getOpenIEExtractionBO().getOriginalText());
								System.out.println(outputBO.getPattern().toString());
								System.out.println("############ "+relationType+"\t"+slotFill);

								//Adding the alternate names and their partial words to the list of aliases
								if(relationType.equalsIgnoreCase("alternate_names")) {
									aliases.add(slotFill);
									String[] slotFillSplit = slotFill.split(" ");
									for (String string : slotFillSplit) {
										aliases.add(string);
									}
								}
							}
						}

					}
				}

				if(textContainsEntity(sentenceAnnotationBO.getSentence(),aliases) || textContainsCoreferedEntityMention(sentenceAnnotationBO, aliases)) {
					//Results from Pattern
					Map<String, String> resultsFromPattern = lexicalClassifier.getResultsFromPattern(queryBO, sentenceAnnotationBO, aliases);

					for (Entry<String, String> patternResultEntry : resultsFromPattern.entrySet()) {

						if(!entitySlotFills.containsKey(patternResultEntry.getKey())){
							List<String> slotFillSet = new ArrayList<String>();
							slotFillSet.add(patternResultEntry.getValue());
							entitySlotFills.put(patternResultEntry.getKey(), slotFillSet);

							//Added to add sentence
							List<String> slotFillSetSentence = new ArrayList<String>();
							slotFillSetSentence.add(sentenceAnnotationBO.getSentence());
							entitySlotFillsSentences.put(patternResultEntry.getKey(), slotFillSetSentence);
						} else {
							List<String> slotFillSet = entitySlotFills.get(patternResultEntry.getKey());
							slotFillSet.add(patternResultEntry.getValue());

							//Added to add sentence
							List<String> slotFillListSentence = entitySlotFillsSentences.get(patternResultEntry.getKey());
							slotFillListSentence.add(sentenceAnnotationBO.getSentence());
						}

						//Adding the alternate names and their partial words to the list of aliases
						if(patternResultEntry.getKey().equalsIgnoreCase("alternate_names")) {
							aliases.add(patternResultEntry.getValue());
							String[] slotFillSplit = patternResultEntry.getValue().split(" ");
							for (String string : slotFillSplit) {
								aliases.add(string);
							}
						}
					}
				}




			}

			Map<String, String> completeNERMap = new HashMap<String,String>();
			for (SentenceAnnotationBO sentenceAnnotationBO : sentenceAnnotationList) {
				completeNERMap.putAll(sentenceAnnotationBO.getNerMap());
			}
			//Getting the slotfills from the Dependency Patterns if dependencyPatternOn is true
			if(dependencyPatternOn) {

				List<String> dependencyOutput = dependencyAnnotation.processKBP(entity, annotator.performAnnotations(text), textNormalizer, completeNERMap);
				for (String string : dependencyOutput) {
					//System.out.println(string);
					String[] split = string.split("\t");
					String slotType = split[1].trim();
					String slotFill = split[2].trim();

					if(!entitySlotFills.containsKey(slotType)){
						List<String> slotFillSet = new ArrayList<String>();
						slotFillSet.add(slotFill);
						entitySlotFills.put(slotType, slotFillSet);

						//Added to add sentence
						List<String> slotFillSetSentence = new ArrayList<String>();
						slotFillSetSentence.add(split[0]);
						entitySlotFillsSentences.put(slotType, slotFillSetSentence);
					} else {
						List<String> slotFillSet = entitySlotFills.get(slotType);
						slotFillSet.add(slotFill);

						//Added to add sentence
						List<String> slotFillListSentence = entitySlotFillsSentences.get(slotType);
						slotFillListSentence.add(split[0]);
					}
				}
			}

			Map<String,List<String>> newEntitySltFill =new HashMap<String, List<String>>();
			//Cleaning entity Slotfill and also making sure it contains unique entries
			for (Entry<String, List<String>> entry : entitySlotFills.entrySet()) {

				String key = entry.getKey();

				Set<String> set = new HashSet<String>();
				set.addAll(entry.getValue());
				List<String> newSlotFilList = new ArrayList<String>();
				newSlotFilList.addAll(set);

				if(completeNERMap.get(entity) != null && completeNERMap.get(entity).equalsIgnoreCase("person")) {
					if(key.startsWith("per")) {
						newEntitySltFill.put(key, newSlotFilList);
					}
				}else if(completeNERMap.get(entity) != null && completeNERMap.get(entity).equalsIgnoreCase("organization")) {
					if(key.startsWith("org")) {
						newEntitySltFill.put(key, entry.getValue());
					}
				}

			}

			//Making the Map contain unique list


			//Creating the BO for the entity and its slotfills
			if(entitySlotFills != null && entitySlotFills.size() > 0) {
				EntityOutputBO entityOutputBO = new EntityOutputBO();
				entityOutputBO.setEntity(queryBO.getEntity());
				entityOutputBO.setRelationTypeEnum(queryBO.getRelationTypeEnum());
				entityOutputBO.setEntitySlotFills(newEntitySltFill);
				entityOutputBO.setEntitySlotFillsSentence(entitySlotFillsSentences);

				entityOutputBOs.add(entityOutputBO);
			}
		}

		return entityOutputBOs;
	}


	/**
	 * Takes the text and performs the required normalization and returns the normalized text 
	 * Also fills the tagMap with the Normalization constant and their value
	 * 
	 * */


	/**
	 * Performs the SlotValue extraction from the normalized text
	 * */
	private String getSlotFill4(KBPRelationClassificationOutputBO outputBO,
			SentenceAnnotationBO sentenceAnnotationBO,
			Map<String, List<String>> tagMap,
			Map<String, List<String>> tagMapArg1,
			Map<String, List<String>> tagMapArg2) {
		// TODO Auto-generated method stub

		String slotFillValue = "";

		RelationPattern pattern = outputBO.getPattern();
		String slotType = pattern.getSlotType();
		String slotFillIn = pattern.getSlotFillIn();

		//Currently handling ProperNoun as any
		if(slotType.equals("ANY") ) {

			if(slotFillIn.equals("arg2")) {
				String arg2Begin = pattern.getArg2Begin();
				String argument2 = outputBO.getOpenIEExtractionBO().getArgument2().toLowerCase();

				slotFillValue = argument2.substring(arg2Begin.length()).trim();

			}else if(slotFillIn.equals("arg1")) {

				slotFillValue = outputBO.getOpenIEExtractionBO().getArgument1();
			}
		}else if(slotType.equals("PROPERNOUN")) {

			Set<String> properNounSet = sentenceAnnotationBO.getProperNounSet();

			if(slotFillIn.equals("arg2")) {
				String argument2 = " "+outputBO.getOpenIEExtractionBO().getArgument2().toLowerCase()+" ";

				//Getting the first proper noun in the 
				int slotFillIndex = Integer.MAX_VALUE;
				for (String string : properNounSet) {
					if(argument2.contains(" "+string.toLowerCase().trim()+" ")) {

						int stringIndex = argument2.indexOf(" "+string.toLowerCase().trim()+" ");
						if(stringIndex <slotFillIndex) {
							slotFillValue = string;
							slotFillIndex = stringIndex;
						}
					}
				}

			}else if(slotFillIn.equals("arg1")) {

				String argument1 = " "+outputBO.getOpenIEExtractionBO().getArgument1().toLowerCase()+" ";

				//Getting the first propernoun in the Argument1
				int slotFillIndex = Integer.MAX_VALUE;
				for (String string : properNounSet) {

					if(argument1.contains(" "+string.toLowerCase().trim()+" ")) {

						int stringIndex = argument1.indexOf(" "+string.toLowerCase().trim()+" ");
						if(stringIndex <slotFillIndex) {
							slotFillValue = string;
							slotFillIndex = stringIndex;
						}

					}
				}
			}

		}else {
			//SlotType is of the type present in the PatternConstantEnum

			if(slotFillIn.equals("arg2")) {

				String argument2 = " "+outputBO.getNormalizedOpenIEExtractionBO().getArgument2().trim()+" ";

				Pattern slotFillTypePattern = Pattern.compile("\\s+"+slotType+"#[0-9]+\\s+");
				Matcher slotFillTypeMatcher = slotFillTypePattern.matcher(argument2);
				if(slotFillTypeMatcher.find()) {
					String firstMatchedText = slotFillTypeMatcher.group();
					System.out.println("First matched Text = "+firstMatchedText);
					int index = Integer.parseInt(firstMatchedText.substring(firstMatchedText.indexOf("#")+1).trim());
					List<String> valueList = tagMapArg2.get(slotType.trim());
					if(valueList != null && valueList.size() > 0) {
						slotFillValue = valueList.get(index-1);
					}
				}

			}else if(slotFillIn.equals("arg1")) {

				String argument1 = " "+outputBO.getNormalizedOpenIEExtractionBO().getArgument1().trim()+" ";

				Pattern slotFillTypePattern = Pattern.compile("\\s+"+slotType+"#[0-9]+\\s+");
				Matcher slotFillTypeMatcher = slotFillTypePattern.matcher(argument1);
				if(slotFillTypeMatcher.find()) {
					String firstMatchedText = slotFillTypeMatcher.group();
					int index = Integer.parseInt(firstMatchedText.substring(firstMatchedText.indexOf("#")+1).trim());
					List<String> valueList = tagMapArg1.get(slotType.trim());
					if(valueList != null && valueList.size() > 0) {
						slotFillValue = valueList.get(index-1);
					}
				}

			}else if(slotFillIn.equals("relation")) {

				String relationTerm = " "+outputBO.getNormalizedOpenIEExtractionBO().getRelation().trim()+" ";

				Pattern slotFillTypePattern = Pattern.compile("\\s+"+slotType+"#[0-9]+\\s+");
				Matcher slotFillTypeMatcher = slotFillTypePattern.matcher(relationTerm);
				if(slotFillTypeMatcher.find()) {

					String firstMatchedText = slotFillTypeMatcher.group();
					int index = Integer.parseInt(firstMatchedText.substring(firstMatchedText.indexOf("#")+1).trim());
					List<String> valueList = tagMap.get(slotType.trim());
					if(valueList != null && valueList.size() > 0) {
						slotFillValue = valueList.get(index-1);
					}
				}
			}

		}

		return slotFillValue;
	}

	/**
	 * To check whether the entity or its co-referenced mention is in the desired slot
	 * @param outputBO
	 * @param aliases
	 * @param sentenceAnnotationBO
	 * @return
	 */
	private boolean containsEntityIndesiredArgument(
			KBPRelationClassificationOutputBO outputBO, Set<String> aliases,
			SentenceAnnotationBO sentenceAnnotationBO) {
		// TODO Auto-generated method stub
		boolean result = false;

		RelationPattern relationPattern = outputBO.getPattern();
		String entityIn = relationPattern.getEntityIn().trim();
		if(entityIn.equalsIgnoreCase("arg1")) {
			String argument1 = outputBO.getOpenIEExtractionBO().getArgument1();

			//Creating sentenceAnnotationBO or particular argument with only argument and the corefMap
			SentenceAnnotationBO sentenceAnnotationArgumentBO = new SentenceAnnotationBO();
			sentenceAnnotationArgumentBO.setSentence(argument1);
			sentenceAnnotationArgumentBO.setCorefMap(sentenceAnnotationBO.getCorefMap());

			result = textContainsEntity(argument1,aliases) || textContainsCoreferedEntityMention(sentenceAnnotationArgumentBO, aliases);

		}else if(entityIn.equalsIgnoreCase("arg2")) {

			String argument2 = outputBO.getOpenIEExtractionBO().getArgument2();

			//Creating sentenceAnnotationBO or particular argument with only argument and the corefMap
			SentenceAnnotationBO sentenceAnnotationArgumentBO = new SentenceAnnotationBO();
			sentenceAnnotationArgumentBO.setSentence(argument2);
			sentenceAnnotationArgumentBO.setCorefMap(sentenceAnnotationBO.getCorefMap());

			result = textContainsEntity(argument2,aliases) || textContainsCoreferedEntityMention(sentenceAnnotationArgumentBO, aliases);

		} else {
			String relationTerm = outputBO.getOpenIEExtractionBO().getRelation();

			//Creating sentenceAnnotationBO or particular argument with only argument and the corefMap
			SentenceAnnotationBO sentenceAnnotationArgumentBO = new SentenceAnnotationBO();
			sentenceAnnotationArgumentBO.setSentence(relationTerm);
			sentenceAnnotationArgumentBO.setCorefMap(sentenceAnnotationBO.getCorefMap());

			result = textContainsEntity(relationTerm,aliases) || textContainsCoreferedEntityMention(sentenceAnnotationArgumentBO, aliases);
		}

		return result;
	}

	/**
	 * Returns the list of products and technologies in the text.
	 * @param sentenceAnnotationList
	 * @return
	 */
	private List<String> getOtherEntities(String originalText) {
		// TODO Auto-generated method stub
		List<String> otherEntityList = new ArrayList<String>();
		List<String> productList = textNormalizer.getKeywordTagger().getList(PatternConstantEnumKBP.PRODUCT);
		List<String> technologyList = textNormalizer.getKeywordTagger().getList(PatternConstantEnumKBP.TECHNOLOGY);

		String text = " "+originalText.toLowerCase().trim()+" ";

		//Finding the name of technologies
		for (String techString : technologyList) {
			Pattern pattern = Pattern.compile("[^a-zA-Z0-9]+\\Q"+techString.toLowerCase()+"\\E[^a-zA-Z0-9]+");
			Matcher matcher = pattern.matcher(text);
			if(matcher.find()) {
				otherEntityList.add(techString.toLowerCase());
			}
			/*if(text.contains(" "+techString.toLowerCase()+" ")) {
				otherEntityList.add(techString.toLowerCase());
			}*/
		}

		//Finding the name of products
		for (String prodString : productList) {

			Pattern pattern = Pattern.compile("[^a-zA-Z0-9]+\\Q"+prodString.toLowerCase()+"\\E[^a-zA-Z0-9]+");
			Matcher matcher = pattern.matcher(text);
			if(matcher.find()) {
				otherEntityList.add(prodString.toLowerCase());
			}


			/*if(text.contains(" "+prodString.toLowerCase()+" ")) {
				otherEntityList.add(prodString.toLowerCase());
			}*/
		}

		return otherEntityList;
	}

	/**
	 * 
	 * @param text
	 * @param aliases
	 * @return
	 */
	public boolean textContainsEntity(String text, Set<String> aliases) {
		boolean containsEntity = false;
		for (String string : aliases) {
			if(text.toLowerCase().contains(string.toLowerCase())) {
				containsEntity= true;
				break;
			}

		}
		return containsEntity;
	}

	private boolean containsPartialEntiy(OpenIEExtractionBOKBP openIEExtractionBO,
			QueryBO queryBO, Set<String> aliases) {
		boolean returnBool = false;

		String entity = queryBO.getEntity();

		String argument1= "",argument2 = "";
		if(openIEExtractionBO.getArgument1() != null) {
			argument1 = openIEExtractionBO.getArgument1().toLowerCase();
		}

		if(openIEExtractionBO.getArgument2() != null) {
			argument2 = openIEExtractionBO.getArgument2().toLowerCase();
		}

		//Do complete Entity match
		if(argument1.contains(entity.toLowerCase()) || argument2.contains(entity.toLowerCase())) {

			returnBool = true;
			return returnBool;
		}

		if(queryBO.getRelationTypeEnum() == KBPRelationTypeEnum.PERSON) {

			//Do partial match of entity
			for (String string : aliases) {
				if(argument1.contains(string) || argument2.contains(string)) {
					returnBool = true;
					break;
				}
			}
		}

		return returnBool;
	}


	/**
	 * Checks whether the openIE extraction has coref Mention and whether it has a 
	 * Representative Mention in its chain which contains the required enity or its aliases. 
	 * 
	 * @param openIEExtractionBO
	 * @param queryBO
	 * @param sentenceAnnotationBO
	 * @param aliases
	 * @return
	 */
	private boolean containsCoreferedEntityMention(OpenIEExtractionBOKBP openIEExtractionBO,
			QueryBO queryBO,SentenceAnnotationBO sentenceAnnotationBO, Set<String> aliases) {



		boolean returnBool = false;


		String argument1= "",argument2 = "";
		if(openIEExtractionBO.getArgument1() != null) {
			argument1 = " "+openIEExtractionBO.getArgument1().toLowerCase().trim()+" ";
		}

		if(openIEExtractionBO.getArgument2() != null) {
			argument2 = " "+openIEExtractionBO.getArgument2().toLowerCase()+" ";
		}

		Map<String, Set<String>> corefMap = sentenceAnnotationBO.getCorefMap();

		if(corefMap == null) {
			return returnBool;
		}
		for (Entry<String, Set<String>> corefMapEntry : corefMap.entrySet()) {

			String corefMention = " "+corefMapEntry.getKey().trim()+" ";

			//Check presence of corefMention in argument1 and corresponding match of the coref's Representative Mention in the list of entity aliases
			if(argument1.contains(corefMention)) {
				returnBool = checkEntityInCorefSet(aliases, corefMapEntry.getValue());
			}

			if(returnBool)
				break;

			if(argument2.contains(corefMention)) {
				returnBool = checkEntityInCorefSet(aliases, corefMapEntry.getValue());
			}

		}

		return returnBool;
	}

	/**
	 * Checks if the corefValues contains any of the aliases of the entity.
	 * The set of aliases also contains the original entity name.
	 * @param aliases
	 * @param value
	 * @return
	 */
	private boolean checkEntityInCorefSet(Set<String> aliases, Set<String> corefValues) {
		// TODO Auto-generated method stub
		boolean matchBool = false;

		for (String alias : aliases) {

			for (String string : corefValues) {

				//matching the space appended values so that partial matching at the end's could also be performed
				String corefRep = " "+string+" ";
				if((" "+corefRep.trim()+" ").contains(" "+alias.trim()+" ")) {
					matchBool = true;
					break;
				}

			}

			if(matchBool)
				break;
		}

		return matchBool;
	}

	/**
	 * Checks presence of entity in the text in form of corefered Mention
	 * @param text
	 * @param aliases
	 * @param sentenceAnnotationBO
	 * @return
	 */
	private boolean textContainsCoreferedEntityMention(SentenceAnnotationBO sentenceAnnotationBO, Set<String> aliases) {

		boolean returnBool = false;

		String text = " "+sentenceAnnotationBO.getSentence().trim().toLowerCase()+" ";
		Map<String, Set<String>> corefMap = sentenceAnnotationBO.getCorefMap();

		if(corefMap == null) {
			return returnBool;
		}
		for (Entry<String, Set<String>> corefMapEntry : corefMap.entrySet()) {

			String corefMention = corefMapEntry.getKey();
			Set<String> corefRep = corefMapEntry.getValue();

			if(text.contains(" "+corefMention+" ")) {
				returnBool = checkEntityInCorefSet(aliases, corefRep);
			}

			if(returnBool) {
				break;
			}
		}

		return returnBool;

	}
	//Check if a key is a duplicate substring of a string already present in the entity Map
	private boolean checkDuplicate(String key, Set<String> keySet) {
		// TODO Auto-generated method stub

		boolean duplicate = false;
		int matchCount = 0;
		for (String string : keySet) {
			if(string.toLowerCase().contains(key.toLowerCase())) {
				matchCount++;
			}
		}

		if(matchCount > 1) {
			duplicate = true;
		}
		return duplicate;
	}

	public static void main(String args[]) throws IOException {

		ExtractionEngine engine = new ExtractionEngine("Resources/KBP/kbpDefault.properties");

		/*QueryBO queryBO = new QueryBO();
		queryBO.setEntity("Ramazan Bashardost");
		queryBO.setRelationTypeEnum(KBPRelationTypeEnum.PERSON);
		queryBO.setId("123");

		Map<String, Set<String>> processQuery = engine.processQuery(queryBO, "Many Hazaras said their sentimental favorite for president is Bashardost, 44, a reformist legislator and former planning minister whose office is in a tent across the street from parliament.");
		for (Entry<String,Set<String>> relMap : processQuery.entrySet()) {

			System.out.println(relMap.getKey());
			Set<String> value = relMap.getValue();
			for (String string : value) {
				System.out.println(string);
			}
			System.out.println("---------");
		}*/
		//String text = "Bill Gates(born October 28, 1955) is an American business magnate, philanthropist, investor, computer programmer, and inventor. Gates is the former chief executive and chairman of Microsoft, the world's largest personal-computer software company, which he co-founded with Paul Allen. Paul Allen was born on 10/07/1990. He is consistently ranked in the Forbes list of the world's wealthiest people and was the wealthiest overall from 1995 to 2009-excluding 2008, when he was ranked third; in 2011 he was the wealthiest American and the world's second wealthiest person. According to the Bloomberg Billionaires List, Gates became the world's richest person again in May 2013, a position that he last held on the list in 2007. He held the position until Carlos Slim reclaimed it in July 2014. As of September 2014, he is the second richest person in the world. During his career at Microsoft, Gates held the positions of CEO and chief software architect, he was also the largest individual shareholder up until May 2014. He has also authored and co-authored several books.";
		//String text = "Dr. Hanmin Jung works as the head of the Dept. of S/W research and chief researcher at Korea Institute of Science and Technology Information (KISTI), Korea since 2004. He received his B.S., M.S., and Ph.D. degrees in Computer Science and Engineering from POSTECH, Korea in 1992, 1994, and 2003. Previously, he was senior researcher at Electronics and Telecommunications Research Institute (ETRI), Korea, and worked as CTO at DiQuest Inc, Korea, Now, he is also adjunct professor at University of Science &amp; Technology (UST), Korea, executive director at Korea Contents Association, and committee member of ISO/IEC JTC1/SC32 and ISO/IEC JTC1/SC34. His current research interests include decision making support mainly based in the Semantic Web and text mining technologies, Big Data, information retrieval, human-computer interaction (HCI), data analytics, and natural language processing (NLP). For these research areas, over 250 papers and 200 patents have been published and created.";
		//String text = "ZIP Clip contains Galaxy.";
		//String text = "Galaxy contains Andriod";
		String text = "Tata Consultancy Services (TCS) is an Indian multinational information technology (IT) service, consulting and business solutions company. It is headquartered in Bombay . TCS operates in 46 countries. It is a subsidiary of Tata Group and is listed on the Bombay Stock Exchange and the National Stock Exchange of India. TCS is the largest Indian company by market capitalization and is the largest India-based IT services company by 2013 revenues. In 2013, TCS is ranked 40th overall in the Forbes World's Most Innovative Companies ranking, making it both the highest-ranked IT services company and the top Indian company. It is the world's 10th largest IT services provider, measured by the revenues.";
		//String text = "John married Jessica.";
		//String text = "Tata Consultancy Services was founded by Sachin and Binny Bansal in 2007 .";
		//String text = "It will take place at a time Obama is due to be in New York for the annual United Nations General Assembly.";
		//String text = "It will take place at a time Obama is due to be in New York for the annual United Nations General Assembly.";
		//Scanner scanner = new Scanner(System.in);

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while(true) {

			text = reader.readLine();
			List<EntityOutputBO> processText = engine.processText(text, true);
			for (EntityOutputBO entityOutputBO : processText) {
				String entity = entityOutputBO.getEntity();
				System.out.println("Entity ------ "+entity);
				Map<String, List<String>> entitySlotFills = entityOutputBO.getEntitySlotFills();
				for (Entry<String,List<String>> relMap : entitySlotFills.entrySet()) {

					System.out.println(relMap.getKey());

					List<String> sentences = entityOutputBO.getEntitySlotFillsSentence().get(relMap.getKey());
					List<String> value = relMap.getValue();
					for (int i=0;i<value.size();i++) {
						System.out.println(value.get(i)+"\t"+sentences.get(i));
					}
					System.out.println("---------");
				}
				System.out.println("###############################");
			}
			System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		}
	}

}
