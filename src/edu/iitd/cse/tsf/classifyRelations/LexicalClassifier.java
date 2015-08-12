package edu.iitd.cse.tsf.classifyRelations;

import java.io.FileInputStream;
import java.io.InputStream;
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

import edu.iitd.cse.tsf.annotation.TextAnnotatorKBP;
import edu.iitd.cse.tsf.constants.KBPRelationTypeEnum;
import edu.iitd.cse.tsf.constants.PatternConstantEnumKBP;
import edu.iitd.cse.tsf.models.KBPRelationClassificationOutputBO;
import edu.iitd.cse.tsf.models.QueryBO;
import edu.iitd.cse.tsf.models.RelationPattern;
import edu.iitd.cse.tsf.models.SentenceAnnotationBO;
import edu.iitd.cse.tsf.pattern_Old.KeywordTagger_Old;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.pipeline.Annotation;



/**
 * @author abhishek
 *
 */
public class LexicalClassifier {

	KeywordTagger_Old keywordTagger;

	Properties properties;


/*	public LexicalClassifier() {
		super();
		// TODO Auto-generated constructor stub

		keywordTagger = new KeywordTagger();

		properties = new Properties();
		InputStream inputStream = null;
		try {inputStream = new FileInputStream("Resources/KBP/kbpDefault.properties");
		properties.load(inputStream);

		//Loading lists
		keywordTagger.loadCountry(properties.getProperty("countriesFilePath"));
		keywordTagger.loadJobTitles(properties.getProperty("jobTitleFilePath"));
		keywordTagger.loadHeadJobTitles(properties.getProperty("headJobTitleFilePath"));
		keywordTagger.loadCity(properties.getProperty("citiesFilePath"));
		keywordTagger.loadStateOrProvince(properties.getProperty("statesOrProvincesFilePath"));
		keywordTagger.loadNationality(properties.getProperty("nationalitiesFilePath"));
		keywordTagger.loadSchool(properties.getProperty("schoolFilePath"));
		keywordTagger.loadCrime(properties.getProperty("crimesFilePath"));
		} catch(Exception e) {
			e.printStackTrace();
		}

	}*/

