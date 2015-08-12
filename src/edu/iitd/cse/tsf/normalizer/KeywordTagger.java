package edu.iitd.cse.tsf.normalizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ho.yaml.Yaml;

import edu.iitd.cse.tsf.constants.PatternConstantEnumKBP;


/**
 * @author abhishek
 *
 */
public class KeywordTagger {

	private List<String> jobTitileList;
	private List<String> headJobTitle;
	private List<String> countryList;
	private List<String> cityList;
	private List<String> stateorprovinceList;
	private List<String> nationalityList;
	private List<String> schoolList;
	private List<String> crimeList;
	private List<String> religionList;
	private List<String> productList;
	private List<String> technologyList;

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadJobTitles(String path) throws FileNotFoundException {
		InputStream inputStream = new FileInputStream(new File(path));
		jobTitileList = (ArrayList<String>)Yaml.load(inputStream);

	}

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadHeadJobTitles(String path) throws FileNotFoundException {
		InputStream inputStream = new FileInputStream(new File(path));
		headJobTitle = (ArrayList<String>)Yaml.load(inputStream);

	}

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadCountry(String path) throws FileNotFoundException {
		InputStream inputStream = new FileInputStream(new File(path));
		countryList = (ArrayList<String>)Yaml.load(inputStream);

	}

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadCity(String path) throws FileNotFoundException {
		InputStream inputStream = new FileInputStream(new File(path));
		cityList = (ArrayList<String>)Yaml.load(inputStream);
	}

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadStateOrProvince(String path) throws FileNotFoundException {
		InputStream inputStream = new FileInputStream(new File(path));
		stateorprovinceList = (ArrayList<String>)Yaml.load(inputStream);
	}

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadNationality(String path) throws FileNotFoundException {
		InputStream inputStream = new FileInputStream(new File(path));
		nationalityList = (ArrayList<String>)Yaml.load(inputStream);

	}

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadSchool(String path) throws FileNotFoundException {
		InputStream inputStream = new FileInputStream(new File(path));
		schoolList = (ArrayList<String>)Yaml.load(inputStream);
	}

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadCrime(String path) throws FileNotFoundException {
		InputStream inputStream = new FileInputStream(new File(path));
		crimeList = (ArrayList<String>)Yaml.load(inputStream);
	}

	public void loadReligion(String path) throws FileNotFoundException {
		InputStream inputStream = new FileInputStream(new File(path));
		religionList = (ArrayList<String>)Yaml.load(inputStream);
	}
	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadProduct(String path) throws FileNotFoundException {
		InputStream inputStream = new FileInputStream(new File(path));
		productList = (ArrayList<String>)Yaml.load(inputStream);
	}

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadTechnology(String path) throws FileNotFoundException {
		InputStream inputStream = new FileInputStream(new File(path));
		technologyList = (ArrayList<String>)Yaml.load(inputStream);
	}

	public List<String> getList(PatternConstantEnumKBP patternConstantEnum) {

		List<String> toReturn;
		switch (patternConstantEnum) {
		case COUNTRY:
			toReturn = countryList;
			break;
		case CRIME:
			toReturn = crimeList;
			break;
		case CITY:
			toReturn = cityList;
			break;
		case HEADJOBTITLE:
			toReturn = headJobTitle;
			break;
		case JOBTITLE:
			toReturn = jobTitileList;
			break;
		case NATIONALITY:
			toReturn = nationalityList;
			break;
		case STATEORPROVINCE:
			toReturn = stateorprovinceList;
			break;
		case SCHOOL:
			toReturn = schoolList;
			break;
		case RELIGION:
			toReturn = religionList;
			break;
		case PRODUCT:
			toReturn = productList;
			break;
		case TECHNOLOGY:
			toReturn = technologyList;
			break;

		default:
			toReturn = null;
			break;
		}
		return toReturn;
	}

