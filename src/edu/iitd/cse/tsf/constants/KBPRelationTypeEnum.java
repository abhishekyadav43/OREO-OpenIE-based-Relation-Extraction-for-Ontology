/**
 * 
 */
package edu.iitd.cse.tsf.constants;

/**
 * @author abhishek
 *
 */
public enum KBPRelationTypeEnum {

	PERSON("PERSON"),
	
	ORGANIZATION("ORGANIZATION"),
	
	//This Enum was added to handle the type of Product and Technology 
	OTHERS("OTHERS");
	
	/** The value. */
	private final String value;

	
	/**
	 * @param value
	 */
	private KBPRelationTypeEnum(String value) {
		this.value = value;
	}


	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * 
	 * */
	public static KBPRelationTypeEnum fromValue(String v) {
		for (KBPRelationTypeEnum relationTypeEnum : KBPRelationTypeEnum.values()) {
			if(relationTypeEnum.value.equalsIgnoreCase(v)){
				return relationTypeEnum;
			}
		}
		throw new IllegalArgumentException(String.valueOf(v));
	}
}
