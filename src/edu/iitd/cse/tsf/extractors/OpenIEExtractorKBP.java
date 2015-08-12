package edu.iitd.cse.tsf.extractors;

import java.util.ArrayList;
import java.util.List;

import scala.collection.Iterator;
import scala.collection.Seq;
import edu.iitd.cse.tsf.constants.OpenIEExtractionBOKBP;
import edu.knowitall.openie.Argument;
import edu.knowitall.openie.Extraction;
import edu.knowitall.openie.Instance;
import edu.knowitall.openie.OpenIE;
import edu.knowitall.tool.parse.ClearParser;
import edu.knowitall.tool.postag.OpenNlpPostagger;
import edu.knowitall.tool.srl.ClearSrl;


/**
 * @author abhishek
 *
 */
public class OpenIEExtractorKBP {

	//Components required for the annotation
	private OpenNlpPostagger postagger;
	private ClearParser clearParser;
	private ClearSrl clearSrl;
	private OpenIE openie;

	//Default Constructor to initialize all the component
	public OpenIEExtractorKBP() {
		super();
		// TODO Auto-generated constructor stub
		postagger = new OpenNlpPostagger();
		clearParser = new ClearParser(postagger);
		clearSrl = new ClearSrl();
		openie = new OpenIE(clearParser, clearSrl, false);
	}


	/**This function returns the openIE output seperated in an array of string
	 * 0-part1
	 * 1-relation
	 * 2-part2
	 * 3-temporal part
	 * 4-Confidence
	 * */
	public String[] splitOpenIEOutput(String S)
	{
		String output[] = new String[5];
		String [] dat = new String[2];
		int temp =S.indexOf('(');
		dat[0] = S.substring(0, temp-1);
		dat[1]= S.substring(temp+1, S.length()-1);

		if(!dat[1].contains("Context"))
		{
			String parts[] = dat[1].split(";");
			output[0]= parts[0];
			output[1] = parts[1];
			if(!parts[2].startsWith(" T:"))
				output[2] = parts[2];
			else
				output[3] = parts[2].substring(3);
			if(parts.length>3&& parts[3].startsWith(" T:"))
				output[3] = parts[3].substring(3);
			output[4] = dat[0];


		}
		return output;
	}

	/**
	 * Performs OpenIE Extraction
	 * */
	public Seq<Instance> runExtraction(String text) {

		Seq<Instance> openIEExtract = openie.extract(text);
		return openIEExtract;
	}


	/**
	 * Normalizes the Seq<Intances > to a List<OpenIEExtractionBO>
	 * 
	 * */
	public List<OpenIEExtractionBOKBP> getOpenIEExtractions(Seq<Instance> openIEResults, String text) {

		List<OpenIEExtractionBOKBP> openIEExtractionList = new ArrayList<OpenIEExtractionBOKBP>();


		Iterator<Instance> iterator = openIEResults.iterator();
		while(iterator.hasNext()) {
			OpenIEExtractionBOKBP openIEExtractionBO = new OpenIEExtractionBOKBP();
			openIEExtractionBO.setOriginalText(text);
			
			Instance instance = iterator.next();
			System.out.println("\t"+instance.toString());

			Extraction extraction = instance.extraction();
			System.out.println("\t"+extraction.toString());

			//Setting the arguments in the 
			if (extraction.copy$default$1().displayText() != null) {
				openIEExtractionBO.setArgument1( extraction.copy$default$1().displayText());
				System.out.println("\tArgument 1 : " + extraction.copy$default$1().displayText());
			}
			if (extraction.copy$default$2().displayText() != null) {
				
				//[ and ] are added by openIE to specify added string
				openIEExtractionBO.setRelation( extraction.copy$default$2().displayText().replace("[", "").replace("]", ""));
				System.out.println("\tRelation : "+extraction.copy$default$2().displayText());
			}

			List<String> temporalExpressionList = new ArrayList<String>();
			List<String> othersList = new ArrayList<String>();

			//extractionBO.addOpenIEExtraction(extraction);
			//System.out.println(instance.toString());
			Seq<Argument> seqArgument3 = extraction.copy$default$3();
			Iterator<Argument> seqArgument3Iterator = seqArgument3.iterator();
			//System.out.println("Argument 3 = "+seqArgument3.toString());

			boolean temporalInRelation = false;
			boolean arg2Set = false;
			while (seqArgument3Iterator.hasNext()) {
				Argument argument = seqArgument3Iterator.next();

				if(!arg2Set) {
					arg2Set=true;
					openIEExtractionBO.setArgument2(argument.text());
					System.out.println("\tArgument 2 : "+argument.text());
				}/*else if(argument.displayText().contains("T:")) {
					//Temporal normalizedDate = suTimeExtractor.getNormalizedDate(argument.text(), documemtCreationTime);

					temporalExpressionList.add(argument.text());
					System.out.println("\tTemporal Expression : "+argument.text());
					if(normalizedDate != null) {
						System.out.println("\tNormalized Date : "+normalizedDate.toString());
						normalizedDateList.add(normalizedDate.toISOString());
					} else {
						System.out.println("\tNormalized Date : NULL");
						normalizedDateList.add("----");
					}

					temporalInRelation = true;
					//extractionBO.addNormalizedDate(normalizedDate);
					//break;
				}*/else if(!argument.displayText().contains("T:")){
						othersList.add(argument.text());
				}
				
				//Adding temporal arguments to temporal list
				if(argument.displayText().contains("T:")) {
					//Temporal normalizedDate = suTimeExtractor.getNormalizedDate(argument.text(), documemtCreationTime);

					temporalExpressionList.add(argument.text());
					System.out.println("\tTemporal Expression : "+argument.text());

					temporalInRelation = true;
				}

			}

			openIEExtractionBO.setTemporalExpressions(temporalExpressionList);
			openIEExtractionBO.setOthers(othersList);
			//extractionBO.setNormalizedDateList(normalizedDateList);

			//Classify the relation type
			/*RelationClassificationOutputBO classifyOutput = relationClassifier.classify(openIEExtractionBO);
			extractionBO.setOpenIEExtraction(openIEExtractionBO);
			extractionBO.setRelationOutputBO(classifyOutput);*/

			//No temporal info for the relation
			/*if(!temporalInRelation) {
			extractionBO.addNormalizedDate(null);
		}*/
			//extractionBOList.add(extractionBO);
			openIEExtractionList.add(openIEExtractionBO);
		}

		return openIEExtractionList;
	}

}
