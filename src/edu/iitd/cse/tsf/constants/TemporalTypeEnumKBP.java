/**
 * 
 */
package edu.iitd.cse.tsf.constants;

/**
 * @author abhishek
 * This is the enum for Temporal types
 * The values are different than that in cuny blender's tagging
 * 
 */
public enum TemporalTypeEnumKBP {

	Start(1),

	End(2),

	Hold(3),

	Range(4),

	Irrelevant(5);

	/** The value. */
	private final Integer value;

	/**
	 * @param value
	 */
	private TemporalTypeEnumKBP(Integer value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public Integer getValue() {
		return value;
	}
	
	public static TemporalTypeEnumKBP fromValue(Integer v) {
		for (TemporalTypeEnumKBP temporalTypeEnum : TemporalTypeEnumKBP.values()) {
			if(temporalTypeEnum.value.equals(v)){
				return temporalTypeEnum;
			}
		}
		throw new IllegalArgumentException(String.valueOf(v));
	}
	
	public static String getString(Integer v) {
		
		String returnValue = null;
		switch (v.intValue()) {
		case 1 :
			returnValue = "start";
			break;
		case 2 :
			returnValue = "end";
			break;
		case 3 :
			returnValue = "hold";
			break;
		case 4 :
			returnValue = "range";
			break;
		default:
			throw new IllegalArgumentException(String.valueOf(v));
		}
		
		return returnValue;
	}
}
