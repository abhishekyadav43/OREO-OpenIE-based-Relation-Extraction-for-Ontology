/**
 * 
 */
package edu.iitd.cse.tsf.pattern_Old;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ho.yaml.Yaml;

import edu.iitd.cse.tsf.constants.KBPRelationTypeEnum;
import edu.iitd.cse.tsf.constants.PatternConstantEnumKBP;

/**
 * @author mcs132540
 *
 */
public class KeywordTagger_Old {

	private List<String> jobTitileList;
	private List<String> headJobTitle;
	private List<String> countryList;
	private List<String> cityList;
	private List<String> stateorprovinceList;
	private List<String> nationalityList;
	private List<String> schoolList;
	private List<String> crimeList;
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
	public String normalizeNERs(String argument2, Map<String, String> nerOutput) {
		// TODO Auto-generated method stub

		String processedArgument2 = " "+argument2.trim()+" ";
		for (Entry<String, String> nerMapEntity : nerOutput.entrySet()) {
			String regex = "\\s?"+nerMapEntity.getKey().toLowerCase()+"\\s?";
			String value = nerMapEntity.getValue();

			//Skip the DATE and MISC Tagged tokens
			if(value.equals("DATE") || value.equals("MISC") || value.equals("NUMBER")) {
				continue;
			}

			//if(processedArgument2.contains(regex)) {
			if(!value.equals("LOCATION")) {
				processedArgument2 = processedArgument2.replaceAll(regex, " "+nerMapEntity.getValue()+" ");

			} else {
				if(countryList.contains(nerMapEntity.getKey())) {
					processedArgument2 = processedArgument2.replaceAll("[^a-zA-Z0-9]+\\Q"+nerMapEntity.getKey().toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.COUNTRY.toString()+" ");
				} else if(stateorprovinceList.contains(nerMapEntity.getKey().toLowerCase())) {
					processedArgument2 = processedArgument2.replaceAll("[^a-zA-Z0-9]+\\Q"+nerMapEntity.getKey().toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.STATEORPROVINCE.toString()+" ");
				} else if(cityList.contains(nerMapEntity.getKey().toLowerCase())) {
					processedArgument2 = processedArgument2.replaceAll("[^a-zA-Z0-9]+\\Q"+nerMapEntity.getKey().toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.CITY.toString()+" ");
				}else {
					processedArgument2 = processedArgument2.replaceAll(regex, " "+nerMapEntity.getValue()+" ");
				}
			}
			//}
		}

		return processedArgument2.trim();
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
	public String normalizeString(String term) {

		//Adding spaces at the end in the relation term
		String processedTerm = " "+term.trim()+" ";


		//Normalize headJobTitle in the relation String
		for (String headJobTitle : this.headJobTitle) {
			processedTerm = processedTerm.replaceAll("[^a-zA-Z0-9]+\\Q"+headJobTitle.toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.HEADJOBTITLE.toString()+" ");
		}

		//Normalizing jobTitle in the relation String
		for (String jobTitle : jobTitileList) {
			processedTerm = processedTerm.replaceAll("[^a-zA-Z0-9]+\\Q"+jobTitle.toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.JOBTITLE.toString()+" ");
		}

		/*//Normalize country in the relation string
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
		}*/

		//Normalizing nationality
		for (String nationality : nationalityList) {

			processedTerm = processedTerm.replaceAll("[^a-zA-Z0-9]+\\Q"+nationality.toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.NATIONALITY.toString()+" ");
		}

		//Normalizing crimesList
		for (String crime : crimeList) {
			processedTerm = processedTerm.replaceAll("[^a-zA-Z0-9]+\\Q"+crime.toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.CRIME.toString()+" ");
		}

		//Normalizing School List
		for (String school : schoolList) {
			processedTerm = processedTerm.replaceAll("[^a-zA-Z0-9]+\\Q"+school.toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.SCHOOL.toString()+" ");
		}

		//Normalizing Product List
		for (String product : productList) {

			processedTerm = processedTerm.replaceAll("[^a-zA-Z0-9]+\\Q"+product.toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.PRODUCT.toString()+" ");
		}

		//Normalizing Technology List
		for (String technology : technologyList) {
			processedTerm = processedTerm.replaceAll("[^a-zA-Z0-9]+\\Q"+technology.toLowerCase()+"\\E[^a-zA-Z0-9]+", " "+PatternConstantEnumKBP.TECHNOLOGY.toString()+" ");
		}

		//Normalize Integers
		processedTerm = processedTerm.replaceAll("\\s+\\d+,?.?\\s+", " "+PatternConstantEnumKBP.INTEGER.toString()+" ");

		processedTerm = processedTerm.replaceAll("(\\s+one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty|thirty|fourty|fifty|sixty|seventy|eighty|ninety|hundred|thousand|million|billion|trillion|quadrillion)\\s+", " "+PatternConstantEnumKBP.INTEGER.toString()+" ");
		return processedTerm.trim();
	}
}
