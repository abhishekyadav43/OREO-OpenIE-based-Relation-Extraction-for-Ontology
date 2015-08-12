/**
 * 
 */
package edu.iitd.cse.tsf.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ling.CoreLabel;

/**
 * @author mcs132540
 *This is the BO for holding the Stanford corenlp annotations for the sentence.
 *
 */
public class SentenceAnnotationBO {

	private String sentence;
	
	private int begin;
	
	private int end;
	
	private List<CoreLabel> tokenList;
	
	//Contains the string and its nerValue
	private Map<String, String> nerMap;
	
	//Contains coref string and its representative value
	private Map<String, Set<String>> corefMap;
	
	//Set of properNouns in the sentence
	private Set<String> properNounSet;

	
	public SentenceAnnotationBO() {
		super();
		// TODO Auto-generated constructor stub
		this.sentence = null;
		this.begin = -1;
		this.end = -1;
		
		nerMap = new HashMap<String, String>();
		corefMap = new HashMap<String, Set<String>>();
	}

	/**
	 * 
	 * @return
	 */
	public String getSentence() {
		return sentence;
	}

	/**
	 * 
	 * @param sentence
	 */
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	/**
	 * 
	 * @return
	 */
	public int getBegin() {
		return begin;
	}

	/**
	 * 
	 * @param begin
	 */
	public void setBegin(int begin) {
		this.begin = begin;
	}

	/**
	 * 
	 * @return
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * 
	 * @param end
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * 
	 * @return
	 */
	public List<CoreLabel> getTokenList() {
		return tokenList;
	}

	/**
	 * 
	 * @param tokenList
	 */
	public void setTokenList(List<CoreLabel> tokenList) {
		this.tokenList = tokenList;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, String> getNerMap() {
		return nerMap;
	}

	/**
	 * 
	 * @param nerMap
	 */
	public void setNerMap(Map<String, String> nerMap) {
		this.nerMap = nerMap;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, Set<String>> getCorefMap() {
		return corefMap;
	}

	/**
	 * 
	 * @param corefMap
	 */
	public void setCorefMap(Map<String, Set<String>> corefMap) {
		this.corefMap = corefMap;
	}
	
	/**
	 * 
	 * @param nerKey
	 * @param nerValue
	 */
	public void addNER(String nerKey, String nerValue) {
		nerMap.put(nerKey, nerValue);
	}
	
	/**
	 * 
	 * @param corefKey
	 * @param representativeValues
	 */
	public void addCoref(String corefKey, Set<String> representativeValues) {
		
		corefMap.put(corefKey, representativeValues);
	}

	/**
	 * 
	 * @return
	 */
	public Set<String> getProperNounSet() {
		return properNounSet;
	}

	/**
	 * 
	 * @param properNounSet
	 */
	public void setProperNounSet(Set<String> properNounSet) {
		this.properNounSet = properNounSet;
	}
	
	
}
