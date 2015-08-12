package edu.iitd.cse.tsf.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;


import edu.iitd.cse.tsf.constants.KBPRelationTypeEnum;
import edu.iitd.cse.tsf.models.QueryBO;

public class QueryParser {

	private String queryFilePath;
	
	
	public String getQueryFilePath() {
		return queryFilePath;
	}


	public void setQueryFilePath(String queryFilePath) {
		this.queryFilePath = queryFilePath;
	}


	public List<QueryBO> processXMLQuery() {
		
		List<QueryBO> queryBOList = new ArrayList<QueryBO>();
		
		try {
			 
			File fXmlFile = new File(queryFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
		 
			doc.getDocumentElement().normalize();
		 
		 
			NodeList nList = doc.getElementsByTagName("query");
		 
		 
			for (int temp = 0; temp < nList.getLength(); temp++) {
		 
				Node nNode = nList.item(temp);
		 
		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					
					QueryBO queryBO = new QueryBO();
		 
					Element eElement = (Element) nNode;
		 
					queryBO.setId(eElement.getAttribute("id"));
					queryBO.setEntity(eElement.getElementsByTagName("name").item(0).getTextContent());
					String entType = eElement.getElementsByTagName("enttype").item(0).getTextContent();
					if(entType.equals("PER")) {
						queryBO.setRelationTypeEnum(KBPRelationTypeEnum.PERSON);
					} else if(entType.equals("ORG")) {
						queryBO.setRelationTypeEnum(KBPRelationTypeEnum.ORGANIZATION);
					}
					queryBOList.add(queryBO);
				}
			}
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		return queryBOList;
	}
	/**
	 * @param args
	 */
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		QueryParser parser = new QueryParser();
		parser.setQueryFilePath("/home/mtech/mcs132540/Downloads/tac2013_queries.xml");
		List<QueryBO> queryBOList = parser.processXMLQuery();
		for (QueryBO queryBO : queryBOList) {
			System.out.println(queryBO.getId()+"\t"+queryBO.getEntity()+"\t"+queryBO.getRelationTypeEnum().toString());
		}

	}*/

}
