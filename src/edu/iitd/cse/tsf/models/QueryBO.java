package edu.iitd.cse.tsf.models;

import edu.iitd.cse.tsf.constants.KBPRelationTypeEnum;


/**
 * @author abhishek
 *
 */
public class QueryBO {

	private String id;
	
	private String entity;
	
	private KBPRelationTypeEnum relationTypeEnum;

	/**
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * @param entity
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * @return
	 */
	public KBPRelationTypeEnum getRelationTypeEnum() {
		return relationTypeEnum;
	}

	/**
	 * @param relationTypeEnum
	 */
	public void setRelationTypeEnum(KBPRelationTypeEnum relationTypeEnum) {
		this.relationTypeEnum = relationTypeEnum;
	}
	
	
}
