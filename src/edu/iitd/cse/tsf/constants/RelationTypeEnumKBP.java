/**
 * 
 */
package edu.iitd.cse.tsf.constants;

/**
 * @author abhishek
 *
 */
public enum RelationTypeEnumKBP {

	SPOUSE("SPOUSE"),
	
	EMPLOYEE("EMPLOYEE"),
	
	RESIDENCE("RESIDENCE"),
	
	TITLE("TITLE"),
	
	NONE("NONE");
	
	/** The value. */
	private final String value;

	
	/**
	 * @param value
	 */
	private RelationTypeEnumKBP(String value) {
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
	public static RelationTypeEnumKBP fromValue(String v) {
		for (RelationTypeEnumKBP relationTypeEnum : RelationTypeEnumKBP.values()) {
			if(relationTypeEnum.value.equalsIgnoreCase(v)){
				return relationTypeEnum;
			}
		}
		throw new IllegalArgumentException(String.valueOf(v));
	}
}
