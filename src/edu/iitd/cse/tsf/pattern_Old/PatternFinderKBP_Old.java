/**
 * 
 */
package edu.iitd.cse.tsf.pattern_Old;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.spec.PSSParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;

import org.ho.yaml.Yaml;

import edu.iitd.cse.tsf.constants.KBPRelationTypeEnum;
import edu.iitd.cse.tsf.constants.OpenIEExtractionBOKBP;
import edu.iitd.cse.tsf.constants.PatternConstantEnumKBP;
import edu.iitd.cse.tsf.constants.TemporalTypeEnumKBP;
import edu.iitd.cse.tsf.models.KBPRelationClassificationOutputBO;
import edu.iitd.cse.tsf.models.RelationPattern;

/**
 * @author mcs132540
 *
 */
public class PatternFinderKBP_Old {

	private List<RelationPattern> personPatternList;
	private List<RelationPattern> organizationPatternList;
	private List<RelationPattern> othersPatternList;
	private List<RelationPattern> allPatternsList;

	private KeywordTagger_Old keywordTagger;

	private DateNormalizer_Old dateNormalizer;

	//Added to handle NER
	/*StanfordCoreNLP stanfordPipeline;
	Properties stanfordProperties;*/

	/**
	 * 
	 */
	public PatternFinderKBP_Old() {
		super();
		// TODO Auto-generated constructor stub
		personPatternList = new ArrayList<RelationPattern>();		
		organizationPatternList = new ArrayList<RelationPattern>();
		allPatternsList = new ArrayList<RelationPattern>();
		
		keywordTagger = new KeywordTagger_Old();
		dateNormalizer = new DateNormalizer_Old();
		/*startPatterns = new ArrayList<Pattern>();
		endPatterns = new ArrayList<Pattern>();
		holdPatterns = new ArrayList<Pattern>();
		rangePatterns = new ArrayList<Pattern>();*/

		//Also load Stanford CoreNLP
		/*stanfordProperties = new Properties();
				stanfordProperties.put("annotators", "tokenize,ssplit,pos,lemma,ner,parse,dcoref");
				stanfordPipeline = new StanfordCoreNLP(stanfordProperties);*/
	}

	/**
	 * This method loads list of titles from a file
	 * @throws FileNotFoundException 
	 * 
	 * */
	/*public void loadTitles(String path) throws FileNotFoundException {
		InputStream inputStream = new FileInputStream(new File(path));
		titleList = (ArrayList<String>)Yaml.load(inputStream);

	}*/

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadJobTitles(String path) throws FileNotFoundException {
		/*InputStream inputStream = new FileInputStream(new File(path));
		jobTitileList = (ArrayList<String>)Yaml.load(inputStream);*/
		keywordTagger.loadJobTitles(path);

	}

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadHeadJobTitles(String path) throws FileNotFoundException {
		/*InputStream inputStream = new FileInputStream(new File(path));
		headJobTitle = (ArrayList<String>)Yaml.load(inputStream);*/
		keywordTagger.loadHeadJobTitles(path);

	}

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadCountry(String path) throws FileNotFoundException {
		/*InputStream inputStream = new FileInputStream(new File(path));
		countryList = (ArrayList<String>)Yaml.load(inputStream);*/
		keywordTagger.loadCountry(path);

	}

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadCity(String path) throws FileNotFoundException {
		/*InputStream inputStream = new FileInputStream(new File(path));
		cityList = (ArrayList<String>)Yaml.load(inputStream);*/
		keywordTagger.loadCity(path);
	}

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadStateOrProvince(String path) throws FileNotFoundException {
		/*InputStream inputStream = new FileInputStream(new File(path));
		stateorprovinceList = (ArrayList<String>)Yaml.load(inputStream);*/
		keywordTagger.loadStateOrProvince(path);
	}

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadNationality(String path) throws FileNotFoundException {
		/*InputStream inputStream = new FileInputStream(new File(path));
		nationalityList = (ArrayList<String>)Yaml.load(inputStream);*/
		keywordTagger.loadNationality(path);

	}

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadSchool(String path) throws FileNotFoundException {
		/*InputStream inputStream = new FileInputStream(new File(path));
		schoolList = (ArrayList<String>)Yaml.load(inputStream);*/
		keywordTagger.loadSchool(path);
	}

