/**
 * 
 */
package edu.iitd.cse.tsf.models;

/**
 * @author abhishek
 *This class contains the details of the pattern to be matched.
 *The inspiration of the members of pattern is derived from the  knowitall tac2013
 *Member variables can be added as per required
 *
 */
public class RelationPattern {

	private String relationType;
	
	//Maximum values of slotfill for the relation, -1 means uninitialized and 9 means no limit
	private int maxSlotFillValues;
	
	//Contains the relation term pattern
	private String relationTerm;
	
	private String arg2Begin;
	
	//IF true then entity is in arg1 and slotfill in arg2 ELSE entity is in arg2 and slotfill in arg1 
	private boolean entityInArg1;
	
	//Following three member variables are added for original TAC pattern type 
	private String slotType;
	
	private String arg1Term;
	
	private String arg2Term;
	
	private String entityIn;
	
	private String slotFillIn;
	
	/*TODO
	 * Slottype is the type of slotfill argument i.e. person,city,title,organization
	 * This can be a String or an enum
	 * */  
	//private String slotType;
	
	/**
	 * @param relationTerm
	 * @param arg2Begin
	 * @param entityInArg1
	 */
	public RelationPattern(String relationTerm, String arg2Begin, boolean entityInArg1) {
		super();
		this.relationTerm = relationTerm;
		this.arg2Begin = arg2Begin;
		this.entityInArg1 = entityInArg1;
	}

	/**
	 * 
	 */
	public RelationPattern() {
		super();
		// TODO Auto-generated constructor stub
		this.relationTerm = null;
		this.arg2Begin = null;
		this.entityInArg1 = false;
		
		this.relationType = null;
		this.maxSlotFillValues = -1;
		this.slotType = null;
		this.arg1Term = null;
		this.arg2Term = null;
		this.entityIn = null;
		this.slotFillIn = null;
		
	}

	/**
	 * Copy constructor
	 * */
	public RelationPattern(RelationPattern pattern) {
		super();
		// TODO Auto-generated constructor stub
		this.relationTerm = pattern.relationTerm;
		this.arg2Begin = pattern.arg2Begin;
		this.entityInArg1 = pattern.entityInArg1;
		
		this.relationType = pattern.relationType;
		this.maxSlotFillValues = pattern.maxSlotFillValues;
		this.slotType = pattern.slotType;
		this.arg1Term = pattern.arg1Term;
		this.arg2Term = pattern.arg2Term;
		this.entityIn = pattern.entityIn;
		this.slotFillIn = pattern.slotFillIn;
		
	}
	
	/**
	 * 
	 * @return
	 */
	public String getRelationType() {
		return relationType;
	}

	/**
	 * 
	 * @param relationType
	 */
	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	/**
	 * 
	 * @return
	 */
	public int getMaxSlotFillValues() {
		return maxSlotFillValues;
	}

	/**
	 * 
	 * @param maxSlotFillValues
	 */
	public void setMaxSlotFillValues(int maxSlotFillValues) {
		this.maxSlotFillValues = maxSlotFillValues;
	}

	/**
	 * @return the relationTerm
	 */
	public String getRelationTerm() {
		return relationTerm;
	}

	/**
	 * @param relationTerm the relationTerm to set
	 */
	public void setRelationTerm(String relationTerm) {
		this.relationTerm = relationTerm;
	}

	/**
	 * @return the arg2Begin
	 */
	public String getArg2Begin() {
		return arg2Begin;
	}

	/**
	 * @param arg2Begin the arg2Begin to set
	 */
	public void setArg2Begin(String arg2Begin) {
		this.arg2Begin = arg2Begin;
	}

	/**
	 * @return the entityInArg1
	 */
	public boolean isEntityInArg1() {
		return entityInArg1;
	}

	/**
	 * @param entityInArg1 the entityInArg1 to set
	 */
	public void setEntityInArg1(boolean entityInArg1) {
		this.entityInArg1 = entityInArg1;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSlotType() {
		return slotType;
	}
	
	/**
	 * 
	 * @param slotType
	 */
	public void setSlotType(String slotType) {
		this.slotType = slotType;
	}

	/**
	 * 
	 * @return
	 */
	public String getArg1Term() {
		return arg1Term;
	}
	
	/**
	 * 
	 * @param arg1Term
	 */
	public void setArg1Term(String arg1Term) {
		this.arg1Term = arg1Term;
	}

	/**
	 * 
	 * @return
	 */
	public String getArg2Term() {
		return arg2Term;
	}

	/**
	 * 
	 * @param arg2Term
	 */
	public void setArg2Term(String arg2Term) {
		this.arg2Term = arg2Term;
	}

	/**
	 * 
	 * @return
	 */
	public String getEntityIn() {
		return entityIn;
	}

	/**
	 * 
	 * @param entityIn
	 */
	public void setEntityIn(String entityIn) {
		this.entityIn = entityIn;
	}

	/**
	 * 
	 * @return
	 */
	public String getSlotFillIn() {
		return slotFillIn;
	}

	/**
	 * 
	 * @param slotFillIn
	 */
	public void setSlotFillIn(String slotFillIn) {
		this.slotFillIn = slotFillIn;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Pattern [relationTerm=" + relationTerm + ", arg2Begin="
				+ arg2Begin + ", entityInArg1=" + entityInArg1 +", slotfillIn="+slotFillIn+", SlotType="+slotType+ "]";
	}
	
	
}
