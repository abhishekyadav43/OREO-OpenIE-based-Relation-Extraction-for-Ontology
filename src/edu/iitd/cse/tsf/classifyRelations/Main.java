/**
 * 
 */
package edu.iitd.cse.tsf.classifyRelations;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scala.collection.Seq;
import edu.iitd.cse.tsf.annotation.TextAnnotatorKBP;
import edu.iitd.cse.tsf.constants.KBPRelationTypeEnum;
import edu.iitd.cse.tsf.constants.OpenIEExtractionBOKBP;
import edu.iitd.cse.tsf.constants.PatternConstantEnumKBP;
import edu.iitd.cse.tsf.constants.RelationClassificationOutputBOKBP;
import edu.iitd.cse.tsf.constants.RelationTypeEnumKBP;
import edu.iitd.cse.tsf.extractors.OpenIEExtractorKBP;
import edu.iitd.cse.tsf.extractors.SUTimeExtractorKBP;
import edu.iitd.cse.tsf.models.KBPRelationClassificationOutputBO;
import edu.iitd.cse.tsf.models.QueryBO;
import edu.iitd.cse.tsf.models.RelationPattern;
import edu.iitd.cse.tsf.models.SentenceAnnotationBO;
import edu.iitd.cse.tsf.models.SolrBO;
import edu.iitd.cse.tsf.normalizer.DateNormalizer;
import edu.iitd.cse.tsf.normalizer.KeywordTagger;
import edu.iitd.cse.tsf.patternMatcher.KBPRelationClassifier;
import edu.iitd.cse.tsf.pattern_Old.DateNormalizer_Old;
import edu.iitd.cse.tsf.pattern_Old.KeywordTagger_Old;
import edu.iitd.cse.tsf.solrUtils.FetchDataFromSolr;
import edu.iitd.cse.tsf.utils.QueryParser;
import edu.knowitall.openie.Instance;
import edu.stanford.nlp.ling.CoreAnnotations.EndIndexAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.time.SUTime.Temporal;

/**
 * @author abhishek
 *
 */
public class Main {

	/**
	 * @param args
	 */

	public static KeywordTagger_Old keywordTagger;

	public static SUTimeExtractorKBP suTimeExtractor;

	public static LexicalClassifier lexicalClassifier;


	public static void mainT(String[] args) {

		lexicalClassifier= new LexicalClassifier("Resources/KBP/kbpDefault.properties");
		keywordTagger = new KeywordTagger_Old();
		suTimeExtractor = new SUTimeExtractorKBP();

		Properties properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream("Resources/KBP/kbpDefault.properties");
			properties.load(inputStream);

			//Loading lists
			keywordTagger.loadJobTitles(properties.getProperty("jobTitleFilePath"));
			keywordTagger.loadHeadJobTitles(properties.getProperty("headJobTitleFilePath"));
			keywordTagger.loadCountry(properties.getProperty("countriesFilePath"));
			keywordTagger.loadCity(properties.getProperty("citiesFilePath"));
			keywordTagger.loadStateOrProvince(properties.getProperty("statesOrProvincesFilePath"));
			keywordTagger.loadNationality(properties.getProperty("nationalitiesFilePath"));
			keywordTagger.loadSchool(properties.getProperty("schoolFilePath"));
			keywordTagger.loadCrime(properties.getProperty("crimesFilePath"));
		} catch(Exception e) {
			e.printStackTrace();
		}


		KBPRelationClassifier relationClassifier = new KBPRelationClassifier();
		relationClassifier.loadProperties("Resources/KBP/kbpDefault.properties");


		KeywordTagger keywordTagger2 = new KeywordTagger();
		try {
			keywordTagger2.loadJobTitles(properties.getProperty("jobTitleFilePath"));
			keywordTagger2.loadHeadJobTitles(properties.getProperty("headJobTitleFilePath"));
			keywordTagger2.loadCountry(properties.getProperty("countriesFilePath"));
			keywordTagger2.loadCity(properties.getProperty("citiesFilePath"));
			keywordTagger2.loadStateOrProvince(properties.getProperty("statesOrProvincesFilePath"));
			keywordTagger2.loadNationality(properties.getProperty("nationalitiesFilePath"));
			keywordTagger2.loadSchool(properties.getProperty("schoolFilePath"));
			keywordTagger2.loadCrime(properties.getProperty("crimesFilePath"));
			keywordTagger2.loadTechnology(properties.getProperty("technologyFilePath"));
			keywordTagger2.loadProduct(properties.getProperty("productsFilePath"));
		}catch(Exception e) {
			e.printStackTrace();
		}
		DateNormalizer dateNormalizer2 = new DateNormalizer();



		TextAnnotatorKBP annotator = new TextAnnotatorKBP();
		OpenIEExtractorKBP openIEExtractor = new OpenIEExtractorKBP();

		String text = "Tata Consultancy Services (TCS) is an Indian multinational information technology (IT) service, consulting and business solutions company. It is headquartered in Bombay . TCS operates in 46 countries. It is a subsidiary of Tata Group and is listed on the Bombay Stock Exchange and the National Stock Exchange of India. TCS is the largest Indian company by market capitalization and is the largest India-based IT services company by 2013 revenues. In 2013, TCS is ranked 40th overall in the Forbes World's Most Innovative Companies ranking, making it both the highest-ranked IT services company and the top Indian company. It is the world's 10th largest IT services provider, measured by the revenues.";

		List<SentenceAnnotationBO> sentenceAnnotationList = annotator.process(text);

