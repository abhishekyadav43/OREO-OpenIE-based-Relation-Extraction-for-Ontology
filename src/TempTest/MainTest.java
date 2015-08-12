package TempTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scala.collection.Seq;
import edu.iitd.cse.tsf.constants.OpenIEExtractionBOKBP;
import edu.iitd.cse.tsf.extractors.OpenIEExtractorKBP;
import edu.knowitall.openie.Instance;


public class MainTest {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		BufferedReader reader = new BufferedReader(new FileReader("/home/abhishek/Data/TSF/instances-2011tsf/data/title.dat"));
		OpenIEExtractorKBP extractor = new OpenIEExtractorKBP();

		Map<String, Integer> relationMap = new HashMap<String, Integer>();
		
		int totalCount = 0;
		int temporalExpressionCount = 0;
		int targetDateInTemporalCount = 0;

		String s = "";
		while((s=reader.readLine()) != null) {

			String[] strings = s.split("\t");
			String normalizedString = strings[4];
			int temporalType = Integer.parseInt(strings[8]);
			
			

			if(temporalType == 2) {
				
				totalCount++;
				
				Seq<Instance> seq = extractor.runExtraction(normalizedString);
				List<OpenIEExtractionBOKBP> openIEExtractions = extractor.getOpenIEExtractions(seq, normalizedString);

				for (OpenIEExtractionBOKBP openIEExtractionBOKBP : openIEExtractions) {

					List<String> temporalExpressions = openIEExtractionBOKBP.getTemporalExpressions();

					if(temporalExpressions != null && temporalExpressions.size() > 0 ) {

						temporalExpressionCount++;
						
						for (String string : temporalExpressions) {

							if(string.contains("TARGET DATE")) {

								targetDateInTemporalCount++;
								
								String relationTerm = openIEExtractionBOKBP.getRelation();
								if(relationMap.containsKey(relationTerm.toLowerCase().trim())) {
									Integer integer = relationMap.get(relationTerm.toLowerCase().trim());
									relationMap.put(relationTerm.toLowerCase().trim(), integer+1);
								} else {
									relationMap.put(relationTerm.toLowerCase().trim(), 1);
								}
							}
						}
					}
				}


			}

		}
		
		reader.close();
		
		
		for (Entry<String, Integer> entry : relationMap.entrySet()) {
			System.out.println(entry.getKey()+"\t"+entry.getValue());
		}
		
		System.out.println("Total Count = "+totalCount);
		System.out.println("Temporalexpression = "+temporalExpressionCount);
		System.out.println("Target Date In Temporal expression count= "+targetDateInTemporalCount);
		System.out.println("Map Size = "+relationMap.size());
	}

}
