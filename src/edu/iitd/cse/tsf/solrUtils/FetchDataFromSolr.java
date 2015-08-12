package edu.iitd.cse.tsf.solrUtils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
//import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.thoughtworks.xstream.XStream;

import edu.iitd.cse.tsf.classifyRelations.ExtractionEngine;
import edu.iitd.cse.tsf.models.EntityOutputBO;
import edu.iitd.cse.tsf.models.SolrBO;
import edu.stanford.nlp.pipeline.Annotation;

public class FetchDataFromSolr {

	private String solrURLWebDoc;
	private String solrURLNewsWire;
	private String solrURLDiscussionForum;	

	SolrServer serverNewsWire;

	public void setSolrURL(String solrURLWebDoc, String solrURLNewsWire, String solrURLDiscussionForum) {
		this.solrURLWebDoc = solrURLWebDoc;
		this.solrURLNewsWire = solrURLNewsWire;
		this.solrURLDiscussionForum = solrURLDiscussionForum;
	}


	/**
	 * 
	 * @return
	 */
	public String getSolrURLWebDoc() {
		return solrURLWebDoc;
	}


	/**
	 * 
	 * @param solrURLWebDoc
	 */
	public void setSolrURLWebDoc(String solrURLWebDoc) {
		this.solrURLWebDoc = solrURLWebDoc;
	}


	/**
	 * 
	 * @return
	 */
	public String getSolrURLNewsWire() {
		return solrURLNewsWire;
	}


	/**
	 * 
	 * @param solrURLNewsWire
	 */
	public void setSolrURLNewsWire(String solrURLNewsWire) {
		this.solrURLNewsWire = solrURLNewsWire;
		serverNewsWire = new HttpSolrServer(solrURLNewsWire);
	}


	/**
	 * 
	 * @return
	 */
	public String getSolrURLDiscussionForum() {
		return solrURLDiscussionForum;
	}


	/**
	 * 
	 * @param solrURLDiscussionForum
	 */
	public void setSolrURLDiscussionForum(String solrURLDiscussionForum) {
		this.solrURLDiscussionForum = solrURLDiscussionForum;
	}


	/**
	 * 
	 * @param solrQuery
	 * @param start
	 * @param numRows
	 * @return
	 */
	public List<SolrBO> getSolrResultsWebDoc(String solrQuery, int start, int numRows) {

		List<SolrBO> solrBOList = new ArrayList<SolrBO>();

		SolrServer serverWebDoc = new HttpSolrServer(solrURLWebDoc);
		SolrQuery query = new SolrQuery();
		query.setRows(numRows);
		query.setQuery(solrQuery);
		query.setStart(start);

		QueryResponse response =null;
		try {
			response = serverWebDoc.query(query);
		} catch (SolrServerException e) {
			System.out.println("Error fetching results for query "+query+"\t"+start+"\t"+numRows);
			e.printStackTrace();
		}

		if(response != null) {

			SolrDocumentList documentList = response.getResults();
			for (SolrDocument solrDocument : documentList) {

				SolrBO solrBO = new SolrBO();
				solrBO.setId(solrDocument.get("id").toString());
				solrBO.setText(solrDocument.get("webdoc").toString());

				solrBOList.add(solrBO);
			}

		}

		return solrBOList;
	}

	public List<SolrBO> getSolrResultsNewsWire(String solrQuery, int start, int numRows) {
		List<SolrBO> solrBOList = new ArrayList<SolrBO>();

		//SolrServer serverNewsWire = new HttpSolrServer(solrURLNewsWire);
		SolrQuery query = new SolrQuery();
		query.setRows(numRows);
		query.setQuery(solrQuery);
		query.setStart(start);

		QueryResponse response =null;
		try {
			response = serverNewsWire.query(query);
		} catch (SolrServerException e) {
			System.out.println("Error fetching NewsWire results for query "+query+"\t"+start+"\t"+numRows);
			e.printStackTrace();
		}

		if(response != null) {

			SolrDocumentList documentList = response.getResults();
			for (SolrDocument solrDocument : documentList) {

				SolrBO solrBO = new SolrBO();
				solrBO.setId(solrDocument.get("id").toString());
				solrBO.setText(solrDocument.get("doc").toString());

				solrBOList.add(solrBO);
			}

		}
		//serverNewsWire.shutdown();

		return solrBOList;
	}

