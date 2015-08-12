/**
 * 
 */
package edu.iitd.cse.tsf.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author abhishek
 * 
 */
public class OpenIEExtractionBOKBP {
	
	String originalText;
	/**
	 * @return the originalText
	 */
	public String getOriginalText() {
		return originalText;
	}
	/**
	 * @param originalText the originalText to set
	 */
	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}
	//OpenIE extractions
	private String argument1;
	private String argument2;
	private String relation;

	//List of all the temporal expressions in the text
	private List<String> temporalExpressions;

	//List of other OpenIE arguments
	private List<String> others;


	/**
	 * 
	 */
	public OpenIEExtractionBOKBP() {
		super();
		temporalExpressions = new ArrayList<String>();
		others = new ArrayList<String>();
	}
	/**
	 * @return the argument1
	 */
	public String getArgument1() {
		return argument1;
	}
	/**
	 * @param argument1 the argument1 to set
	 */
	public void setArgument1(String argument1) {
		this.argument1 = argument1;
	}
	/**
	 * @return the argument2
	 */
	public String getArgument2() {
		return argument2;
	}
	/**
	 * @param argument2 the argument2 to set
	 */
	public void setArgument2(String argument2) {
		this.argument2 = argument2;
	}
	/**
	 * @return the relation
	 */
	public String getRelation() {
		return relation;
	}
	/**
	 * @param relation the relation to set
	 */
	public void setRelation(String relation) {
		this.relation = relation;
	}
	/**
	 * @return the temporalExpressions
	 */
	public List<String> getTemporalExpressions() {
		return temporalExpressions;
	}
	/**
	 * @param temporalExpressions the temporalExpressions to set
	 */
	public void setTemporalExpressions(List<String> temporalExpressions) {
		this.temporalExpressions = temporalExpressions;
	}
	/**
	 * @return the others
	 */
	public List<String> getOthers() {
		return others;
	}
	/**
	 * @param others the others to set
	 */
	public void setOthers(List<String> others) {
		this.others = others;
	}


}
