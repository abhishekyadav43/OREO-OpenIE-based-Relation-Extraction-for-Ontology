/**
 * 
 */
package edu.iitd.cse.tsf.constants;

/**
 * @author abhishek
 *
 */
public class RelationClassificationOutputBOKBP {
	
	//Type of relation
	RelationTypeEnumKBP relationType;
	//Temporal Type for the relation
	TemporalTypeEnumKBP temporalType;
	
	
	/**
	 * 
	 */
	public RelationClassificationOutputBOKBP() {
		super();
		// TODO Auto-generated constructor stub
		relationType = RelationTypeEnumKBP.NONE;
		temporalType = TemporalTypeEnumKBP.Irrelevant;
	}


	/**
	 * @param relationTyp
	 * @param temporalType
	 */
	public RelationClassificationOutputBOKBP(RelationTypeEnumKBP relationTyp,
			TemporalTypeEnumKBP temporalType) {
		super();
		this.relationType = relationTyp;
		this.temporalType = temporalType;
	}


	/**
	 * @return the relationType
	 */
	public RelationTypeEnumKBP getRelationType() {
		return relationType;
	}


	/**
	 * @param relationType the relationType to set
	 */
	public void setRelationType(RelationTypeEnumKBP relationType) {
		this.relationType = relationType;
	}


	/**
	 * @return the temporalType
	 */
	public TemporalTypeEnumKBP getTemporalType() {
		return temporalType;
	}


	/**
	 * @param temporalType the temporalType to set
	 */
	public void setTemporalType(TemporalTypeEnumKBP temporalType) {
		this.temporalType = temporalType;
	}
	
	

}