	/**
	 * Normalizes text with NER outputs
	 * PERSON
	 * LOCATION
	 * ORGANIZATION
	 * 
	 * */
	public String normalizeNERs(String textArgument, Map<String, String> nerOutput, Map<String,List<String>> normalizationValue) {
		// TODO Auto-generated method stub

		String processedArgument = " "+textArgument.trim()+" ";
		for (Entry<String, String> nerMapEntity : nerOutput.entrySet()) {
			String regex = "\\s?"+nerMapEntity.getKey().toLowerCase()+"\\s?";
			String value = nerMapEntity.getValue();

			//Skip the DATE and MISC Tagged tokens
			if(value.equals("DATE") || value.equals("MISC") || value.equals("NUMBER")) {
				continue;
			}

			//if(processedArgument2.contains(regex)) {
			if(!value.equals("LOCATION")) {

				processedArgument = normalizeStringForPatternValue(nerMapEntity.getValue(), regex, processedArgument, normalizationValue);
				/*Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(processedArgument);

				while(matcher.find()) {
					int patternConstantCount=1;

					//Updating the count
					if(normalizationValue.containsKey(nerMapEntity.getValue())) {
						patternConstantCount = normalizationValue.get(nerMapEntity.getValue()).size()+1;
					}

					processedArgument = matcher.replaceFirst(" "+nerMapEntity.getValue()+"#"+patternConstantCount+" ");
					//System.out.println("----"+matcher.group());

					addValueToNormalizationMap(normalizationValue, nerMapEntity.getValue(), regex.trim());

					matcher = pattern.matcher(processedArgument);
				}*/

				/*processedArgument = processedArgument.replaceAll(regex, " "+nerMapEntity.getValue()+" ");

				//Adding to the normalization value
				addValueToNormalizationMap(normalizationValue, nerMapEntity.getValue(), regex.trim());*/

			} else {
				if(countryList.contains(nerMapEntity.getKey())) {

					processedArgument = normalizeStringForPatternValue(PatternConstantEnumKBP.COUNTRY.toString(), "\\s+[^a-zA-Z0-9 ]*\\Q"+nerMapEntity.getKey().toLowerCase()+"\\E[^a-zA-Z0-9 ]*\\s+", processedArgument, normalizationValue);

					/*processedArgument = processedArgument.replaceAll("[^a-zA-Z0-9]+\\Q"+nerMapEntity.getKey().toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.COUNTRY.toString()+" ");

					//Adding to the Map
					addValueToNormalizationMap(normalizationValue, PatternConstantEnumKBP.COUNTRY.toString(), nerMapEntity.getKey());*/

				} else if(stateorprovinceList.contains(nerMapEntity.getKey().toLowerCase())) {

					processedArgument = normalizeStringForPatternValue(PatternConstantEnumKBP.STATEORPROVINCE.toString(), "\\s+[^a-zA-Z0-9 ]*\\Q"+nerMapEntity.getKey().toLowerCase()+"\\E[^a-zA-Z0-9 ]*\\s+", processedArgument, normalizationValue);

					/*processedArgument = processedArgument.replaceAll("[^a-zA-Z0-9]+\\Q"+nerMapEntity.getKey().toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.STATEORPROVINCE.toString()+" ");

					//Adding to the Map
					addValueToNormalizationMap(normalizationValue, PatternConstantEnumKBP.STATEORPROVINCE.toString(), nerMapEntity.getKey());*/

				} else if(cityList.contains(nerMapEntity.getKey().toLowerCase())) {

					processedArgument = normalizeStringForPatternValue(PatternConstantEnumKBP.CITY.toString(), "\\s+[^a-zA-Z0-9 ]*\\Q"+nerMapEntity.getKey().toLowerCase()+"\\E[^a-zA-Z0-9 ]*\\s+", processedArgument, normalizationValue);

					/*processedArgument = processedArgument.replaceAll("[^a-zA-Z0-9]+\\Q"+nerMapEntity.getKey().toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.CITY.toString()+" ");

					//Adding to the Map
					addValueToNormalizationMap(normalizationValue, PatternConstantEnumKBP.CITY.toString(), nerMapEntity.getKey());*/

				}else {

					processedArgument = normalizeStringForPatternValue(nerMapEntity.getValue(), regex, processedArgument, normalizationValue);

					/*processedArgument = processedArgument.replaceAll(regex, " "+nerMapEntity.getValue()+" ");

					//Adding to the Map
					addValueToNormalizationMap(normalizationValue, nerMapEntity.getValue(), regex.trim());*/
				}
			}
			//}
		}

		return processedArgument.trim();
	}

