/**
 * 
 */
package edu.iitd.cse.tsf.constants;

/**
 * @author abhishek
 * This contains the constants used in specifying patterns
 */
public enum PatternConstantEnumKBP {

	PERSON("PERSON"),
	
	LOCATION("LOCATION"),
	
	ORGANIZATION("ORGANIZATION"),
	
	JOBTITLE("JOBTITLE"),
	
	HEADJOBTITLE("HEADJOBTITLE"),
	
	COUNTRY("COUNTRY"),
	
	CITY("CITY"),
	
	STATEORPROVINCE("STATEORPROVINCE"),
	
	INTEGER("INTEGER"),
	
	DATE("DATE"),
	
	NATIONALITY("NATIONALITY"),
	
	CRIME("CRIME"),
	
	SCHOOL("SCHOOL"),
	
	PRODUCT("PRODUCT"),
	
	TECHNOLOGY("TECHNOLOGY"),
	
	RELIGION("RELIGION"),
	
	NONE("NONE");

	/** The value. */
	private final String value;

	
	/**
	 * @param value
	 */
	private PatternConstantEnumKBP(String value) {
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
	public static PatternConstantEnumKBP fromValue(String v) {
		for (PatternConstantEnumKBP patternConstantEnum : PatternConstantEnumKBP.values()) {
			if(patternConstantEnum.value.equalsIgnoreCase(v)){
				return patternConstantEnum;
			}
		}
		throw new IllegalArgumentException(String.valueOf(v));
	}
}
