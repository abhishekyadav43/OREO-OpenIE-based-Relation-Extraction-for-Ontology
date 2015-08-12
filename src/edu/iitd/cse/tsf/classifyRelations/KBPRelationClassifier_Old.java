/**
 * 
 */
package edu.iitd.cse.tsf.classifyRelations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import edu.iitd.cse.tsf.constants.KBPRelationTypeEnum;
import edu.iitd.cse.tsf.constants.OpenIEExtractionBOKBP;
import edu.iitd.cse.tsf.constants.RelationClassificationOutputBOKBP;
import edu.iitd.cse.tsf.constants.RelationTypeEnumKBP;
import edu.iitd.cse.tsf.constants.TemporalTypeEnumKBP;
import edu.iitd.cse.tsf.models.KBPRelationClassificationOutputBO;
import edu.iitd.cse.tsf.pattern_Old.PatternFinderKBP_Old;

/**
 * @author abhishek
 *
 */
public class KBPRelationClassifier_Old {
	
	Properties properties;
	PatternFinderKBP_Old relationsClassifier;
	/**
	 * 
	 */
	public KBPRelationClassifier_Old() {
		super();
		properties = new Properties();
		relationsClassifier = new PatternFinderKBP_Old();
	}
	
	public void loadProperties(String propertyPath) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(propertyPath);
			properties.load(inputStream);
			
			//Loading lists
			relationsClassifier.loadJobTitles(properties.getProperty("jobTitleFilePath"));
			relationsClassifier.loadHeadJobTitles(properties.getProperty("headJobTitleFilePath"));
			relationsClassifier.loadCountry(properties.getProperty("countriesFilePath"));
			relationsClassifier.loadCity(properties.getProperty("citiesFilePath"));
			relationsClassifier.loadStateOrProvince(properties.getProperty("statesOrProvincesFilePath"));
			relationsClassifier.loadNationality(properties.getProperty("nationalitiesFilePath"));
			relationsClassifier.loadSchool(properties.getProperty("schoolFilePath"));
			relationsClassifier.loadCrime(properties.getProperty("crimesFilePath"));
			relationsClassifier.loadProduct(properties.getProperty("productsFilePath"));
			relationsClassifier.loadTechnology(properties.getProperty("technologyFilePath"));
			
			//Loading classification patterns
			relationsClassifier.loadPersonPattern(properties.getProperty("personPatternFilePath"));
			relationsClassifier.loadOrganizationPattern(properties.getProperty("organizationPatternFilePath"));
			relationsClassifier.loadOthersPattern(properties.getProperty("othersPatternFilePath"));
			relationsClassifier.loadAllPatterns(properties.getProperty("allPatternFilePath"));
			
			
			/*//Instantiating patternFinder for relation spouse
			PatternFinder patternFinderPerson = new PatternFinder();
			patternFinderSpouse.loadPatterns(properties.getProperty("spouseFilePath"));
			relationsClassifier.put(RelationTypeEnum.SPOUSE, patternFinderSpouse);
			
			//Instantiating patternFinder for relation employee
			PatternFinder patternFinderEmployee = new PatternFinder();
			patternFinderEmployee.loadPatterns(properties.getProperty("employeeFilePath"));
			relationsClassifier.put(RelationTypeEnum.EMPLOYEE, patternFinderEmployee);
			
			//Instantiating patternFinder for relation residence
			PatternFinder patternFinderResidence = new PatternFinder();
			patternFinderResidence.loadPatterns(properties.getProperty("residenceFilePath"));
			relationsClassifier.put(RelationTypeEnum.RESIDENCE, patternFinderResidence);
			
			//Instantiating patternFinder for relation title
			PatternFinder patternFinderTitle = new PatternFinder();
			patternFinderTitle.loadPatterns(properties.getProperty("titleFilePath"));
			//Loading the list of titles
			patternFinderTitle.loadTitles(properties.getProperty("titlesListFilePath"));
			relationsClassifier.put(RelationTypeEnum.TITLE, patternFinderTitle);*/
			
		} catch (IOException e) {
			System.err.println("Error loading the properties or pattern files");
			e.printStackTrace();
		}
	}
	

	public List<KBPRelationClassificationOutputBO> classify(OpenIEExtractionBOKBP openIEExtractionBO,Map<String,String> nerOutput, KBPRelationTypeEnum relationTypeEnum) {
		
		/*RelationClassificationOutputBO outputBO = new RelationClassificationOutputBO();
		
		//Running all the classifiers
		for (Entry<RelationTypeEnum, PatternFinder> entry : relationsClassifier.entrySet()) {
			RelationTypeEnum key = entry.getKey();
			PatternFinder patternFinder = entry.getValue();
			TemporalTypeEnum relationTemporalType = patternFinder.getRelationTemporalType(openIEExtractionBO,nerOutput);
			//Exits when
			if(relationTemporalType != TemporalTypeEnum.Irrelevant) {
				outputBO.setRelationType(key);
				outputBO.setTemporalType(relationTemporalType);
				break;
			}
			
		}*/
		//relationsClassifier.loadOthersPattern(properties.getProperty("othersPatternFilePath"));
		List<KBPRelationClassificationOutputBO> outputBOs = null;
		
		//Different classification for different KBPRelationType 
		//outputBOs = relationsClassifier.classifyOpenIEExtraction(openIEExtractionBO, nerOutput, relationTypeEnum);
		
		//All in on 
		outputBOs = relationsClassifier.classifyOpenIEExtractionForAll(openIEExtractionBO, nerOutput);
		return outputBOs;
	}
}