	public List<SolrBO> getSolrResultsDiscussionForum(String solrQuery, int start, int numRows) {
		List<SolrBO> solrBOList = new ArrayList<SolrBO>();

		SolrServer serverDiscussionForum = new HttpSolrServer(solrURLDiscussionForum);
		SolrQuery query = new SolrQuery();
		query.setRows(numRows);
		query.setQuery(solrQuery);
		query.setStart(start);

		QueryResponse response =null;
		try {
			response = serverDiscussionForum.query(query);
		} catch (SolrServerException e) {
			System.out.println("Error fetching Discussion Forum results for query "+query+"\t"+start+"\t"+numRows);
			e.printStackTrace();
		}

		if(response != null) {

			SolrDocumentList documentList = response.getResults();
			for (SolrDocument solrDocument : documentList) {

				SolrBO solrBO = new SolrBO();
				solrBO.setId(solrDocument.get("id").toString());
				solrBO.setText(solrDocument.get("post").toString());

				solrBOList.add(solrBO);
			}

		}

		return solrBOList;
	}


	/*public static List<String> splitSentence(FetchDataFromSolr dataFromSolr,ExtractionEngine engine, String entity, String slotFill) {

		List<String> sentencesList = new ArrayList<String>();
		
		List<SolrBO> otherTextHits = dataFromSolr.getSolrResultsNewsWire("doc:\""+entity+"\" AND doc:\""+slotFill+"\"",0, 100);
		for (SolrBO solrBO2 : otherTextHits) {

			String text = solrBO2.getText();
			sentencesList.add(text);
			Annotation annotations = engine.annotator.performAnnotations(text);
			List<String> splittedSentence = engine.annotator.getSplittedSentence(annotations);
			for (String string : splittedSentence) {

				if(string.contains(entity) && string.contains(slotFill)){
					otherSentences.add(string);
				}
			}

		}

		return null;
	}*/
	
	
	
