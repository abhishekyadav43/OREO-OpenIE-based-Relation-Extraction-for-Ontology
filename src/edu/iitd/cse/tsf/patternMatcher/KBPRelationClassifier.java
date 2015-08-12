package edu.iitd.cse.tsf.patternMatcher;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.iitd.cse.tsf.constants.KBPRelationTypeEnum;
import edu.iitd.cse.tsf.constants.OpenIEExtractionBOKBP;
import edu.iitd.cse.tsf.models.KBPRelationClassificationOutputBO;


/**
 * @author abhishek
 *
 */
public class KBPRelationClassifier {

	private Properties properties;
	private PatternFinderKBP relationsClassifier;
	/**
	 * 
	 */
	public KBPRelationClassifier() {
		super();
		properties = new Properties();
		relationsClassifier = new PatternFinderKBP();
	}

	public void loadProperties(String propertyPath) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(propertyPath);
			properties.load(inputStream);

			//Loading classification patterns
			relationsClassifier.loadPersonPattern(properties.getProperty("personPatternFilePath"));
			relationsClassifier.loadOrganizationPattern(properties.getProperty("organizationPatternFilePath"));
			relationsClassifier.loadOthersPattern(properties.getProperty("othersPatternFilePath"));
			relationsClassifier.loadAllPatterns(properties.getProperty("allPatternFilePath"));

		} catch (IOException e) {
			System.err.println("Error loading the properties or pattern files");
			e.printStackTrace();
		}
	}


	public List<KBPRelationClassificationOutputBO> classify(OpenIEExtractionBOKBP openIEExtractionBO,Map<String,String> nerOutput, KBPRelationTypeEnum relationTypeEnum) {

		//relationsClassifier.loadOthersPattern(properties.getProperty("othersPatternFilePath"));
		List<KBPRelationClassificationOutputBO> outputBOs = null;
		outputBOs = relationsClassifier.classifyOpenIEExtraction(openIEExtractionBO, nerOutput, relationTypeEnum);
		return outputBOs;
	}


	/**
	 * Takes oprnIE extractions and matches the extraction for all possible patterns
	 * 
	 * */
	public List<KBPRelationClassificationOutputBO> classifyForAllTypes(OpenIEExtractionBOKBP openIEExtractionBO,Map<String,String> nerOutput) {

		//relationsClassifier.loadOthersPattern(properties.getProperty("othersPatternFilePath"));
		List<KBPRelationClassificationOutputBO> outputBOs = null;
		outputBOs = relationsClassifier.classifyOpenIEExtractionForAll(openIEExtractionBO, nerOutput);
		return outputBOs;
	}
}
