/**
 * 
 */
package edu.iitd.cse.tsf.models;

/**
 * @author mcs132540
 *
 */
public class SolrBO {
	
	//Solr id of the text
	private String id;
	
	//The text 
	private String text;

	public SolrBO() {
		super();
		// TODO Auto-generated constructor stub
		this.id = null;
		this.text = null;
	}

	/**
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public String getText() {
		return text;
	}

	/**
	 * 
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	
	

}