	public void loadCrime(String path) throws FileNotFoundException {
		/*InputStream inputStream = new FileInputStream(new File(path));
		crimeList = (ArrayList<String>)Yaml.load(inputStream);*/
		keywordTagger.loadCrime(path);
	}
	
	public void loadProduct(String path) throws FileNotFoundException {
		/*InputStream inputStream = new FileInputStream(new File(path));
		crimeList = (ArrayList<String>)Yaml.load(inputStream);*/
		keywordTagger.loadProduct(path);
	}
	
	public void loadTechnology(String path) throws FileNotFoundException {
		/*InputStream inputStream = new FileInputStream(new File(path));
		crimeList = (ArrayList<String>)Yaml.load(inputStream);*/
		keywordTagger.loadTechnology(path);
	}
	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadPersonPattern (String path) throws FileNotFoundException {
		personPatternList = loadPatternList(path);
	}

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadOrganizationPattern(String path) throws FileNotFoundException {
		organizationPatternList = loadPatternList(path);
	}
	
	public void loadOthersPattern(String path) throws FileNotFoundException {
		othersPatternList = loadPatternList(path);
	}
	
	public void loadAllPatterns(String path) throws FileNotFoundException {
		allPatternsList = loadPatternList(path);
	}

	public List<RelationPattern> loadPatternList(String path) throws FileNotFoundException {

		List<RelationPattern> patternList = new ArrayList<RelationPattern>();
		List<String> patternStringList;

		InputStream inputStream = new FileInputStream(new File(path));
		patternStringList = (ArrayList<String>)Yaml.load(inputStream);

		for (String string : patternStringList) {

			RelationPattern pattern = new RelationPattern();
			String[] patternSplits = string.split("\t");

			pattern.setRelationType(patternSplits[0]);
			pattern.setMaxSlotFillValues(Integer.parseInt(patternSplits[1]));
			pattern.setRelationTerm(patternSplits[2]);
			pattern.setArg2Begin(patternSplits[3]);
			pattern.setEntityIn(patternSplits[4]);
			pattern.setSlotFillIn(patternSplits[5]);
			pattern.setSlotType(patternSplits[6]);
			pattern.setArg1Term(patternSplits[7]);
			pattern.setArg2Term(patternSplits[8]);

			patternList.add(pattern);

		}

		return patternList;
	}
	/**
	 * This functions iteratively tries to match the openIE output for each temporal type 
	 * of a relation and returns the first match temporal type
	 *  
	 * */
	/*public TemporalTypeEnum getRelationTemporalType(OpenIEExtractionBO openIEExtractionBO, Map<String,String> nerOutput) {

		//Running the Stanford pipeline
		//Map<String,String> nerOutput = performNER(openIEExtractionBO.getOriginalText());

		TemporalTypeEnum temporalTypeEnum = TemporalTypeEnum.Irrelevant;
		for (Entry<TemporalTypeEnum, List<Pattern>> typeIterator : relationPatterns.entrySet()) {

			List<Pattern> patternList = typeIterator.getValue();
			boolean isPatternMatching = matchPatterns(openIEExtractionBO, patternList, nerOutput);
			if(isPatternMatching) {
				temporalTypeEnum = typeIterator.getKey();
				break;
			}
		}
		return temporalTypeEnum;
	}*/


	/**
	 * Matches the openIEExtractions with all the patterns
	 * 
	 * */
	public List<KBPRelationClassificationOutputBO> classifyOpenIEExtractionForAll(OpenIEExtractionBOKBP openIEExtractionBO, Map<String,String> nerOutput) {
		
		List<KBPRelationClassificationOutputBO> classificationOutputBOs = matchPatterns(openIEExtractionBO, allPatternsList, nerOutput);
		
		
		return classificationOutputBOs;
		
	}

