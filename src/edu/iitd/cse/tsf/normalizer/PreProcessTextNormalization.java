package edu.iitd.cse.tsf.normalizer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;



/**
 * @author abhishek
 *
 */
public class PreProcessTextNormalization {

	private KeywordTagger keywordTagger;
	
	private DateNormalizer dateNormalizer;

	public PreProcessTextNormalization(String propertyFilePath) {
		super();
		// TODO Auto-generated constructor stub
		keywordTagger = new KeywordTagger();
		dateNormalizer = new DateNormalizer();
		
		Properties properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(propertyFilePath);
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
			keywordTagger.loadReligion(properties.getProperty("religionsFilePath"));
			keywordTagger.loadProduct(properties.getProperty("productsFilePath"));
			keywordTagger.loadTechnology(properties.getProperty("technologyFilePath"));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Takes the text and performs the required normalization and returns the normalized text 
	 * Also fills the tagMap with the Normalization constant and their value
	 * 
	 * */
	public String performTextNormalization(String text, Map<String,String> nerMap, Map<String,List<String>> tagMap) {

		//Map<String,List<String>> tagMap = new HashMap<String, List<String>>();

		String normalizedTerm = " "+text.trim()+" ";
		normalizedTerm = dateNormalizer.normalizeDate(normalizedTerm,tagMap);
		normalizedTerm = keywordTagger.normalizeNERs(normalizedTerm, nerMap, tagMap);

		normalizedTerm = keywordTagger.normalizeString(normalizedTerm,tagMap);
		normalizedTerm = keywordTagger.normalizeInteger(normalizedTerm, tagMap);

		return normalizedTerm;
	}

	
	/**
	 * @return
	 */
	public KeywordTagger getKeywordTagger() {
		return keywordTagger;
	}

	/**
	 * @param keywordTagger
	 */
	public void setKeywordTagger(KeywordTagger keywordTagger) {
		this.keywordTagger = keywordTagger;
	}

	/**
	 * @return
	 */
	public DateNormalizer getDateNormalizer() {
		return dateNormalizer;
	}

	/**
	 * @param dateNormalizer
	 */
	public void setDateNormalizer(DateNormalizer dateNormalizer) {
		this.dateNormalizer = dateNormalizer;
	}
	
	
	
}