	/**
	 * Normalizes the string term to contain the pattern constants
	 * The things normalized are
	 * jobTitle
	 * HeadJobTitle
	 * Country
	 * City
	 * StateorProvince
	 * */
	public String normalizeString(String term, Map<String,List<String>> normalizationValue) {

		//Adding spaces at the end in the relation term
		String processedTerm = " "+term.trim()+" ";


		//Normalize headJobTitle in the relation String
		for (String headJobTitle : this.headJobTitle) {

			processedTerm = normalizeStringForPatternValue(PatternConstantEnumKBP.HEADJOBTITLE.toString(), "\\s+[^a-zA-Z0-9 ]*\\Q"+headJobTitle.toLowerCase()+"\\E[^a-zA-Z0-9 ]*\\s+", processedTerm, normalizationValue);

			/*if(Pattern.compile("[^a-zA-Z0-9]+\\Q"+headJobTitle.toLowerCase()+"\\E[^a-zA-Z0-9]+").matcher(processedTerm).find()) {
				processedTerm = processedTerm.replaceAll("[^a-zA-Z0-9]+\\Q"+headJobTitle.toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.HEADJOBTITLE.toString()+" ");
				addValueToNormalizationMap(normalizationValue, PatternConstantEnumKBP.HEADJOBTITLE.toString(), headJobTitle);
			}*/

		}

		//Normalizing jobTitle in the relation String
		for (String jobTitle : jobTitileList) {

			processedTerm = normalizeStringForPatternValue(PatternConstantEnumKBP.JOBTITLE.toString(), "\\s+[^a-zA-Z0-9 ]*\\Q"+jobTitle.toLowerCase()+"\\E[^a-zA-Z0-9 ]*\\s+", processedTerm, normalizationValue);

			/*if(Pattern.compile("[^a-zA-Z0-9]+\\Q"+jobTitle.toLowerCase()+"\\E[^a-zA-Z0-9]+").matcher(processedTerm).find()) {
				processedTerm = processedTerm.replaceAll("[^a-zA-Z0-9]+\\Q"+jobTitle.toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.JOBTITLE.toString()+" ");
				addValueToNormalizationMap(normalizationValue, PatternConstantEnumKBP.JOBTITLE.toString(), jobTitle);
			}*/
		}


		//Normalizing nationality
		for (String nationality : nationalityList) {

			processedTerm = normalizeStringForPatternValue(PatternConstantEnumKBP.NATIONALITY.toString(), "\\s+[^a-zA-Z0-9 ]*\\Q"+nationality.toLowerCase()+"\\E[^a-zA-Z0-9 ]*\\s+", processedTerm, normalizationValue);

			/*if(Pattern.compile("[^a-zA-Z0-9]+\\Q"+nationality.toLowerCase()+"\\E[^a-zA-Z0-9]+").matcher(processedTerm).find()) {
				processedTerm = processedTerm.replaceAll("[^a-zA-Z0-9]+\\Q"+nationality.toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.NATIONALITY.toString()+" ");
				addValueToNormalizationMap(normalizationValue, PatternConstantEnumKBP.NATIONALITY.toString(), nationality);
			}*/
		}

		//Normalizing crimesList
		for (String crime : crimeList) {

			processedTerm = normalizeStringForPatternValue(PatternConstantEnumKBP.CRIME.toString(), "\\s+[^a-zA-Z0-9 ]*\\Q"+crime.toLowerCase()+"\\E[^a-zA-Z0-9 ]*\\s+", processedTerm, normalizationValue);

			/*if(Pattern.compile("[^a-zA-Z0-9]+\\Q"+crime.toLowerCase()+"\\E[^a-zA-Z0-9]+").matcher(processedTerm).find()) {
				processedTerm = processedTerm.replaceAll("[^a-zA-Z0-9]+\\Q"+crime.toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.CRIME.toString()+" ");
				addValueToNormalizationMap(normalizationValue, PatternConstantEnumKBP.CRIME.toString(), crime);
			}*/
		}

		//Normalizing religionLsit
		for (String religion : religionList) {

			processedTerm = normalizeStringForPatternValue(PatternConstantEnumKBP.RELIGION.toString(), "\\s+[^a-zA-Z0-9 ]*\\Q"+religion.toLowerCase()+"\\E[^a-zA-Z0-9 ]*\\s+", processedTerm, normalizationValue);

			/*if(Pattern.compile("[^a-zA-Z0-9]+\\Q"+crime.toLowerCase()+"\\E[^a-zA-Z0-9]+").matcher(processedTerm).find()) {
						processedTerm = processedTerm.replaceAll("[^a-zA-Z0-9]+\\Q"+crime.toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.CRIME.toString()+" ");
						addValueToNormalizationMap(normalizationValue, PatternConstantEnumKBP.CRIME.toString(), crime);
					}*/
		}

		//Normalizing School List
		for (String school : schoolList) {

			processedTerm = normalizeStringForPatternValue(PatternConstantEnumKBP.SCHOOL.toString(), "\\s+[^a-zA-Z0-9 ]*\\Q"+school.toLowerCase()+"\\E[^a-zA-Z0-9 ]*\\s+", processedTerm, normalizationValue);

			/*if(Pattern.compile("[^a-zA-Z0-9]+\\Q"+school.toLowerCase()+"\\E[^a-zA-Z0-9]+").matcher(processedTerm).find()) {
				processedTerm = processedTerm.replaceAll("[^a-zA-Z0-9]+\\Q"+school.toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.SCHOOL.toString()+" ");
				addValueToNormalizationMap(normalizationValue, PatternConstantEnumKBP.SCHOOL.toString(), school);
			}*/
		}

		//Normalizing Product List
		for (String product : productList) {

			processedTerm = normalizeStringForPatternValue(PatternConstantEnumKBP.PRODUCT.toString(), "\\s+[^a-zA-Z0-9 ]*\\Q"+product.toLowerCase()+"\\E[^a-zA-Z0-9 ]*\\s+", processedTerm, normalizationValue);

			/*if(Pattern.compile("[^a-zA-Z0-9]+\\Q"+product.toLowerCase()+"\\E[^a-zA-Z0-9]+").matcher(processedTerm).find()) {
				processedTerm = processedTerm.replaceAll("[^a-zA-Z0-9]+\\Q"+product.toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.PRODUCT.toString()+" ");
				addValueToNormalizationMap(normalizationValue, PatternConstantEnumKBP.PRODUCT.toString(), product);
			}*/
		}

		//Normalizing Technology List
		for (String technology : technologyList) {

			processedTerm = normalizeStringForPatternValue(PatternConstantEnumKBP.TECHNOLOGY.toString(), "\\s+[^a-zA-Z0-9 ]*\\Q"+technology.toLowerCase()+"\\E[^a-zA-Z0-9 ]*\\s+", processedTerm, normalizationValue);

			/*if(Pattern.compile("[^a-zA-Z0-9]+\\Q"+technology.toLowerCase()+"\\E[^a-zA-Z0-9]+").matcher(processedTerm).find()) {
				processedTerm = processedTerm.replaceAll("[^a-zA-Z0-9]+\\Q"+technology.toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.TECHNOLOGY.toString()+" ");
				addValueToNormalizationMap(normalizationValue, PatternConstantEnumKBP.TECHNOLOGY.toString(), technology);
			}*/
		}

		return processedTerm.trim();
	}