	/*public static void main(String[] args) {
		
		FetchDataFromSolr dataFromSolr = new FetchDataFromSolr();
		dataFromSolr.setSolrURLNewsWire("http://panini2.cse.iitd.ernet.in:9192/solr");
		
		
		int start = 0;
		int end = 100;
		int fetchedDataSize = 0;

		do{
			List<SolrBO> solrResultsNewsWire = dataFromSolr.getSolrResultsNewsWire("doc:\"John\"", start, 100);
			for (SolrBO solrBO : solrResultsNewsWire) {
				System.out.println(solrBO.getId()+"\t"+solrBO.getText());

			}

			fetchedDataSize = solrResultsNewsWire.size();
			start+= 100;
			end+= 100;

		}while(fetchedDataSize > 99);
	}*/
	public static void main(String[] args) throws IOException {

		FetchDataFromSolr dataFromSolr = new FetchDataFromSolr();
		dataFromSolr.setSolrURLNewsWire("http://panini2.cse.iitd.ernet.in:9192/solr");

		XStream xstream = new XStream();
		xstream.alias("otherSentence", String.class);
		xstream.alias("Entity", EntityOutputBOXML.class);
		xstream.addImplicitCollection(EntityOutputBOXML.class, "otherSentences");

		ExtractionEngine engine = new ExtractionEngine("Resources/KBP/kbpDefault.properties");

		int start = 0;
		int end = 100;
		int fetchedDataSize = 0;
		int slotfillExtractionCount = 0;

		do{
			List<SolrBO> solrResultsNewsWire = dataFromSolr.getSolrResultsNewsWire("doc:\"Robert\"", start, 100);
			for (SolrBO solrBO : solrResultsNewsWire) {
				System.out.println(solrBO.getId());
				List<EntityOutputBO> outputBOs = engine.processText(solrBO.getText(),false);

				for (EntityOutputBO entityOutputBO : outputBOs) {

					Map<String, List<String>> entitySlotFills = entityOutputBO.getEntitySlotFills();
					Map<String, List<String>> entitySlotFillsSentence = entityOutputBO.getEntitySlotFillsSentence();


					for (Entry<String,List<String>> relMap : entitySlotFills.entrySet()) {


						List<String> sentences = entitySlotFillsSentence.get(relMap.getKey());
						List<String> value = relMap.getValue();

						for (int i=0;i<value.size();i++) {
							System.out.println(value.get(i)+"\t"+sentences.get(i));
							EntityOutputBOXML entityOutputBOXML = new EntityOutputBOXML();
							entityOutputBOXML.setEntity(entityOutputBO.getEntity());
							entityOutputBOXML.setRelationType(relMap.getKey());
							entityOutputBOXML.setSlotfill(value.get(i));
							entityOutputBOXML.setOriginalSentence(sentences.get(i));
							List<String> otherSentences = new ArrayList<String>();
							
							slotfillExtractionCount++;

							String entity = entityOutputBO.getEntity();
							String slotFill = value.get(i);

							List<SolrBO> otherTextHits = dataFromSolr.getSolrResultsNewsWire("doc:\""+entity+"\" AND doc:\""+slotFill+"\"",0, 100);
							System.out.println("\n\nSize of fetched other sentence "+otherTextHits.size());
							for (SolrBO solrBO2 : otherTextHits) {

								String text = solrBO2.getText();
								//otherSentences.add(text);
								Annotation annotations = engine.annotator.performAnnotations(text);
								List<String> splittedSentence = engine.annotator.getSplittedSentence(annotations);
								for (String string : splittedSentence) {

									if(string.toLowerCase().contains(entity) && string.toLowerCase().contains(slotFill)){
										otherSentences.add(string);
									}
								}

							}

							entityOutputBOXML.setOtherSentences(otherSentences);

							System.out.println(xstream.toXML(entityOutputBOXML));
							BufferedWriter writer = new BufferedWriter(new FileWriter("/home/abhishek/SlotFillFiles/Robert/File"+slotfillExtractionCount+".xml"));
							writer.write(xstream.toXML(entityOutputBOXML));
							writer.close();
						}
					}
				}
			}

			fetchedDataSize = solrResultsNewsWire.size();
			start+= 100;
			end+= 100;

		}while(fetchedDataSize > 99);

	}
	/**
	 * @param args
	 * @throws SolrServerException 
	 * @throws MalformedURLException 
	 */
	/*public static void main(String[] args) throws SolrServerException, MalformedURLException {
		// TODO Auto-generated method stub
		SolrServer server = new HttpSolrServer("http://10.208.23.201:8983/solr");
		SolrQuery query = new SolrQuery();
		query.setRows(20);
		query.setQuery("*:*");
		//query.setStart(10);

		QueryResponse response = server.query(query);
		SolrDocumentList documentList = response.getResults();

		for (SolrDocument solrDocument : documentList) {
			String id = solrDocument.get("id").toString();
			String string = solrDocument.get("webdoc").toString();
			System.out.println(id);
			System.out.println(string+"\n");
		}

		FetchDataFromSolr dataFromSolr = new FetchDataFromSolr();
		dataFromSolr.setSolrURLWebDoc("http://10.208.23.201:8983/solr");
		dataFromSolr.setSolrURLNewsWire("http://10.237.23.28:8983/solr");
		dataFromSolr.setSolrURLDiscussionForum("http://10.237.23.28:9893/solr");

		List<SolrBO> solrResultsDiscussionForum = dataFromSolr.getSolrResultsDiscussionForum("post:\"Ramazan Bashardost\"", 0, 100);
		for (SolrBO solrBO : solrResultsDiscussionForum) {
			System.out.println(solrBO.getId());
		}

		List<SolrBO> solrResultsNewsWire = dataFromSolr.getSolrResultsNewsWire("doc:\"Ramazan Bashardost\"", 0, 100);
		for (SolrBO solrBO : solrResultsNewsWire) {
			System.out.println(solrBO.getId());
		}

		dataFromSolr.getSolrResultsWebDoc("webdoc:\"Ramazan Bashardost\"", 0, 100);
		for (SolrBO solrBO : solrResultsNewsWire) {
			System.out.println(solrBO.getId());
		}
	}*/

}