	public List<KBPRelationClassificationOutputBO> classifyOpenIEExtraction(OpenIEExtractionBOKBP openIEExtractionBO, Map<String,String> nerOutput,KBPRelationTypeEnum relationTypeEnum) {

		List<KBPRelationClassificationOutputBO> classificationOutputBOs = null;

		//Match patterns for person relation
		if(relationTypeEnum == KBPRelationTypeEnum.PERSON) {
			classificationOutputBOs = matchPatterns(openIEExtractionBO, personPatternList, nerOutput);
			if(classificationOutputBOs != null && classificationOutputBOs.size() > 0) {
				for (KBPRelationClassificationOutputBO classificationOutputBO : classificationOutputBOs) {
					classificationOutputBO.setKbpRelationTypeEnum(KBPRelationTypeEnum.PERSON);
				}
			}
		}else if(relationTypeEnum == KBPRelationTypeEnum.ORGANIZATION) {
			classificationOutputBOs = matchPatterns(openIEExtractionBO, organizationPatternList, nerOutput);
			if(classificationOutputBOs != null && classificationOutputBOs.size() > 0) {

				for (KBPRelationClassificationOutputBO classificationOutputBO : classificationOutputBOs) {
					classificationOutputBO.setKbpRelationTypeEnum(KBPRelationTypeEnum.ORGANIZATION);
				}
			}
		}else {
			classificationOutputBOs = matchPatterns(openIEExtractionBO, othersPatternList, nerOutput);
			if(classificationOutputBOs != null && classificationOutputBOs.size() > 0) {

				for (KBPRelationClassificationOutputBO classificationOutputBO : classificationOutputBOs) {
					classificationOutputBO.setKbpRelationTypeEnum(KBPRelationTypeEnum.OTHERS);
				}
			}
		}

		return classificationOutputBOs;
	}
	/**
	 * 
	 * The the KBPRelationClassificationBO if there is a pattern match else returns null
	 * @param openIEExtractionBO
	 * @param patternList
	 * @param nerOutput 
	 * @return
	 * Currently com
	 */
	private List<KBPRelationClassificationOutputBO> matchPatterns(OpenIEExtractionBOKBP openIEExtractionBO,
			List<RelationPattern> patternList, Map<String, String> nerOutput) {
		// TODO Auto-generated method stub

		List<KBPRelationClassificationOutputBO> outputBOs = new ArrayList<KBPRelationClassificationOutputBO>();

		//LowerCase to match pattern with without case and avoid lowercasing of the Added constants to Strings
		String relationTerm = "", argument1 = "",argument2 = "";

		if(openIEExtractionBO.getRelation() != null)
			relationTerm = openIEExtractionBO.getRelation().toLowerCase();

		if(openIEExtractionBO.getArgument2() != null)
			argument2 = openIEExtractionBO.getArgument2().toLowerCase();

		if(openIEExtractionBO.getArgument1() != null)
			argument1 = openIEExtractionBO.getArgument1().toLowerCase();



		//Keyword normalization of relation term
		String normalizedRelationTerm = " "+relationTerm+" ";
		normalizedRelationTerm = dateNormalizer.normalizeDate(normalizedRelationTerm);
		normalizedRelationTerm = keywordTagger.normalizeNERs(normalizedRelationTerm, nerOutput);
		normalizedRelationTerm = keywordTagger.normalizeString(normalizedRelationTerm);

		//Keyword normalization of second argument
		String normalizedSecondArgument = " "+argument2+" ";
		normalizedSecondArgument = dateNormalizer.normalizeDate(normalizedSecondArgument);
		normalizedSecondArgument = keywordTagger.normalizeNERs(normalizedSecondArgument, nerOutput);
		normalizedSecondArgument = keywordTagger.normalizeString(normalizedSecondArgument);

		//Keyword normalization of first argument
		String normalizedFirstArgument = " "+argument1+" ";
		normalizedFirstArgument = dateNormalizer.normalizeDate(normalizedFirstArgument);
		normalizedFirstArgument = keywordTagger.normalizeNERs(normalizedFirstArgument, nerOutput);
		normalizedFirstArgument = keywordTagger.normalizeString(normalizedFirstArgument);

		//Generating the normalized OpenIE extractionBO
		OpenIEExtractionBOKBP normalizedOpenIEExtractionBO = new OpenIEExtractionBOKBP();
		normalizedOpenIEExtractionBO.setArgument1(normalizedFirstArgument);
		normalizedOpenIEExtractionBO.setArgument2(normalizedSecondArgument);
		normalizedOpenIEExtractionBO.setRelation(normalizedRelationTerm);

		for (RelationPattern pattern : patternList) {

			boolean isSecondArgMatching = false,isRelationMatching = false, doesArg2ContainsPattern = false, isSlotFillMatching = false;
			//Matching the 2nd argument of openIE with the Arg2Begin pattern
			//boolean isArg2Matching = matchArgument2(pattern.getArg2Begin(), argument2, nerOutput);

			//Matching the Second argument 
			isSecondArgMatching = matchSecondArgument(pattern.getArg2Begin(), normalizedSecondArgument);

			if(isSecondArgMatching)
				isRelationMatching = matchRelation(pattern.getRelationTerm(), normalizedRelationTerm);

			if(isRelationMatching && isSecondArgMatching)
				doesArg2ContainsPattern = secondArgumentContains(pattern.getArg2Term(), normalizedSecondArgument);

			if(isSecondArgMatching && isRelationMatching && doesArg2ContainsPattern) {

				//To check wether the required argument contains the required slotType
				if(pattern.getSlotFillIn().equalsIgnoreCase("arg1")) {
					isSlotFillMatching = containsSlotType(pattern.getSlotType(),normalizedFirstArgument);
				}else if(pattern.getSlotFillIn().equalsIgnoreCase("arg2")) {
					isSlotFillMatching = containsSlotType(pattern.getSlotType(),normalizedSecondArgument);
				}else if (pattern.getSlotFillIn().equalsIgnoreCase("relation")) {
					isSlotFillMatching = containsSlotType(pattern.getSlotType(),normalizedRelationTerm);
				}
			}
			//Checks for pattern in relation and the second argument
			if(isSecondArgMatching && isRelationMatching && doesArg2ContainsPattern && isSlotFillMatching) {

				KBPRelationClassificationOutputBO outputBO = new KBPRelationClassificationOutputBO();
				outputBO.setOpenIEExtractionBO(openIEExtractionBO);
				outputBO.setPattern(pattern);
				outputBO.setNormalizedOpenIEExtractionBO(normalizedOpenIEExtractionBO);

				//return outputBO;
				outputBOs.add(outputBO);
			}
		}

		if(normalizedFirstArgument.contains(PatternConstantEnumKBP.HEADJOBTITLE.toString()) || normalizedSecondArgument.contains(PatternConstantEnumKBP.HEADJOBTITLE.toString()) || normalizedRelationTerm.contains(PatternConstantEnumKBP.HEADJOBTITLE.toString())) {

			String normalizedFirstArgumentJobTitle = normalizedFirstArgument.replaceAll(PatternConstantEnumKBP.HEADJOBTITLE.toString(), PatternConstantEnumKBP.JOBTITLE.toString());
			String normalizedSecondArgumentJobTitle = normalizedSecondArgument.replaceAll(PatternConstantEnumKBP.HEADJOBTITLE.toString(), PatternConstantEnumKBP.JOBTITLE.toString());
			String normalizedRelationTermJobTitle = normalizedRelationTerm.replaceAll(PatternConstantEnumKBP.HEADJOBTITLE.toString(), PatternConstantEnumKBP.JOBTITLE.toString());

			OpenIEExtractionBOKBP openIEExtractionBO2 = new OpenIEExtractionBOKBP();
			openIEExtractionBO2.setArgument1(normalizedFirstArgumentJobTitle);
			openIEExtractionBO2.setArgument2(normalizedSecondArgumentJobTitle);
			openIEExtractionBO2.setRelation(normalizedRelationTermJobTitle);


			for (RelationPattern pattern : patternList) {

				boolean isSecondArgMatching = false,isRelationMatching = false, doesArg2ContainsPattern = false, isSlotFillMatching = false;
				//Matching the 2nd argument of openIE with the Arg2Begin pattern
				//boolean isArg2Matching = matchArgument2(pattern.getArg2Begin(), argument2, nerOutput);

				//Matching the Second argument 
				isSecondArgMatching = matchSecondArgument(pattern.getArg2Begin(), normalizedSecondArgumentJobTitle);

				if(isSecondArgMatching)
					isRelationMatching = matchRelation(pattern.getRelationTerm(), normalizedRelationTermJobTitle);

				if(isRelationMatching && isSecondArgMatching)
					doesArg2ContainsPattern = secondArgumentContains(pattern.getArg2Term(), normalizedSecondArgumentJobTitle);

				if(isSecondArgMatching && isRelationMatching && doesArg2ContainsPattern) {

					//To check wether the required argument contains the required slotType
					if(pattern.getSlotFillIn().equalsIgnoreCase("arg1")) {
						isSlotFillMatching = containsSlotType(pattern.getSlotType(),normalizedFirstArgumentJobTitle);
					}else if(pattern.getSlotFillIn().equalsIgnoreCase("arg2")) {
						isSlotFillMatching = containsSlotType(pattern.getSlotType(),normalizedSecondArgumentJobTitle);
					}if (pattern.getSlotFillIn().equalsIgnoreCase("relation")) {
						isSlotFillMatching = containsSlotType(pattern.getSlotType(),normalizedRelationTermJobTitle);
					}
				}
				//Checks for pattern in relation and the second argument
				if(isSecondArgMatching && isRelationMatching && doesArg2ContainsPattern && isSlotFillMatching) {

					KBPRelationClassificationOutputBO outputBO = new KBPRelationClassificationOutputBO();
					outputBO.setOpenIEExtractionBO(openIEExtractionBO);
					outputBO.setPattern(pattern);
					outputBO.setNormalizedOpenIEExtractionBO(openIEExtractionBO2);

					//return outputBO;
					outputBOs.add(outputBO);
				}
			}

		}

		return outputBOs;
	}