	public String normalizeInteger(String term, Map<String,List<String>> normalizationValue) {

		//Adding spaces at the end in the relation term
		String processedTerm = " "+term.trim()+" ";

		//Integer regex
		String integerRegex1 = "\\s+\\d+,?.?\\s+";
		String integerRegex2 = "\\s+(one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty|thirty|fourty|fifty|sixty|seventy|eighty|ninety|hundred|thousand|million|billion|trillion|quadrillion)\\s+";

		processedTerm = normalizeStringForPatternValue(PatternConstantEnumKBP.INTEGER.toString(), integerRegex1, processedTerm, normalizationValue);
		processedTerm = normalizeStringForPatternValue(PatternConstantEnumKBP.INTEGER.toString(), integerRegex2, processedTerm, normalizationValue);
		/*Pattern pattern = Pattern.compile(integerRegex1);
		Matcher matcher = pattern.matcher(processedTerm);
		while(matcher.find()) {

			processedTerm = matcher.replaceFirst(" "+PatternConstantEnumKBP.INTEGER.toString()+" ");

			//Adding value to normalization value
			addValueToNormalizationMap(normalizationValue, PatternConstantEnumKBP.INTEGER.toString(), matcher.group());
			matcher = pattern.matcher(processedTerm);
		}

		pattern = Pattern.compile(integerRegex2);
		matcher = pattern.matcher(processedTerm);
		while(matcher.find()) {

			processedTerm = matcher.replaceFirst(" "+PatternConstantEnumKBP.INTEGER.toString()+" ");

			//Adding value to normalization value
			addValueToNormalizationMap(normalizationValue, PatternConstantEnumKBP.INTEGER.toString(), matcher.group());
			matcher = pattern.matcher(processedTerm);
		}*/



		return processedTerm.trim();
	}
	/**
	 * Adds value to the list of key in the map
	 * 
	 * */
	public void addValueToNormalizationMap(Map<String,List<String>> normalizationMap, String key, String value) {

		if(normalizationMap.containsKey(key)) {

			List<String> valueList = normalizationMap.get(key);
			valueList.add(value);

		}else {
			//The Map does not contain the key constant
			List<String> valueList = new ArrayList<String>();
			valueList.add(value);
			normalizationMap.put(key, valueList);
		}
	}