	public LexicalClassifier(String propertyFilePath) {
		super();
		// TODO Auto-generated constructor stub

		keywordTagger = new KeywordTagger_Old();

		properties = new Properties();
		InputStream inputStream = null;
		try {inputStream = new FileInputStream(propertyFilePath);
		properties.load(inputStream);

		//Loading lists
		keywordTagger.loadCountry(properties.getProperty("countriesFilePath"));
		keywordTagger.loadJobTitles(properties.getProperty("jobTitleFilePath"));
		keywordTagger.loadHeadJobTitles(properties.getProperty("headJobTitleFilePath"));
		keywordTagger.loadCity(properties.getProperty("citiesFilePath"));
		keywordTagger.loadStateOrProvince(properties.getProperty("statesOrProvincesFilePath"));
		keywordTagger.loadNationality(properties.getProperty("nationalitiesFilePath"));
		keywordTagger.loadSchool(properties.getProperty("schoolFilePath"));
		keywordTagger.loadCrime(properties.getProperty("crimesFilePath"));
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	public Map<String, String> getResultsFromPattern(QueryBO queryBO, SentenceAnnotationBO sentenceAnnotationBO,Set<String> aliases) {

		Map<String,String> outpMap = new HashMap<String, String>();
		//Map<String, String> nerMap = sentenceAnnotationBO.getNerMap();
		//String sentence = sentenceAnnotationBO.getSentence();

		/*String normalizedString = "";
		if(nerMap != null && nerMap.size() > 0) {
			normalizedString = keywordTagger.normalizeNERs(sentence, nerMap);
		}
		normalizedString = keywordTagger.normalizeString(normalizedString);*/

		if(queryBO.getRelationTypeEnum() == KBPRelationTypeEnum.ORGANIZATION) {

			/*Map<String, String> matchPatternOrg2Map = matchPatternOrg2(normalizedString, queryBO, sentenceAnnotationBO);
			outpMap.putAll(matchPatternOrg2Map);*/

			Map<String, String> matchPatternOrg3Map = matchPatternOrg3(queryBO, sentenceAnnotationBO,aliases);
			outpMap.putAll(matchPatternOrg3Map);
		}

		if(queryBO.getRelationTypeEnum() == KBPRelationTypeEnum.PERSON) {

			Map<String, String> matchPatternPer1Map = matchPatternPer1(queryBO, sentenceAnnotationBO,aliases);
			if(matchPatternPer1Map != null) {
				outpMap.putAll(matchPatternPer1Map);
			}

			Map<String, String> matchPatternPer2Map = matchPatternPer2(queryBO, sentenceAnnotationBO, aliases);
			if(matchPatternPer2Map != null) {
				outpMap.putAll(matchPatternPer2Map);
			}
		}

		return outpMap;

	}


	public Map<String,String> matchPatternOrg2(String normalizedstring, QueryBO queryBO, SentenceAnnotationBO sentenceAnnotationBO) {

		if(queryBO.getRelationTypeEnum() != KBPRelationTypeEnum.ORGANIZATION)
			return null;

		String regex = "ORGANIZATION in CITY";

		Map<String,String> outputMap = new HashMap<String, String>();

		String originalText = " "+sentenceAnnotationBO.getSentence()+" ";
		Map<String, String> nerMap = sentenceAnnotationBO.getNerMap();

		String organizationValue = "";
		String cityValue = "";


		if(normalizedstring.contains(regex)) {


			//getting organization
			for (Entry<String,String> personEntry : nerMap.entrySet()) {
				String key = personEntry.getKey();
				String value = personEntry.getValue();
				if(value.equalsIgnoreCase("ORGANIZATION") && originalText.toLowerCase().contains(" "+key.toLowerCase().trim()+" ")) {
					organizationValue = key;
					break;
				}
			}

			//Getting city
			List<String> list = keywordTagger.getList(PatternConstantEnumKBP.CITY);

			for (String string : list) {
				if(originalText.contains(" "+string.toLowerCase()+" ")) {
					cityValue = string;
					break;
				}
			}

			outputMap.put("org:city_of_headquarters", cityValue);


		}
		return outputMap;


	}

	public Map<String,String> matchPatternOrg3(QueryBO queryBO, SentenceAnnotationBO sentenceAnnotationBO, Set<String> aliases) {

		if(queryBO.getRelationTypeEnum() != KBPRelationTypeEnum.ORGANIZATION)
			return null;

		/*String regex = "ORGANIZATION \\(\\s?[a-zA-Z0-9\\.?]+\\s?([a-zA-Z0-9\\.?]+)?\\s?\\)";

		Map<String,String> outputMap = new HashMap<String, String>();

		String originalText = " "+sentenceAnnotationBO.getSentence()+" ";
		Map<String, String> nerMap = sentenceAnnotationBO.getNerMap();

		String organizationValue = "";
		String alternateName = "";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(normalizedstring);

		if(matcher.find()) {


			//getting organization
			for (Entry<String,String> personEntry : nerMap.entrySet()) {
				String key = personEntry.getKey();
				String value = personEntry.getValue();
				if(value.equalsIgnoreCase("ORGANIZATION") && originalText.toLowerCase().contains(" "+key.toLowerCase().trim()+" ")) {
					organizationValue = key;
					break;
				}
			}

			//Getting the alternate name
			Pattern pattern2 = Pattern.compile(organizationValue+" \\(\\s?[a-zA-Z0-9\\.?]+\\s?([a-zA-Z0-9\\.?]+)?\\s?\\)");
			Matcher matcher2 = pattern2.matcher(originalText);

			if(matcher2.find()) {
				alternateName = originalText.substring(matcher2.start()+organizationValue.length()+2, matcher2.end()-1);
			}
			if(alternateName.length() > 0) {
				outputMap.put("alternate_name", alternateName);
			}


		}*/


		Map<String,String> outputMap = new HashMap<String, String>();

		//Remove for loop comment to to add aliases in apttern to find more aliases
		String aliasString = queryBO.getEntity();
		//for (String aliasString : aliases) {

			String regex = aliasString.toLowerCase()+" \\(\\s?[a-zA-Z0-9\\.?]+\\s?([a-zA-Z0-9\\.?]+)?\\s?\\)";
			String originalText = " "+sentenceAnnotationBO.getSentence().toLowerCase()+" ";

			String alternateName = "";

			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(originalText);

			if(matcher.find()) {

				alternateName = originalText.substring(matcher.start()+aliasString.length()+2, matcher.end()-1);
				//Getting the alternate name
				//Pattern pattern2 = Pattern.compile(organizationValue+" \\(\\s?[a-zA-Z0-9\\.?]+\\s?([a-zA-Z0-9\\.?]+)?\\s?\\)");
				//Matcher matcher2 = pattern2.matcher(originalText)

				if(alternateName.length() > 0) 
					outputMap.put("org:alternate_names", alternateName);
			}

		//}
		return outputMap;
	}

	public Map<String, String> matchPatternPer1( QueryBO queryBO, SentenceAnnotationBO sentenceAnnotationBO, Set<String> aliases) {

		if(queryBO.getRelationTypeEnum() != KBPRelationTypeEnum.PERSON)
			return null;

		/*String regex = "PERSON\\s?,\\s?[0-9]{2}\\s?,";

		Map<String,String> outputMap = new HashMap<String, String>();

		String originalText = " "+sentenceAnnotationBO.getSentence()+" ";
		Map<String, String> nerMap = sentenceAnnotationBO.getNerMap();

		String personNormalizedString = originalText.trim().toLowerCase();
		for (Entry<String, String> entry : nerMap.entrySet()) {

			String key = entry.getKey();
			String value = entry.getValue();

			if(value.equals("PERSON") && personNormalizedString.contains(key)) {
				personNormalizedString = personNormalizedString.replace(key, value);
			}

		}


		String personValue = "";
		String age = "";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(personNormalizedString);
		if(matcher.find()) {
			String personSubstring = personNormalizedString.substring(matcher.start(), matcher.end());

			Pattern pattern2 = Pattern.compile("[0-9]+");
			Matcher matcher2 = pattern2.matcher(personSubstring);
			if(matcher2.find()) {
				age = personSubstring.substring(matcher2.start(), matcher2.end());
			}
		}
		if(age.length() > 0) {
			outputMap.put("age", age);
		}*/


		Map<String,String> outputMap = new HashMap<String, String>();

		for (String aliasString : aliases) {

			String regex = aliasString.toLowerCase()+"\\s?,\\s?[0-9]{2}\\s?,";
			String originalText = " "+sentenceAnnotationBO.getSentence().toLowerCase()+" ";



			String age = "";

			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(originalText);
			if(matcher.find()) {

				String matched = originalText.substring(matcher.start(), matcher.end());
				Pattern pattern2 = Pattern.compile("[0-9]{2}");
				Matcher matcher2 = pattern2.matcher(matched);
				if(matcher2.find()) {
					age = matched.substring(matcher2.start(), matcher2.end());
				}
			}
			if(age.length() > 0) {
				outputMap.put("per:age", age);
			}

		}

		return outputMap;
	}


	public Map<String, String> matchPatternPer2(QueryBO queryBO, SentenceAnnotationBO sentenceAnnotationBO, Set<String> aliases) {

		//For patter Title entity

		Map<String, String> outputMap = new HashMap<String, String>();

		if(queryBO.getRelationTypeEnum() != KBPRelationTypeEnum.PERSON)
			return null;

		List<CoreLabel> tokenList = sentenceAnnotationBO.getTokenList();
		List<String> tokenArrayList = new ArrayList<String>();

		int index=0;
		int entityIndex = -1;
		for (CoreLabel token : tokenList) {
			String string = token.get(TextAnnotation.class);
			tokenArrayList.add(string);
			if(aliases.contains(string.toLowerCase())) {
				entityIndex = index;
				break;
			}
			index++;
		}

		String oneWordBefore = "",twoWordBefore = "",threeWordBefore = "";
		if((entityIndex-1) >= 0)
			oneWordBefore = tokenArrayList.get(entityIndex-1);

		if((entityIndex-2) >= 0)
			twoWordBefore = tokenArrayList.get(entityIndex-2)+" "+tokenArrayList.get(entityIndex-1);

		if((entityIndex-3) >= 0)
			threeWordBefore = tokenArrayList.get(entityIndex-3)+tokenArrayList.get(entityIndex-2)+" "+tokenArrayList.get(entityIndex-1);

		List<String> titleList = keywordTagger.getList(PatternConstantEnumKBP.HEADJOBTITLE);

		String title = "";
		if(threeWordBefore.length() > 0 && titleList.contains(threeWordBefore.toLowerCase())) {
			title = threeWordBefore;
		} else if(twoWordBefore.length() > 0 && titleList.contains(twoWordBefore.toLowerCase())) {
			title = twoWordBefore;
		} else if(oneWordBefore.length() > 0 && titleList.contains(oneWordBefore.toLowerCase())) {
			title = oneWordBefore;
		}

		if(title.length() > 0) {
			outputMap.put("per:title", title);
		}

		return outputMap;
	}

	public static void main3(String args[]) {

		/*LexicalClassifier lexicalClassifier = new LexicalClassifier();
		String s = "John, 92, is the CEO of Google (go.)";
		QueryBO queryBO = new QueryBO();
		queryBO.setRelationTypeEnum(KBPRelationTypeEnum.PERSON);

		SentenceAnnotationBO sentenceAnnotationBO = new SentenceAnnotationBO();
		sentenceAnnotationBO.setSentence(s);

		Map<String, String> nerMap = new HashMap<String,String>();
		nerMap.put("John", "PERSON");
		nerMap.put("Google", "ORGANIZATION");

		String normalizedString = lexicalClassifier.keywordTagger.normalizeNERs(s, nerMap);
		sentenceAnnotationBO.setNerMap(nerMap);*/

		//Map<String, String> matchPatternPer1 = lexicalClassifier.matchPatternPer1(queryBO, sentenceAnnotationBO);
		//System.out.println(matchPatternPer1.get("age"));
		LexicalClassifier classifier = new LexicalClassifier("Resources/KBP/kbpDefault.properties");

		String s = "prime minister Narendra Modi visited Japan";
		TextAnnotatorKBP annotator = new TextAnnotatorKBP();
		List<SentenceAnnotationBO> list = annotator.process(s);
		Set<String> aliaseSet = new HashSet<String>();
		aliaseSet.add("narendra modi");
		aliaseSet.add("narendra");
		aliaseSet.add("modi");

		QueryBO queryBO = new QueryBO();
		queryBO.setRelationTypeEnum(KBPRelationTypeEnum.PERSON);

		for (SentenceAnnotationBO sentenceAnnotationBO : list) {
			Map<String, String> matchPatternPer2 = classifier.matchPatternPer2(queryBO, sentenceAnnotationBO, aliaseSet);
			for (Entry<String, String> entry : matchPatternPer2.entrySet()) {
				System.out.println(entry.getValue());
			}
		}


	}
}