	/**
	 * @param slotType
	 * @param nerOutput 
	 * @param argument1
	 * @return
	 */
	private boolean containsSlotType(String slotType, String normalizedArgument) {

		boolean matchResult = false;

		if(slotType.equals("ANY") || normalizedArgument.contains(slotType))
			matchResult = true;

		return matchResult;
	}

	/**
	 * 
	 * Normalizes and matches the second argument with the pattern
	 * 
	 * */
	private boolean matchSecondArgument(String arg2Begin, String normalizedArgument2) {

		boolean matchResult = false;

		//NONE if the second argument is empty
		if(arg2Begin.equals("NONE") || normalizedArgument2.startsWith(arg2Begin))
			matchResult = true;

		return matchResult;
	}

	/**
	 * Normalizes the relation string and matches with the pattern
	 * 
	 * */
	private boolean matchRelation(String relationPattern, String normalizedRelationString) {

		boolean matchResult = false;

		/*if(relationPattern.equals(normalizedRelationString.trim()))
			matchResult = true;*/
		if((" "+normalizedRelationString+" ").endsWith(" "+relationPattern+" ")) {
			matchResult = true;
		}

		return matchResult;
	}


	/**
	 * Checks if the second argument contains the desired string
	 * 
	 * */
	private boolean secondArgumentContains(String arg2Term, String argument2) {

		boolean matchResult = false;

		if(arg2Term.equals("NONE") || argument2.contains(arg2Term.toLowerCase()))
			matchResult = true;

		return matchResult;
	}