	/**
	 * 
	 * This function takes inputString, regex and pattern constant
	 * replaces the regex in the inputString with the patternConstant#indexInTheMapForTheKey
	 * updates the value in the normalization map
	 * */
	public String normalizeStringForPatternValue(String patternConstant, String regexString, String inputText, Map<String,List<String>> normalizationMap) {

		Pattern pattern = Pattern.compile(regexString);
		Matcher matcher = pattern.matcher(inputText);

		while(matcher.find()) {
			int patternConstantCount=1;

			//Updating the count
			if(normalizationMap.containsKey(patternConstant)) {
				patternConstantCount = normalizationMap.get(patternConstant).size()+1;
			}

			inputText = matcher.replaceFirst(" "+patternConstant+"#"+patternConstantCount+" ");
			//System.out.println("----"+matcher.group());

			//addValueToNormalizationMap(normalizationMap, patternConstant, regexString.trim());
			addValueToNormalizationMap(normalizationMap, patternConstant, matcher.group().trim());

			matcher = pattern.matcher(inputText);

		}

		return inputText;
	}

	/*public static void main(String[] args) {

		KeywordTagger2 keywordTagger2 = new KeywordTagger2();
		String text = "Last Year gates earned 2 million dollars 3 times .";
		HashMap<String,List<String>> hashMap = new HashMap<String, List<String>>();
		String normalizeInteger = keywordTagger2.normalizeInteger(text.toLowerCase(), hashMap);

		for (Entry<String, List<String>> entry : hashMap.entrySet()) {

			System.out.println(entry.getKey());
			for (String string : entry.getValue()) {
				System.out.println(string);
			}

		}
		System.out.println(normalizeInteger);
	}*/
}
