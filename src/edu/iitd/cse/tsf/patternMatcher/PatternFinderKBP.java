package edu.iitd.cse.tsf.patternMatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ho.yaml.Yaml;

import edu.iitd.cse.tsf.constants.KBPRelationTypeEnum;
import edu.iitd.cse.tsf.constants.OpenIEExtractionBOKBP;
import edu.iitd.cse.tsf.constants.PatternConstantEnumKBP;
import edu.iitd.cse.tsf.models.KBPRelationClassificationOutputBO;
import edu.iitd.cse.tsf.models.RelationPattern;



/**
 * @author abhishek
 *
 */
public class PatternFinderKBP {

	private List<RelationPattern> personPatternList;
	private List<RelationPattern> organizationPatternList;
	private List<RelationPattern> othersPatternList;
	private List<RelationPattern> allPatternsList;



	//Added to handle NER
	/*StanfordCoreNLP stanfordPipeline;
	Properties stanfordProperties;*/

	/**
	 * 
	 */
	public PatternFinderKBP() {
		super();
		// TODO Auto-generated constructor stub
		personPatternList = new ArrayList<RelationPattern>();
		organizationPatternList = new ArrayList<RelationPattern>();
		allPatternsList = new ArrayList<RelationPattern>();
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

	/**
	 * This function load pattern from file.
	 * Yaml is used and the patterns are stored as sequence
	 * the components af the patterns are tab separated.
	 * RelationTerm \t Arg2Begins \t EntityIn \t SlotFillIn \t Slot type
	 * 
	 * */
	/**
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 */
	/**
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	/*	public void loadPatterns(String path) throws FileNotFoundException {

		//Yaml yaml = new Yaml();

		//Here String refers to temporal type "start,end,hold,range" and Object is the list of pattern String
		Map<String,Object> patternMap = null;

		//List<String> patternList;
		InputStream inputStream = new FileInputStream(new File(path));
		patternMap = (HashMap<String,Object>)Yaml.load(inputStream);


		for (Entry<String, Object> patternMapElement : patternMap.entrySet()) {

			//Temporal type are start,end,range, hold
			String temporalType = patternMapElement.getKey();

			//Get the list of all the String patterns
			List<String> stringList = (ArrayList<String>)patternMapElement.getValue();
			List<Pattern> patternList = new ArrayList<Pattern>();

			//Splitting the strings to get the patterns in desired format
			for (String string : stringList) {

				Pattern pattern = new Pattern();
				String[] splits = string.split("\t");

				pattern.setRelationTerm(splits[0]);
				if(splits.length > 1 && !splits[1].equalsIgnoreCase("NONE"))
					pattern.setArg2Begin(splits[1]);

				//Setting whether the entity is in argument1 or argument2
				if(splits[2].equalsIgnoreCase("arg1")) {
					pattern.setEntityInArg1(true);
				} else {
					pattern.setEntityInArg1(false);
				}

				patternList.add(pattern);
			}

			switch (temporalType) {
			case "start":
				//startPatterns = patternList;
				relationPatterns.put(TemporalTypeEnum.Start, patternList);
				break;
			case "end":
				//endPatterns = patternList;
				relationPatterns.put(TemporalTypeEnum.End, patternList);
				break;
			case "range":
				//rangePatterns = patternList;
				relationPatterns.put(TemporalTypeEnum.Range, patternList);
				break;
			case "hold":
				//holdPatterns = patternList;
				relationPatterns.put(TemporalTypeEnum.Hold, patternList);
			default:
				break;
			}
		}
	}*/


	
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
			relationTerm = openIEExtractionBO.getRelation();

		if(openIEExtractionBO.getArgument2() != null)
			argument2 = openIEExtractionBO.getArgument2();

		if(openIEExtractionBO.getArgument1() != null)
			argument1 = openIEExtractionBO.getArgument1();



		//Keyword normalization of relation term
		String normalizedRelationTerm = renormalizeText(relationTerm.trim());

		//Keyword normalization of second argument
		String normalizedSecondArgument = renormalizeText(argument2.trim());

		//Keyword normalization of first argument
		String normalizedFirstArgument = renormalizeText(argument1.trim());


		for (RelationPattern pattern : patternList) {

			boolean isSecondArgMatching = false,isRelationMatching = false, doesArg2ContainsPattern = false, isSlotFillMatching = false;
			//Matching the 2nd argument of openIE with the Arg2Begin pattern
			//boolean isArg2Matching = matchArgument2(pattern.getArg2Begin(), argument2, nerOutput);

			//Matching the Second argument 
			isSecondArgMatching = matchSecondArgument(pattern.getArg2Begin(), normalizedSecondArgument, pattern.getSlotType());

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
				outputBO.setNormalizedOpenIEExtractionBO(openIEExtractionBO);

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
				isSecondArgMatching = matchSecondArgument(pattern.getArg2Begin(), normalizedSecondArgumentJobTitle, pattern.getSlotType());

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
					
					//Modifying the pattern to match the HeadJobTitle
					RelationPattern modifiedPattern = new RelationPattern(pattern);
					modifiedPattern.setSlotType(PatternConstantEnumKBP.HEADJOBTITLE.toString());
					outputBO.setPattern(modifiedPattern);
					
					outputBO.setNormalizedOpenIEExtractionBO(openIEExtractionBO);

					//return outputBO;
					outputBOs.add(outputBO);
				}
			}

		}

		return outputBOs;
	}



	/**
	 * Renormalizes the text
	 * PatternConstant#Num --> PatternConstant
	 * 
	 * */
	private String renormalizeText(String text) {
		// TODO Auto-generated method stub
		
		String renormalizedText = " "+text+" ";
		
		for (PatternConstantEnumKBP patternConstantEnum : PatternConstantEnumKBP.values()) {
			
			Pattern pattern = Pattern.compile("\\s+"+patternConstantEnum.toString()+"#[0-9]+\\s+");
			Matcher matcher = pattern.matcher(renormalizedText);
			
			renormalizedText = matcher.replaceAll(" "+patternConstantEnum+" ");
		}
		return renormalizedText.trim();
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
	private boolean matchSecondArgument(String arg2Begin, String normalizedArgument2, String slotFillType) {

		boolean matchResult = false;

		//NONE if the second argument is empty
		if(arg2Begin.equals("NONE") || normalizedArgument2.startsWith(arg2Begin))
			matchResult = true;
		
		/*if(normalizedArgument2.startsWith(arg2Begin))
			matchResult = true;
		
		if(arg2Begin.equals("NONE") && normalizedArgument2.startsWith(slotFillType.toLowerCase()))
			matchResult = true;*/

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