	/**
	 * Normalizes text with NER outputs
	 * PERSON
	 * LOCATION
	 * ORGANIZATION
	 * 
	 * */
	/*private String normalizeNERs(String argument2, Map<String, String> nerOutput) {
		// TODO Auto-generated method stub

		String processedArgument2 = " "+argument2+" ";
		for (Entry<String, String> nerMapEntity : nerOutput.entrySet()) {
			String regex = "\\s+"+nerMapEntity.getKey().toLowerCase()+"\\s+";
			String value = nerMapEntity.getValue();
			//if(processedArgument2.contains(regex)) {
			if(!value.equals("LOCATION")) {
			processedArgument2 = processedArgument2.replaceAll(regex, " "+nerMapEntity.getValue()+" ");

			} else {
				if(countryList.contains(nerMapEntity.getKey())) {
					processedArgument2 = processedArgument2.replaceAll("\\s+"+nerMapEntity.getKey().toLowerCase()+"\\s+", " "+PatternConstantEnum.COUNTRY.toString()+" ");
				} else if(stateorprovinceList.contains(nerMapEntity.getKey().toLowerCase())) {
					processedArgument2 = processedArgument2.replaceAll("\\s+"+nerMapEntity.getKey().toLowerCase()+"\\s+", " "+PatternConstantEnum.STATEORPROVINCE.toString()+" ");
				} else if(cityList.contains(nerMapEntity.getKey().toLowerCase())) {
					processedArgument2 = processedArgument2.replaceAll("\\s+"+nerMapEntity.getKey().toLowerCase()+"\\s+", " "+PatternConstantEnum.CITY.toString()+" ");
				}else {
					processedArgument2 = processedArgument2.replaceAll(regex, " "+nerMapEntity.getValue()+" ");
				}
			}
			//}
		}

		return processedArgument2.trim();
	}*/


