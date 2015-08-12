/**
 * 
 */
package edu.iitd.cse.tsf.models;

import edu.iitd.cse.tsf.constants.KBPRelationTypeEnum;
import edu.iitd.cse.tsf.constants.OpenIEExtractionBOKBP;

/**
 * @author mcs132540
 *
 */
public class KBPRelationClassificationOutputBO {
	
	KBPRelationTypeEnum kbpRelationTypeEnum;
	
	//The openIE Extraction
	private OpenIEExtractionBOKBP openIEExtractionBO;
	
	//Contains string normalized argument1, argument2 and relation
	private OpenIEExtractionBOKBP normalizedOpenIEExtractionBO;
	
	//The matched pattern and null if openIE extraction does not match any pattern
	private RelationPattern pattern;

	/**
	 * 
	 * @param openIEExtractionBO
	 * @param pattern
	 */
	public KBPRelationClassificationOutputBO(
			OpenIEExtractionBOKBP openIEExtractionBO, RelationPattern pattern) {
		super();
		this.openIEExtractionBO = openIEExtractionBO;
		this.pattern = pattern;
	}

	/**
	 * 
	 */
	public KBPRelationClassificationOutputBO() {
		super();
		this.openIEExtractionBO = null;
		this.pattern = null;
		this.normalizedOpenIEExtractionBO = null;
	}

	/**
	 * 
	 * @return
	 */
	public OpenIEExtractionBOKBP getOpenIEExtractionBO() {
		return openIEExtractionBO;
	}

	/**
	 * 
	 * @param openIEExtractionBO
	 */
	public void setOpenIEExtractionBO(OpenIEExtractionBOKBP openIEExtractionBO) {
		this.openIEExtractionBO = openIEExtractionBO;
	}

	/**
	 * 
	 * @return
	 */
	public RelationPattern getPattern() {
		return pattern;
	}

	/**
	 * 
	 * @param pattern
	 */
	public void setPattern(RelationPattern pattern) {
		this.pattern = pattern;
	}

	/**
	 * 
	 * @return
	 */
	public KBPRelationTypeEnum getKbpRelationTypeEnum() {
		return kbpRelationTypeEnum;
	}

	/**
	 * 
	 * @param kbpRelationTypeEnum
	 */
	public void setKbpRelationTypeEnum(KBPRelationTypeEnum kbpRelationTypeEnum) {
		this.kbpRelationTypeEnum = kbpRelationTypeEnum;
	}

	/**
	 * 
	 * @return
	 */
	public OpenIEExtractionBOKBP getNormalizedOpenIEExtractionBO() {
		return normalizedOpenIEExtractionBO;
	}

	/**
	 * 
	 * @param normalizedOpenIEExtractionBO
	 */
	public void setNormalizedOpenIEExtractionBO(
			OpenIEExtractionBOKBP normalizedOpenIEExtractionBO) {
		this.normalizedOpenIEExtractionBO = normalizedOpenIEExtractionBO;
	}
	
}
