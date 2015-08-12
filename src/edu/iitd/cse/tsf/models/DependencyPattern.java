package edu.iitd.cse.tsf.models;

import java.util.ArrayList;
import java.util.List;

public class DependencyPattern {

	String patternType;

	//Pattern is a sequence of Node and edges
	List<String> pattern = new ArrayList<String>();

	public String getPatternType() {
		return patternType;
	}

	public void setPatternType(String patternType) {
		this.patternType = patternType;
	}

	public List<String> getPattern() {
		return pattern;
	}

	public void setPattern(List<String> pattern) {
		this.pattern = pattern;
	}
	
	
	
	
}