		for (SentenceAnnotationBO sentenceAnnotationBO : sentenceAnnotationList) {
			//System.out.println(sentenceAnnotationBO.getSentence());

			Seq<Instance> seq = openIEExtractor.runExtraction(sentenceAnnotationBO.getSentence());
			List<OpenIEExtractionBOKBP> openIEExtractions = openIEExtractor.getOpenIEExtractions(seq, sentenceAnnotationBO.getSentence());

			for (OpenIEExtractionBOKBP openIEExtractionBO : openIEExtractions) {
				openIEExtractionBO.setOriginalText(sentenceAnnotationBO.getSentence());


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
				String normalizedRelationTerm = " "+relationTerm+" ";
				normalizedRelationTerm = dateNormalizer2.normalizeDate(normalizedRelationTerm,tagMap);
				normalizedRelationTerm = keywordTagger2.normalizeNERs(normalizedRelationTerm, sentenceAnnotationBO.getNerMap(), tagMap);
				normalizedRelationTerm = keywordTagger2.normalizeString(normalizedRelationTerm,tagMap);
				normalizedRelationTerm = keywordTagger2.normalizeInteger(normalizedRelationTerm, tagMap);

				//Keyword normalization of second argument
				String normalizedSecondArgument = " "+argument2+" ";
				normalizedSecondArgument = dateNormalizer2.normalizeDate(normalizedSecondArgument,tagMapArg2);
				normalizedSecondArgument = keywordTagger2.normalizeNERs(normalizedSecondArgument, sentenceAnnotationBO.getNerMap(), tagMapArg2);
				normalizedSecondArgument = keywordTagger2.normalizeString(normalizedSecondArgument, tagMapArg2);
				normalizedSecondArgument = keywordTagger2.normalizeInteger(normalizedSecondArgument, tagMapArg2);

				//Keyword normalization of first argument
				String normalizedFirstArgument = " "+argument1+" ";
				normalizedFirstArgument = dateNormalizer2.normalizeDate(normalizedFirstArgument,tagMapArg1);
				normalizedFirstArgument = keywordTagger2.normalizeNERs(normalizedFirstArgument, sentenceAnnotationBO.getNerMap(), tagMapArg1);
				normalizedFirstArgument = keywordTagger2.normalizeString(normalizedFirstArgument, tagMapArg1);
				normalizedFirstArgument = keywordTagger2.normalizeInteger(normalizedFirstArgument, tagMapArg1);

				//Generating the normalized OpenIE extractionBO
				OpenIEExtractionBOKBP normalizedOpenIEExtractionBO = new OpenIEExtractionBOKBP();
				normalizedOpenIEExtractionBO.setArgument1(normalizedFirstArgument);
				normalizedOpenIEExtractionBO.setArgument2(normalizedSecondArgument);
				normalizedOpenIEExtractionBO.setRelation(normalizedRelationTerm);


				List<KBPRelationClassificationOutputBO> outputBOs = relationClassifier.classify(normalizedOpenIEExtractionBO,sentenceAnnotationBO.getNerMap(),KBPRelationTypeEnum.PERSON);

				if(outputBOs != null && outputBOs.size() > 0) {

					for (KBPRelationClassificationOutputBO outputBO : outputBOs) {

						outputBO.setOpenIEExtractionBO(openIEExtractionBO);
						String relationType = outputBO.getPattern().getRelationType();
						//String slotFill = getSlotFill3(outputBO,sentenceAnnotationBO);
						String slotFill = getSlotFill4(outputBO, sentenceAnnotationBO, tagMap, tagMapArg1, tagMapArg2);

						System.out.println("############ "+relationType+"\t"+slotFill);
					}

					/*System.out.println(outputBO.getPattern().getRelationType());
				String slotFill = getSlotFill(outputBO);
				System.out.println("Slot Fill - "+slotFill);*/
				}
			}
			QueryBO queryBO = new QueryBO();
			queryBO.setRelationTypeEnum(KBPRelationTypeEnum.ORGANIZATION);
			queryBO.setEntity("Tata Consultancy Services");

			Set<String> aliases = new HashSet<String>();
			aliases.add("TCS");
			Map<String, String> resultsFromPattern = lexicalClassifier.getResultsFromPattern(queryBO, sentenceAnnotationBO, aliases);

			for (Entry<String, String> patternResultEntry : resultsFromPattern.entrySet()) {

				System.out.println(patternResultEntry.getKey()+"\t"+patternResultEntry.getValue());

			}

		}

	}

	public static void main3(String args[]) throws FileNotFoundException {

		lexicalClassifier= new LexicalClassifier("Resources/KBP/kbpDefault.properties");
		keywordTagger = new KeywordTagger_Old();
		suTimeExtractor = new SUTimeExtractorKBP();

		Properties properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream("Resources/KBP/kbpDefault.properties");
			properties.load(inputStream);

			//Loading lists
			keywordTagger.loadJobTitles(properties.getProperty("jobTitleFilePath"));
			keywordTagger.loadHeadJobTitles(properties.getProperty("headJobTitleFilePath"));
			keywordTagger.loadCountry(properties.getProperty("countriesFilePath"));
			keywordTagger.loadCity(properties.getProperty("citiesFilePath"));
			keywordTagger.loadStateOrProvince(properties.getProperty("statesOrProvincesFilePath"));
			keywordTagger.loadNationality(properties.getProperty("nationalitiesFilePath"));
			keywordTagger.loadSchool(properties.getProperty("schoolFilePath"));
			keywordTagger.loadCrime(properties.getProperty("crimesFilePath"));
		} catch(Exception e) {
			e.printStackTrace();
		}

		KBPRelationClassifier_Old relationClassifier = new KBPRelationClassifier_Old();
		relationClassifier.loadProperties("Resources/KBP/kbpDefault.properties");

		TextAnnotatorKBP annotator = new TextAnnotatorKBP();
		OpenIEExtractorKBP openIEExtractor = new OpenIEExtractorKBP();

		String text = "In other words, Adam was raised in the funky, radicalized Whole Earth vibe that is typical from Arcadia to Santa Cruz to Encinitas.Adam Pearlman joins al-QaedaNew York Sun reports that son of American Jew joined al-Qaeda, is wanted by FBIYnetnewsAn al-Qaeda activist who in a tape aired by the terror group called on American citizens to embrace Islam is Adam Pearlman, the son of a Jewish musician from California.The New York Sun newspaper reported on Wednesday that Pearlman, 28, has been on the FBI 's list of most wanted terrorists for two years.";

		List<SentenceAnnotationBO> sentenceAnnotationList = annotator.process(text);

		for (SentenceAnnotationBO sentenceAnnotationBO : sentenceAnnotationList) {
			//System.out.println(sentenceAnnotationBO.getSentence());

			Seq<Instance> seq = openIEExtractor.runExtraction(sentenceAnnotationBO.getSentence());
			List<OpenIEExtractionBOKBP> openIEExtractions = openIEExtractor.getOpenIEExtractions(seq, sentenceAnnotationBO.getSentence());

			for (OpenIEExtractionBOKBP openIEExtractionBO : openIEExtractions) {
				openIEExtractionBO.setOriginalText(sentenceAnnotationBO.getSentence());



				List<KBPRelationClassificationOutputBO> outputBOs = relationClassifier.classify(openIEExtractionBO,sentenceAnnotationBO.getNerMap(),KBPRelationTypeEnum.PERSON);

				if(outputBOs != null && outputBOs.size() > 0) {

					for (KBPRelationClassificationOutputBO outputBO : outputBOs) {
						String relationType = outputBO.getPattern().getRelationType();
						String slotFill = getSlotFill3(outputBO,sentenceAnnotationBO);

						System.out.println("############ "+relationType+"\t"+slotFill);
					}

					/*System.out.println(outputBO.getPattern().getRelationType());
				String slotFill = getSlotFill(outputBO);
				System.out.println("Slot Fill - "+slotFill);*/
				}
			}
			QueryBO queryBO = new QueryBO();
			queryBO.setRelationTypeEnum(KBPRelationTypeEnum.PERSON);
			queryBO.setEntity("adam gadahn");

			Set<String> aliases = new HashSet<String>();
			aliases.add("adam gadahn");
			Map<String, String> resultsFromPattern = lexicalClassifier.getResultsFromPattern(queryBO, sentenceAnnotationBO, aliases);

			for (Entry<String, String> patternResultEntry : resultsFromPattern.entrySet()) {

				System.out.println(patternResultEntry.getKey()+"\t"+patternResultEntry.getValue());

			}

		}

	}
	public static void main(String args[]) throws IOException {


		lexicalClassifier = new LexicalClassifier("Resources/KBP/kbpDefault.properties");
		keywordTagger = new KeywordTagger_Old();
		suTimeExtractor = new SUTimeExtractorKBP();

		Properties properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream("Resources/KBP/kbpDefault.properties");
			properties.load(inputStream);

			//Loading lists
			keywordTagger.loadJobTitles(properties.getProperty("jobTitleFilePath"));
			keywordTagger.loadHeadJobTitles(properties.getProperty("headJobTitleFilePath"));
			keywordTagger.loadCountry(properties.getProperty("countriesFilePath"));
			keywordTagger.loadCity(properties.getProperty("citiesFilePath"));
			keywordTagger.loadStateOrProvince(properties.getProperty("statesOrProvincesFilePath"));
			keywordTagger.loadNationality(properties.getProperty("nationalitiesFilePath"));
			keywordTagger.loadSchool(properties.getProperty("schoolFilePath"));
			keywordTagger.loadCrime(properties.getProperty("crimesFilePath"));
		} catch(Exception e) {
			e.printStackTrace();
		}

		if(args.length<2 ){
			System.out.println("Insufficient argument");
			System.exit(1);
		}
		//String text = "John Lewis joined Google in 2005. He is Acting Chief Director of Google.";

		PrintWriter writer = new PrintWriter(new FileWriter(args[1]));

		QueryParser parser = new QueryParser();
		parser.setQueryFilePath(args[0]);
		List<QueryBO> queryBOList = parser.processXMLQuery();

		FetchDataFromSolr dataFromSolr = new FetchDataFromSolr();
		dataFromSolr.setSolrURLWebDoc("http://10.208.23.201:8983/solr");
		dataFromSolr.setSolrURLNewsWire("http://10.237.23.28:8983/solr");
		dataFromSolr.setSolrURLDiscussionForum("http://10.237.23.28:9893/solr");

		KBPRelationClassifier_Old relationClassifier = new KBPRelationClassifier_Old();
		relationClassifier.loadProperties("Resources/KBP/kbpDefault.properties");

		TextAnnotatorKBP annotator = new TextAnnotatorKBP();
		OpenIEExtractorKBP openIEExtractor = new OpenIEExtractorKBP();




		for (QueryBO queryBO : queryBOList) {

			System.out.println("-----------------------------Processing query id "+queryBO.getId());
			try {
				//Map of relation type and slots fills
				Map<String, Set<String>> entitySlotFills = new HashMap<String, Set<String>>();

				//This set is used to store partial names and aliases
				Set<String> aliases = new HashSet<String>();

				//Adding partial entities to aliases
				//Commenting this for not allowing part of the entity name in the system
				/*String[] entitySplit = queryBO.getEntity().split(" ");
				for (String string : entitySplit) {
					aliases.add(string.toLowerCase());
				}*/
				aliases.add(queryBO.getEntity().toLowerCase());

				//Process Webdoc for entity
				int start = 0;
				boolean hasNext = true;

				while(hasNext) {

					hasNext = false;

					List<SolrBO> solrResultsWebDoc = dataFromSolr.getSolrResultsWebDoc("webdoc:\""+queryBO.getEntity()+"\"", start, 50);
					if(solrResultsWebDoc.size() == 50) {
						start +=50;
						hasNext = true;
					}

					for (SolrBO solrBO : solrResultsWebDoc) {
						String text = solrBO.getText().replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&apos;", "'");
						List<SentenceAnnotationBO> sentenceAnnotationList = annotator.process(text);

						for (SentenceAnnotationBO sentenceAnnotationBO : sentenceAnnotationList) {
							//System.out.println(sentenceAnnotationBO.getSentence());

							Seq<Instance> seq = openIEExtractor.runExtraction(sentenceAnnotationBO.getSentence());
							List<OpenIEExtractionBOKBP> openIEExtractions = openIEExtractor.getOpenIEExtractions(seq, sentenceAnnotationBO.getSentence());

							for (OpenIEExtractionBOKBP openIEExtractionBO : openIEExtractions) {
								openIEExtractionBO.setOriginalText(sentenceAnnotationBO.getSentence());

								if(/*containsEntity(openIEExtractionBO, queryBO.getEntity(), sentenceAnnotationBO.getCorefMap())*/containsPartialEntiy(openIEExtractionBO, queryBO, aliases)) {
									List<KBPRelationClassificationOutputBO> outputBOs = relationClassifier.classify(openIEExtractionBO,sentenceAnnotationBO.getNerMap(),queryBO.getRelationTypeEnum());

									if(outputBOs != null && outputBOs.size() > 0) {

										for (KBPRelationClassificationOutputBO outputBO : outputBOs) {

											//To check wether 
											/*if(!containsEntityIndesiredSlot(outputBO, aliases,sentenceAnnotationBO)) {
												continue;
											}*/
											String relationType = outputBO.getPattern().getRelationType();
											String slotFill = getSlotFill3(outputBO,sentenceAnnotationBO);

											//Slotfill cannot be the query argument or the slotfill is already present in aliases
											if(aliases.contains(slotFill.toLowerCase())) {
												continue;
											}

											if(!entitySlotFills.containsKey(relationType)){
												Set<String> slotFillSet = new HashSet<String>();
												slotFillSet.add(slotFill);

												entitySlotFills.put(relationType, slotFillSet);
											} else {
												Set<String> slotFillSet = entitySlotFills.get(relationType);
												slotFillSet.add(slotFill);
											}

											System.out.println(outputBO.getOpenIEExtractionBO().getOriginalText());
											System.out.println(outputBO.getPattern().toString());
											System.out.println("############ "+relationType+"\t"+slotFill);

											//########################################################################################################
											//Adding the alternate names and their partial words to the list of aliases
											//Uncomment to allow alternate names to be added to the aliases list
											/*if(relationType.equalsIgnoreCase("alternate_names")) {
												aliases.add(slotFill);
												String[] slotFillSplit = slotFill.split(" ");
												for (String string : slotFillSplit) {
													aliases.add(string);
												}
											}*/
											//########################################################################################################
										}

										/*System.out.println(outputBO.getPattern().getRelationType());
								String slotFill = getSlotFill(outputBO);
								System.out.println("Slot Fill - "+slotFill);*/
									}
								}

							}
							//########################################################################################################
							//Uncomment to allow lexical rules

							/*if(textContainsEntity(sentenceAnnotationBO.getSentence(),aliases)) {
								Map<String, String> resultsFromPattern = lexicalClassifier.getResultsFromPattern(queryBO, sentenceAnnotationBO, aliases);

								for (Entry<String, String> patternResultEntry : resultsFromPattern.entrySet()) {

									if(!entitySlotFills.containsKey(patternResultEntry.getKey())){
										Set<String> slotFillSet = new HashSet<String>();
										slotFillSet.add(patternResultEntry.getValue());

										entitySlotFills.put(patternResultEntry.getKey(), slotFillSet);
									} else {
										Set<String> slotFillSet = entitySlotFills.get(patternResultEntry.getKey());
										slotFillSet.add(patternResultEntry.getValue());
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
							}*/
							//########################################################################################################

						}

					}
				}


				start = 0;
				hasNext = true;

				//Process NewsWire for entity

				while(hasNext) {

					hasNext = false;
					List<SolrBO> solrResultsNewsWire = dataFromSolr.getSolrResultsNewsWire("doc:\""+queryBO.getEntity()+"\"", start, 50);

					if(solrResultsNewsWire.size() == 50) {
						start+=50;
						hasNext = true;
					}

					for (SolrBO solrBO : solrResultsNewsWire) {
						String text = solrBO.getText().replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&apos;", "'");
						List<SentenceAnnotationBO> sentenceAnnotationList = annotator.process(text);

						for (SentenceAnnotationBO sentenceAnnotationBO : sentenceAnnotationList) {
							//System.out.println(sentenceAnnotationBO.getSentence());

							Seq<Instance> seq = openIEExtractor.runExtraction(sentenceAnnotationBO.getSentence());
							List<OpenIEExtractionBOKBP> openIEExtractions = openIEExtractor.getOpenIEExtractions(seq, sentenceAnnotationBO.getSentence());

							for (OpenIEExtractionBOKBP openIEExtractionBO : openIEExtractions) {
								openIEExtractionBO.setOriginalText(sentenceAnnotationBO.getSentence());

								if(/*containsEntity(openIEExtractionBO, queryBO.getEntity(), sentenceAnnotationBO.getCorefMap())*/ containsPartialEntiy(openIEExtractionBO, queryBO, aliases)) {
									List<KBPRelationClassificationOutputBO> outputBOs = relationClassifier.classify(openIEExtractionBO,sentenceAnnotationBO.getNerMap(),queryBO.getRelationTypeEnum());

									if(outputBOs != null && outputBOs.size() > 0) {

										for (KBPRelationClassificationOutputBO outputBO : outputBOs) {
											String relationType = outputBO.getPattern().getRelationType();
											String slotFill = getSlotFill3(outputBO,sentenceAnnotationBO);

											//SlotFill should not be equal to the query entity
											if(aliases.contains(slotFill.toLowerCase())) {
												continue;
											}

											if(!entitySlotFills.containsKey(relationType)){
												Set<String> slotFillSet = new HashSet<String>();
												slotFillSet.add(slotFill);

												entitySlotFills.put(relationType, slotFillSet);
											} else {
												Set<String> slotFillSet = entitySlotFills.get(relationType);
												slotFillSet.add(slotFill);
											}

											System.out.println(outputBO.getOpenIEExtractionBO().getOriginalText());
											System.out.println(outputBO.getPattern().toString());
											System.out.println("############ "+relationType+"\t"+slotFill);

											//########################################################################################################
											//Adding the alternate names and their partial words to the list of aliases
											//Uncomment htis part to add alternate names to the aliases
											/*if(relationType.equalsIgnoreCase("alternate_names")) {
												aliases.add(slotFill);
												String[] slotFillSplit = slotFill.split(" ");
												for (String string : slotFillSplit) {
													aliases.add(string);
												}
											}*/

											//########################################################################################################
										}

										/*System.out.println(outputBO.getPattern().getRelationType());
								String slotFill = getSlotFill(outputBO);
								System.out.println("Slot Fill - "+slotFill);*/
									}
								}

							}
							//########################################################################################################
							/*if(textContainsEntity(sentenceAnnotationBO.getSentence(),aliases)) {
								Map<String, String> resultsFromPattern = lexicalClassifier.getResultsFromPattern(queryBO, sentenceAnnotationBO,aliases);

								for (Entry<String, String> patternResultEntry : resultsFromPattern.entrySet()) {

									if(!entitySlotFills.containsKey(patternResultEntry.getKey())){
										Set<String> slotFillSet = new HashSet<String>();
										slotFillSet.add(patternResultEntry.getValue());

										entitySlotFills.put(patternResultEntry.getKey(), slotFillSet);
									} else {
										Set<String> slotFillSet = entitySlotFills.get(patternResultEntry.getKey());
										slotFillSet.add(patternResultEntry.getValue());
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

							}*/
							//########################################################################################################

						}

					}
				}


				hasNext = true;
				start = 0;
				//Process Discussion Forum for entity
				while(hasNext) {

					hasNext = false;
					List<SolrBO> solrResultsDiscussionForum = dataFromSolr.getSolrResultsDiscussionForum("post:\""+queryBO.getEntity()+"\"", start, 50);

					if(solrResultsDiscussionForum.size() == 50) {
						start+=50;
						hasNext = true;
					}
					for (SolrBO solrBO : solrResultsDiscussionForum) {
						String text = solrBO.getText().replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&apos;", "'");

						//Do stanford Annotation
						List<SentenceAnnotationBO> sentenceAnnotationList = annotator.process(text);

						for (SentenceAnnotationBO sentenceAnnotationBO : sentenceAnnotationList) {
							//System.out.println(sentenceAnnotationBO.getSentence());

							//Do OpenIE Extraction
							Seq<Instance> seq = openIEExtractor.runExtraction(sentenceAnnotationBO.getSentence());
							List<OpenIEExtractionBOKBP> openIEExtractions = openIEExtractor.getOpenIEExtractions(seq, sentenceAnnotationBO.getSentence());

							for (OpenIEExtractionBOKBP openIEExtractionBO : openIEExtractions) {
								openIEExtractionBO.setOriginalText(sentenceAnnotationBO.getSentence());

								if(/*containsEntity(openIEExtractionBO, queryBO.getEntity(), sentenceAnnotationBO.getCorefMap())*/containsPartialEntiy(openIEExtractionBO, queryBO, aliases)) {
									List<KBPRelationClassificationOutputBO> outputBOs = relationClassifier.classify(openIEExtractionBO,sentenceAnnotationBO.getNerMap(),queryBO.getRelationTypeEnum());

									if(outputBOs != null && outputBOs.size() > 0) {

										for (KBPRelationClassificationOutputBO outputBO : outputBOs) {
											String relationType = outputBO.getPattern().getRelationType();
											String slotFill = getSlotFill3(outputBO,sentenceAnnotationBO);

											//SlotFill should not be equal to the query entity
											if(aliases.contains(slotFill.toLowerCase())) {
												continue;
											}

											if(!entitySlotFills.containsKey(relationType)){
												Set<String> slotFillSet = new HashSet<String>();
												slotFillSet.add(slotFill);

												entitySlotFills.put(relationType, slotFillSet);
											} else {
												Set<String> slotFillSet = entitySlotFills.get(relationType);
												slotFillSet.add(slotFill);
											}

											System.out.println(outputBO.getOpenIEExtractionBO().getOriginalText());
											System.out.println(outputBO.getPattern().toString());
											System.out.println("############ "+relationType+"\t"+slotFill);

											//########################################################################################################
											//Adding the alternate names and their partial words to the list of aliases
											//Uncomment this part to add alternate names to the list of aliases
											/*if(relationType.equalsIgnoreCase("alternate_names")) {
												aliases.add(slotFill);
												String[] slotFillSplit = slotFill.split(" ");
												for (String string : slotFillSplit) {
													aliases.add(string);
												}
											}*/
											//########################################################################################################
										}

										/*System.out.println(outputBO.getPattern().getRelationType());
								String slotFill = getSlotFill(outputBO);
								System.out.println("Slot Fill - "+slotFill);*/
									}
								}

							}
							//########################################################################################################
							/*if(textContainsEntity(sentenceAnnotationBO.getSentence(),aliases)) {
								//Results from Pattern
								Map<String, String> resultsFromPattern = lexicalClassifier.getResultsFromPattern(queryBO, sentenceAnnotationBO, aliases);

								for (Entry<String, String> patternResultEntry : resultsFromPattern.entrySet()) {

									if(!entitySlotFills.containsKey(patternResultEntry.getKey())){
										Set<String> slotFillSet = new HashSet<String>();
										slotFillSet.add(patternResultEntry.getValue());

										entitySlotFills.put(patternResultEntry.getKey(), slotFillSet);
									} else {
										Set<String> slotFillSet = entitySlotFills.get(patternResultEntry.getKey());
										slotFillSet.add(patternResultEntry.getValue());
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
							}*/
							//########################################################################################################

						}

					}
				}

				System.out.println("##########################Size of Entry of Query Id "+queryBO.getId()+"\t"+entitySlotFills.size());
				for (Entry<String, Set<String>> entry: entitySlotFills.entrySet()) {

					Set<String> value = entry.getValue();
					for (String string : value) {
						if(string.length() > 0) {
							System.out.println(entry.getKey()+"\t"+string);
							writer.write(queryBO.getId()+"\t"+entry.getKey()+"\t"+string+"\n");
						}
					}
					//System.out.println("-----------------------------------");
				}

			}catch(Exception e) {
				System.out.println("Error processing query with Id"+queryBO.getId());
				e.printStackTrace();
			}

		} 

		writer.close();


	}

	/**
	 * Check wether the output contains the enity or its coref in desired slot
	 * @param outputBO
	 * @param aliases
	 * @param sentenceAnnotationBO
	 * @return
	 */
	/*private static boolean containsEntityIndesiredSlot(
			KBPRelationClassificationOutputBO outputBO, Set<String> aliases,
			SentenceAnnotationBO sentenceAnnotationBO) {

		RelationPattern relationPattern = outputBO.getPattern();
		String entityIn = relationPattern.getEntityIn();
		if(entityIn.equals("arg1"))

		return false;
	}*/
	//Checks for presence of partial entity in OPenIE extraction and aliases. Aliases contains the partial entities
	public static boolean containsPartialEntiy(OpenIEExtractionBOKBP openIEExtractionBO, QueryBO queryBO, Set<String> aliases) {
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

	public static boolean textContainsEntity(String text, Set<String> aliases) {
		boolean containsEntity = false;
		for (String string : aliases) {
			if(text.toLowerCase().contains(string.toLowerCase())) {
				containsEntity= true;
				break;
			}

		}
		return containsEntity;
	}

	//Checks for the presence of entity in openIE extractions
	public static boolean containsEntity(OpenIEExtractionBOKBP openIEExtractionBO, String entity, Map<String, Set<String>> corefMap) {

		boolean returnBool = false;

		String newEntity = entity.toLowerCase();

		//The set of coref which has the required entity in its chain
		Set<String> corefSet = new HashSet<String>();
		for (Entry<String, Set<String>>corefEntry : corefMap.entrySet()) {

			Set<String> value = corefEntry.getValue();
			if(value.contains(entity.toLowerCase()) || value.contains(entity)) {
				corefSet.add(corefEntry.getKey());
			}
		}

		String argument1= "",argument2 = "";
		if(openIEExtractionBO.getArgument1() != null) {
			argument1 = openIEExtractionBO.getArgument1().toLowerCase();
		}

		if(openIEExtractionBO.getArgument2() != null) {
			argument2 = openIEExtractionBO.getArgument2().toLowerCase();
		}

		//Check for the exact entity
		if(argument1.contains(newEntity) || argument2.contains(newEntity)) {
			returnBool = true;
		}

		//Check for the coref with the entity
		for (String string : corefSet) {
			if(argument1.contains(string) || argument2.contains(string)) {
				returnBool = true;
				break;
			}
		}


		return returnBool;
	}

	public static String getSlotFill(KBPRelationClassificationOutputBO outputBO, SentenceAnnotationBO sentenceAnnotationBO) {

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
				String argument2 = outputBO.getOpenIEExtractionBO().getArgument2().toLowerCase();

				for (String string : properNounSet) {
					if(argument2.contains(string.toLowerCase())) {
						slotFillValue = string;
						break;
					}
				}

			}else if(slotFillIn.equals("arg1")) {

				String argument1 = outputBO.getOpenIEExtractionBO().getArgument1().toLowerCase();

				for (String string : properNounSet) {
					if(argument1.contains(string.toLowerCase())) {
						slotFillValue = string;
						break;
					}
				}
			}

		}else if(slotFillIn.equals("arg1")) {

			String normalizedArgument1 = outputBO.getNormalizedOpenIEExtractionBO().getArgument1();
			String argument1 = outputBO.getOpenIEExtractionBO().getArgument1().toLowerCase();

			int startIndex = normalizedArgument1.indexOf(slotType);

			/*String prevToken= " ",nextToken = " ";
			//Get the previous token
			if(startIndex > 0) {
				String[] normalizedArgument1Split = normalizedArgument1.trim().split(" ");
				for (int i=0;i<normalizedArgument1Split.length;i++) {
					if(normalizedArgument1Split[i].equals(slotType)) {
						if(i>0)
							prevToken = " "+normalizedArgument1Split[i-1]+" ";
						if(i<normalizedArgument1Split.length-1)
							nextToken = " "+normalizedArgument1Split[i+1]+" ";
					}
				}

			}*/

			if(startIndex != -1) {
				if(startIndex+slotType.length() < normalizedArgument1.length()) {

					String followedBy = normalizedArgument1.substring(startIndex+slotType.length());

					int endIndex = -1;
					if(followedBy.length() > 0)
						endIndex = argument1.indexOf(followedBy);
					/*Pattern regexPattern = Pattern.compile("("+prevToken+")[a-zA-Z0-9 ]{1,30}("+nextToken+")");
					Matcher regexMatcher = regexPattern.matcher(argument1);

					int patternStart=-1, patternEnd = -1;
					if(regexMatcher.find()) {
						patternStart = regexMatcher.start();
						patternEnd = regexMatcher.end();


					slotFillValue=argument1.substring(patternStart+prevToken.length(), patternEnd-nextToken.length());
					}*/
					/*if(prevToken.length() > 0) {
						startIndex = argument1.indexOf(prevToken)+prevToken.length();
					}

					int endIndex = -1;
					if(nextToken.length() > 0) {
						endIndex = argument1.indexOf(nextToken)-1;
					}*/

					if(endIndex != -1)
						slotFillValue = argument1.substring(startIndex, endIndex);
					else
						slotFillValue = argument1.substring(startIndex);
				} else {
					slotFillValue = argument1.substring(startIndex);
				}
			}

		}else if(slotFillIn.equals("arg2")) {

			String normalizedArgument2 = outputBO.getNormalizedOpenIEExtractionBO().getArgument2();
			String argument2 = outputBO.getOpenIEExtractionBO().getArgument2().toLowerCase();

			int startIndex = normalizedArgument2.indexOf(slotType);

			/*String prevToken= "",nextToken = "";
			//Get the previous token
			if(startIndex > 0) {
				String[] normalizedArgument2Split = normalizedArgument2.split(" ");
				for (int i=0;i<normalizedArgument2Split.length;i++) {
					if(normalizedArgument2Split[i].equals(slotType)) {
						if(i>0)
							prevToken = " "+normalizedArgument2Split[i-1]+" ";
						if(i<normalizedArgument2Split.length-1)
							nextToken = " "+normalizedArgument2Split[i+1]+" ";
						break;
					}
				}

			}*/

			if(startIndex != -1) {
				if(startIndex+slotType.length() < normalizedArgument2.length()) {

					String followedBy = normalizedArgument2.substring(startIndex+slotType.length());

					int endIndex = -1;
					if(followedBy.length() > 0)
						endIndex = argument2.indexOf(followedBy);
					/*Pattern regexPattern = Pattern.compile("("+prevToken+")[a-zA-Z0-9 ]{1,30}?("+nextToken+")");
					Matcher regexMatcher = regexPattern.matcher(argument2);

					int patternStart=-1, patternEnd = -1;
					if(regexMatcher.find()) {
						patternStart = regexMatcher.start();
						patternEnd = regexMatcher.end();


					slotFillValue=argument2.substring(patternStart+prevToken.length(), patternEnd-nextToken.length());
					}*/

					/*if(prevToken.length() > 0) {
						startIndex = argument2.indexOf(prevToken)+prevToken.length();
					}

					int endIndex = -1;
					if(nextToken.length() > 0) {
						endIndex = argument2.indexOf(nextToken)-1;
					}*/

					if(endIndex != -1)
						slotFillValue = argument2.substring(startIndex, endIndex);
					else
						slotFillValue = argument2.substring(startIndex);
				} else {
					slotFillValue = argument2.substring(startIndex);
				}
			}

		}else if(slotFillIn.equals("relation")) {

			String normalizedRelationTerm = outputBO.getNormalizedOpenIEExtractionBO().getRelation();
			String relationTerm = outputBO.getOpenIEExtractionBO().getRelation().toLowerCase();

			int startIndex = normalizedRelationTerm.indexOf(slotType);

			/*String prevToken= "",nextToken = "";
			//Get the previous token
			if(startIndex > 0) {
				String[] relationTermSplit = normalizedRelationTerm.split(" ");
				for (int i=0;i<relationTermSplit.length;i++) {
					if(relationTermSplit[i].equals(slotType)) {
						if(i>0)
							prevToken = " "+relationTermSplit[i-1]+" ";
						if(i<relationTermSplit.length-1)
							nextToken = " "+relationTermSplit[i+1]+" ";
					}
				}

			}*/

			if(startIndex != -1) {
				if(startIndex+slotType.length() < normalizedRelationTerm.length()) {

					String followedBy = normalizedRelationTerm.substring(startIndex+slotType.length());

					int endIndex = -1;
					if(followedBy.length() > 0)
						endIndex = relationTerm.indexOf(followedBy);
					/*Pattern regexPattern = Pattern.compile("("+prevToken+")[a-zA-Z0-9 ]{1,30}?("+nextToken+")");
					Matcher regexMatcher = regexPattern.matcher(relationTerm);

					int patternStart=-1, patternEnd = -1;
					if(regexMatcher.find()) {
						patternStart = regexMatcher.start();
						patternEnd = regexMatcher.end();


					slotFillValue=relationTerm.substring(patternStart+prevToken.length(), patternEnd-nextToken.length());
					}*/

					/*if(prevToken.length() > 0) {
						startIndex = relationTerm.indexOf(prevToken)+prevToken.length();
					}

					int endIndex = -1;
					if(nextToken.length() > 0) {
						endIndex = relationTerm.indexOf(nextToken)-1;
					}*/

					if(endIndex != -1)
						slotFillValue = relationTerm.substring(startIndex, endIndex);
					else
						slotFillValue = relationTerm.substring(startIndex);
				} else {
					slotFillValue = relationTerm.substring(startIndex);
				}
			}
		}

		return slotFillValue;
	}


	private static String getSlotFill4(
			KBPRelationClassificationOutputBO outputBO,
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
					int index = Integer.parseInt(firstMatchedText.substring(firstMatchedText.indexOf("#")+1));
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
					int index = Integer.parseInt(firstMatchedText.substring(firstMatchedText.indexOf("#")+1));
					List<String> valueList = tagMap.get(slotType.trim());
					if(valueList != null && valueList.size() > 0) {
						slotFillValue = valueList.get(index-1);
					}
				}
			}

		}

		return slotFillValue;
	}

	public static String getSlotFill3(KBPRelationClassificationOutputBO outputBO, SentenceAnnotationBO sentenceAnnotationBO) {

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

				for (String string : properNounSet) {
					if(argument2.contains(" "+string.toLowerCase().trim()+" ")) {
						slotFillValue = string;
						break;
					}
				}

			}else if(slotFillIn.equals("arg1")) {

				String argument1 = " "+outputBO.getOpenIEExtractionBO().getArgument1().toLowerCase()+" ";

				for (String string : properNounSet) {
					if(argument1.contains(" "+string.toLowerCase().trim()+" ")) {
						slotFillValue = string;
						break;
					}
				}
			}

		}else if(slotType.equals("PERSON")) {

			Map<String, String> nerMap = sentenceAnnotationBO.getNerMap();
			if(slotFillIn.equals("arg2")) {

				String argument2 = " "+outputBO.getOpenIEExtractionBO().getArgument2().toLowerCase()+" ";
				for (Entry<String,String> personEntry : nerMap.entrySet()) {
					String key = personEntry.getKey();
					String value = personEntry.getValue();
					if(value.equalsIgnoreCase("PERSON") && argument2.toLowerCase().contains(" "+key.toLowerCase().trim()+" ")) {
						slotFillValue = key;
						break;
					}
				}

			}else if(slotFillIn.equals("arg1")) {
				String argument1 = " "+outputBO.getOpenIEExtractionBO().getArgument1().toLowerCase()+" ";
				for (Entry<String,String> personEntry : nerMap.entrySet()) {
					String key = personEntry.getKey();
					String value = personEntry.getValue();
					if(value.equalsIgnoreCase("PERSON") && argument1.toLowerCase().contains(" "+key.toLowerCase().trim()+" ")) {
						slotFillValue = key;
						break;
					}
				}
			}

		}else if(slotType.equals("ORGANIZATION")) {




			Map<String, String> nerMap = sentenceAnnotationBO.getNerMap();
			if(slotFillIn.equals("arg2")) {

				String argument2 = " "+outputBO.getOpenIEExtractionBO().getArgument2().toLowerCase()+" ";
				for (Entry<String,String> personEntry : nerMap.entrySet()) {
					String key = personEntry.getKey();
					String value = personEntry.getValue();
					if(value.equalsIgnoreCase("ORGANIZATION") && argument2.toLowerCase().contains(" "+key.toLowerCase().trim()+" ")) {
						slotFillValue = key;
						break;
					}
				}

			}else if(slotFillIn.equals("arg1")) {
				String argument1 = " "+outputBO.getOpenIEExtractionBO().getArgument1().toLowerCase()+" ";
				for (Entry<String,String> personEntry : nerMap.entrySet()) {
					String key = personEntry.getKey();
					String value = personEntry.getValue();
					if(value.equalsIgnoreCase("ORGANIZATION") && argument1.toLowerCase().contains(" "+key.toLowerCase().trim()+" ")) {
						slotFillValue = key;
						break;
					}
				}
			}



		}else if(slotType.equals("DATE")) {

			DateNormalizer_Old dateNormalizer = new DateNormalizer_Old();
			List<String> dateRegex = dateNormalizer.getDateRegex();
			/*for (String string : dateRegex) {
				Pattern datePattern = Pattern.compile(string);
				Matcher matcher = datePattern.matcher(input)
			}*/

			if(slotFillIn.equals("arg2")) {

				String argument2 = " "+outputBO.getOpenIEExtractionBO().getArgument2().toLowerCase()+" ";
				for (String string : dateRegex) {
					Pattern datePattern = Pattern.compile(string);
					Matcher matcher = datePattern.matcher(argument2);

					if(matcher.find()) {
						int start = matcher.start();
						int end = matcher.end();

						slotFillValue = argument2.substring(start, end);
						break;
					}

				}

			}else if(slotFillIn.equals("arg1")) {
				String argument1 = " "+outputBO.getOpenIEExtractionBO().getArgument1().toLowerCase()+" ";
				for (String string : dateRegex) {
					Pattern datePattern = Pattern.compile(string);
					Matcher matcher = datePattern.matcher(argument1);

					if(matcher.find()) {
						int start = matcher.start();
						int end = matcher.end();

						slotFillValue = argument1.substring(start, end);
						break;
					}

				}
			}else if(slotFillIn.equals("relation")) {
				String relationTerm = " "+outputBO.getOpenIEExtractionBO().getRelation().toLowerCase()+" ";
				for (String string : dateRegex) {
					Pattern datePattern = Pattern.compile(string);
					Matcher matcher = datePattern.matcher(relationTerm);

					if(matcher.find()) {
						int start = matcher.start();
						int end = matcher.end();

						slotFillValue = relationTerm.substring(start, end);
						break;
					}

				}
			}
			if(slotFillValue.length() > 0) {
				Temporal normalizedDate = suTimeExtractor.getNormalizedDate(slotType, "2014-9-27");
				slotFillValue = normalizedDate.toString();
			}

		}else if(slotType.equals("INTEGER")) {

			List<String> integerPattern = new ArrayList<String>();
			integerPattern.add("\\s+\\d+,?.?\\s+");
			integerPattern.add("(\\s+one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty|thirty|fourty|fifty|sixty|seventy|eighty|ninety|hundred|thousand|million|billion|trillion|quadrillion)\\s+");

			if(slotFillIn.equals("arg2")) {

				String argument2 = " "+outputBO.getOpenIEExtractionBO().getArgument2().toLowerCase()+" ";
				for (String string : integerPattern) {
					Pattern datePattern = Pattern.compile(string);
					Matcher matcher = datePattern.matcher(argument2);

					if(matcher.find()) {
						int start = matcher.start();
						int end = matcher.end();

						slotFillValue = argument2.substring(start, end);
						break;
					}

				}

			}else if(slotFillIn.equals("arg1")) {
				String argument1 = " "+outputBO.getOpenIEExtractionBO().getArgument1().toLowerCase()+" ";
				for (String string : integerPattern) {
					Pattern datePattern = Pattern.compile(string);
					Matcher matcher = datePattern.matcher(argument1);

					if(matcher.find()) {
						int start = matcher.start();
						int end = matcher.end();

						slotFillValue = argument1.substring(start, end);
						break;
					}

				}
			}else if(slotFillIn.equals("relation")) {
				String relationTerm = " "+outputBO.getOpenIEExtractionBO().getRelation().toLowerCase()+" ";
				for (String string : integerPattern) {
					Pattern datePattern = Pattern.compile(string);
					Matcher matcher = datePattern.matcher(relationTerm);

					if(matcher.find()) {
						int start = matcher.start();
						int end = matcher.end();

						slotFillValue = relationTerm.substring(start, end);
						break;
					}

				}
			}


		}else if(slotFillIn.equals("arg1")) {
			String argument1 = " "+outputBO.getOpenIEExtractionBO().getArgument1().toLowerCase()+" ";
			List<String> list = keywordTagger.getList(PatternConstantEnumKBP.fromValue(slotType.trim()));

			for (String string : list) {
				if(argument1.contains(" "+string.toLowerCase()+" ")) {
					slotFillValue = string;
					break;
				}
			}

		}else if(slotFillIn.equals("arg2")) {
			String argument2 = " "+outputBO.getOpenIEExtractionBO().getArgument2().toLowerCase()+" ";
			List<String> list = keywordTagger.getList(PatternConstantEnumKBP.fromValue(slotType.trim()));

			for (String string : list) {
				if(argument2.contains(" "+string.toLowerCase()+" ")) {
					slotFillValue = string;
					break;
				}
			}
		} else if(slotFillIn.equals("relation")) {

			String relationTerm = " "+outputBO.getOpenIEExtractionBO().getRelation().toLowerCase()+" ";
			List<String> list = keywordTagger.getList(PatternConstantEnumKBP.fromValue(slotType));

			for (String string : list) {
				if(relationTerm.contains(" "+string.toLowerCase()+" ")) {
					slotFillValue = string;
					break;
				}
			}
		}

		return slotFillValue;
	}
	public static String getSlotFill2(KBPRelationClassificationOutputBO outputBO, SentenceAnnotationBO sentenceAnnotationBO) {

		String slotFillValue = "";

		RelationPattern pattern = outputBO.getPattern();
		String slotType = pattern.getSlotType();
		String slotFillIn = pattern.getSlotFillIn();

		if(slotFillIn.equals("arg1")) {

			String normalizedArgument1 = " "+outputBO.getNormalizedOpenIEExtractionBO().getArgument1()+" ";
			String argument1 = " "+outputBO.getOpenIEExtractionBO().getArgument1().toLowerCase()+" ";

			int startIndex = normalizedArgument1.indexOf(slotType);

			String prevToken= " ",nextToken = " ";
			//Get the previous token
			if(startIndex > 0) {
				String[] normalizedArgument1Split = normalizedArgument1.trim().split(" ");
				for (int i=0;i<normalizedArgument1Split.length;i++) {
					if(normalizedArgument1Split[i].equals(slotType)) {
						if(i>0)
							prevToken = " "+normalizedArgument1Split[i-1]+" ";
						if(i<normalizedArgument1Split.length-1)
							nextToken = " "+normalizedArgument1Split[i+1]+" ";
					}
				}

			}

			if(startIndex != -1) {
				if(startIndex+slotType.length() < normalizedArgument1.length()) {

					Pattern regexPattern = Pattern.compile("("+prevToken+")[a-zA-Z0-9 ]{1,30}("+nextToken+")");
					Matcher regexMatcher = regexPattern.matcher(argument1);

					int patternStart=-1, patternEnd = -1;
					if(regexMatcher.find()) {
						patternStart = regexMatcher.start();
						patternEnd = regexMatcher.end();


						slotFillValue=argument1.substring(patternStart+prevToken.length(), patternEnd-nextToken.length());
					}
					/*if(prevToken.length() > 0) {
						startIndex = argument1.indexOf(prevToken)+prevToken.length();
					}

					int endIndex = -1;
					if(nextToken.length() > 0) {
						endIndex = argument1.indexOf(nextToken)-1;
					}

					if(endIndex != -1)
						slotFillValue = argument1.substring(startIndex, endIndex);
					else
						slotFillValue = argument1.substring(startIndex);*/
				} else {
					slotFillValue = argument1.substring(startIndex);
				}
			}

		}else if(slotFillIn.equals("arg2")) {

			String normalizedArgument2 = " "+outputBO.getNormalizedOpenIEExtractionBO().getArgument2()+" ";
			String argument2 = " "+outputBO.getOpenIEExtractionBO().getArgument2().toLowerCase()+" ";

			int startIndex = normalizedArgument2.indexOf(slotType);

			String prevToken= "",nextToken = "";
			//Get the previous token
			if(startIndex > 0) {
				String[] normalizedArgument2Split = normalizedArgument2.split(" ");
				for (int i=0;i<normalizedArgument2Split.length;i++) {
					if(normalizedArgument2Split[i].equals(slotType)) {
						if(i>0)
							prevToken = " "+normalizedArgument2Split[i-1]+" ";
						if(i<normalizedArgument2Split.length-1)
							nextToken = " "+normalizedArgument2Split[i+1]+" ";
						break;
					}
				}

			}

			if(startIndex != -1) {
				if(startIndex+slotType.length() < normalizedArgument2.length()) {

					Pattern regexPattern = Pattern.compile("("+prevToken+")[a-zA-Z0-9 ]{1,30}?("+nextToken+")");
					Matcher regexMatcher = regexPattern.matcher(argument2);

					int patternStart=-1, patternEnd = -1;
					if(regexMatcher.find()) {
						patternStart = regexMatcher.start();
						patternEnd = regexMatcher.end();


						slotFillValue=argument2.substring(patternStart+prevToken.length(), patternEnd-nextToken.length());
					}

					/*if(prevToken.length() > 0) {
						startIndex = argument2.indexOf(prevToken)+prevToken.length();
					}

					int endIndex = -1;
					if(nextToken.length() > 0) {
						endIndex = argument2.indexOf(nextToken)-1;
					}

					if(endIndex != -1)
						slotFillValue = argument2.substring(startIndex, endIndex);
					else
						slotFillValue = argument2.substring(startIndex);*/
				} else {
					slotFillValue = argument2.substring(startIndex);
				}
			}

		}else if(slotFillIn.equals("relation")) {

			String normalizedRelationTerm = " "+outputBO.getNormalizedOpenIEExtractionBO().getRelation()+" ";
			String relationTerm = " "+outputBO.getOpenIEExtractionBO().getRelation().toLowerCase()+" ";

			int startIndex = normalizedRelationTerm.indexOf(slotType);

			String prevToken= "",nextToken = "";
			//Get the previous token
			if(startIndex > 0) {
				String[] relationTermSplit = normalizedRelationTerm.split(" ");
				for (int i=0;i<relationTermSplit.length;i++) {
					if(relationTermSplit[i].equals(slotType)) {
						if(i>0)
							prevToken = " "+relationTermSplit[i-1]+" ";
						if(i<relationTermSplit.length-1)
							nextToken = " "+relationTermSplit[i+1]+" ";
					}
				}

			}

			if(startIndex != -1) {
				if(startIndex+slotType.length() < normalizedRelationTerm.length()) {

					Pattern regexPattern = Pattern.compile("("+prevToken+")[a-zA-Z0-9 ]{1,30}?("+nextToken+")");
					Matcher regexMatcher = regexPattern.matcher(relationTerm);

					int patternStart=-1, patternEnd = -1;
					if(regexMatcher.find()) {
						patternStart = regexMatcher.start();
						patternEnd = regexMatcher.end();


						slotFillValue=relationTerm.substring(patternStart+prevToken.length(), patternEnd-nextToken.length());
					}

					/*if(prevToken.length() > 0) {
						startIndex = relationTerm.indexOf(prevToken)+prevToken.length();
					}

					int endIndex = -1;
					if(nextToken.length() > 0) {
						endIndex = relationTerm.indexOf(nextToken)-1;
					}

					if(endIndex != -1)
						slotFillValue = relationTerm.substring(startIndex, endIndex);
					else
						slotFillValue = relationTerm.substring(startIndex);*/
				} else {
					slotFillValue = relationTerm.substring(startIndex);
				}
			}


		}
		return slotFillValue;
	}
}