	/**
	 * Normalizes the string term to contain the pattern constants
	 * The things normalized are
	 * jobTitle
	 * HeadJobTitle
	 * Country
	 * City
	 * StateorProvince
	 * *//*
	private String normalizeString(String term) {

		//Adding spaces at the end in the relation term
		String processedTerm = " "+term+" ";

		//Normalize Integers
		processedTerm = processedTerm.replaceAll("\\s+\\d+\\s+", " "+PatternConstantEnum.INTEGER.toString()+" ");

		//Normalizing jobTitle in the relation String
		for (String jobTitle : jobTitileList) {
			processedTerm = processedTerm.replaceAll("\\s+"+jobTitle.toLowerCase()+"\\s+", " "+PatternConstantEnum.JOBTITLE.toString()+" ");
		}

		//Normalize headJobTitle in the relation String
		for (String headJobTitle : this.headJobTitle) {
			processedTerm = processedTerm.replaceAll("\\s+"+headJobTitle.toLowerCase()+"\\s+", " "+PatternConstantEnum.HEADJOBTITLE.toString()+" ");
		}

		//Normalize country in the relation string
		for (String country : countryList) {
			processedTerm = processedTerm.replaceAll("\\s+"+country.toLowerCase()+"\\s+", " "+PatternConstantEnum.COUNTRY.toString()+" ");
		}

		//Normalizing city in the relation string
		for (String city : cityList) {
			processedTerm = processedTerm.replaceAll("\\s+"+city.toLowerCase()+"\\s+", " "+PatternConstantEnum.CITY.toString()+" ");
		}

		//Normalizing state or province in the relation String
		for (String stateOrProvince : stateorprovinceList) {
			processedTerm = processedTerm.replaceAll("\\s+"+stateOrProvince.toLowerCase()+"\\s+", " "+PatternConstantEnum.STATEORPROVINCE.toString()+" ");
		}

		//Normalizing nationality
		for (String nationality : nationalityList) {

			processedTerm = processedTerm.replaceAll("\\s+"+nationality.toLowerCase()+"\\s+", " "+PatternConstantEnum.NATIONALITY.toString()+"\\s+");
		}

		//Nomalizing crimesList
		for (String crime : crimeList) {
			processedTerm = processedTerm.replaceAll("\\s+"+crime.toLowerCase()+"\\s+", " "+PatternConstantEnum.CRIME.toString()+"\\s+");
		}

		//Normalizing School List
		for (String school : schoolList) {
			processedTerm = processedTerm.replaceAll("\\s+"+school.toLowerCase()+"\\s+", " "+PatternConstantEnum.SCHOOL.toString()+"\\s+");
		}

		return processedTerm.trim();
	}*/

