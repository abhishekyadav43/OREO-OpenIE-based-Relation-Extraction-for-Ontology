/**
 * 
 */
package edu.iitd.cse.tsf.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.iitd.cse.tsf.constants.KBPRelationTypeEnum;

/**
 * @author mcs132540
 * This class contains the Entity, its Type and slotfills for the entity extracted from the text.
 * 
 */
public class EntityOutputBO {
	
	String entity;
	
	KBPRelationTypeEnum relationTypeEnum;
	
	Map<String, List<String>> entitySlotFills;

	Map<String, List<String>> entitySlotFillsSentence;
	/**
	 * 
	 */
	public EntityOutputBO() {
		super();
		entity = "";
		relationTypeEnum = null;
		entitySlotFills = new HashMap<String, List<String>>();
	}

	
	/**
	 * @param entity
	 * @param relationTypeEnum
	 */
	public EntityOutputBO(String entity, KBPRelationTypeEnum relationTypeEnum) {
		super();
		this.entity = entity;
		this.relationTypeEnum = relationTypeEnum;
	}


	/**
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * @return the entitySlotFills
	 */
	public Map<String, List<String>> getEntitySlotFills() {
		return entitySlotFills;
	}

	/**
	 * @param entitySlotFills the entitySlotFills to set
	 */
	public void setEntitySlotFills(Map<String, List<String>> entitySlotFills) {
		this.entitySlotFills = entitySlotFills;
	}

	/**
	 * @return the relationTypeEnum
	 */
	public KBPRelationTypeEnum getRelationTypeEnum() {
		return relationTypeEnum;
	}

	/**
	 * @param relationTypeEnum the relationTypeEnum to set
	 */
	public void setRelationTypeEnum(KBPRelationTypeEnum relationTypeEnum) {
		this.relationTypeEnum = relationTypeEnum;
	}


	public Map<String, List<String>> getEntitySlotFillsSentence() {
		return entitySlotFillsSentence;
	}


	public void setEntitySlotFillsSentence(
			Map<String, List<String>> entitySlotFillsSentence) {
		this.entitySlotFillsSentence = entitySlotFillsSentence;
	}

	
	
	

}
