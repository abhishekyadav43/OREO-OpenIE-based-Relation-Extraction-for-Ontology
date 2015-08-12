/**
 * 
 */
package edu.iitd.cse.tsf.solrUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author abhishek
 *
 */
public class EntityOutputBOXML {
	
	String entity;
	
	String relationType;
	
	String Slotfill;
	
	String originalSentence;
	
	List<String> otherSentences;

	public EntityOutputBOXML() {
		super();
		// TODO Auto-generated constructor stub
		otherSentences = new ArrayList<String>();
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	public String getSlotfill() {
		return Slotfill;
	}

	public void setSlotfill(String slotfill) {
		Slotfill = slotfill;
	}

	public String getOriginalSentence() {
		return originalSentence;
	}

	public void setOriginalSentence(String originalSentence) {
		this.originalSentence = originalSentence;
	}

	public List<String> getOtherSentences() {
		return otherSentences;
	}

	public void setOtherSentences(List<String> otherSentences) {
		this.otherSentences = otherSentences;
	}
	
	
	

}