	/**
	 * @param arg2Begin
	 * @param argument2
	 * @param nerOutput 
	 * @return
	 * 
	 * Currently the matching is based on the words in the relation and the 2nd argument
	 * and not considering the type of the entity in the 2nd argument
	 */
	/*private boolean matchArgument2(String arg2Begin, String argument2, Map<String, String> nerOutput) {

		boolean matchResult = false;
		String[] split = arg2Begin.split(" ");

		//Checks if the last word is an ENUM
		boolean isEnum = isPatternConstantEnum(split[split.length - 1]);

		if(split.length == 1) {
			//Only one word in the arg2Begin patterns
			if(isEnum) {
				//If the only word is an ENUM
				matchResult = matchEnum(PatternConstantEnum.fromValue(split[split.length - 1]), argument2, nerOutput);
				//matchResult = true;
			}else {
				matchResult = argument2.trim().toLowerCase().startsWith(arg2Begin.toLowerCase());
			}
		} else {
			//Multiple words pattern
			if(isEnum) {
				//Last Word is a PatternConstantEnum
				StringBuffer buffer = new StringBuffer();
				//Concating the n-1 words of the split(leaving the Enum)
				for(int i=0;i<split.length - 1;i++) {
					buffer.append(split[i]);
				}
				String stringPattern = buffer.toString();
				matchResult = argument2.trim().toLowerCase().startsWith(stringPattern.toLowerCase());

				if(matchResult) {
					//The stringPattern is matched exactly with the first stringPattern.length() characters and hence Enum matching must be 
					//Performed for text after that
					matchResult = matchEnum(PatternConstantEnum.fromValue(split[split.length - 1]), argument2.substring(stringPattern.length()), nerOutput);
				}

			} else {
				matchResult = argument2.trim().toLowerCase().startsWith(arg2Begin.toLowerCase());
			}
		}
		return matchResult;
	}*/

	/**
	 * @param fromValue
	 * @param substring
	 * @param nerOutput 
	 * @return
	 */
	/*private boolean matchEnum(PatternConstantEnum enumValue, String substring, Map<String, String> nerOutput) {
		// TODO Auto-generated method stub
		boolean enumMatchResult = false;
		String text = substring.trim().toLowerCase();

		//Iterating over all the elements in the titlelist and comparing it with the text
		if(enumValue == PatternConstantEnum.TITLE) {
			for (String string : titleList) {
				enumMatchResult = text.startsWith(string.toLowerCase());
				if(enumMatchResult) {
					break;
				}
			}
		} else if(enumValue == PatternConstantEnum.ORGANIZATION) {
			//TODO implement ORGANIZATION Matching
			String firstWord = substring.split(" ")[0];
			if(nerOutput.get(firstWord) != null && nerOutput.get(firstWord).equals("ORGANIZATION"))
				enumMatchResult = true;
			//enumMatchResult = true;
		} else if(enumValue == PatternConstantEnum.PERSON) {
			//TODO implement person matching
			String firstWord = substring.split(" ")[0];
			if(nerOutput.get(firstWord) != null && nerOutput.get(firstWord).equals("PERSON"))
				enumMatchResult = true;
			//enumMatchResult = true;
		} else if(enumValue == PatternConstantEnum.LOCATION) {
			//TODO implement LOCATION matching
			String firstWord = substring.split(" ")[0];
			if(nerOutput.get(firstWord) != null && nerOutput.get(firstWord).equals("LOCATION"))
				enumMatchResult = true;
			//enumMatchResult = true;
		} else if(enumValue == PatternConstantEnum.NONE) {
			//If the arg2Begin in the pattern is NONE then matchResult is TRUE
			enumMatchResult = true;
		}

		return enumMatchResult;
	}*/

	/**
	 * @param string
	 * @return
	 * Returns true if the string is a valid enum else false
	 */
	private boolean isPatternConstantEnum(String string) {
		boolean isEnum;
		try {
			PatternConstantEnumKBP fromValue = PatternConstantEnumKBP.fromValue(string);
			isEnum = true;
		} catch (IllegalArgumentException e) {
			isEnum = false;
		}
		return isEnum;
	}
}
