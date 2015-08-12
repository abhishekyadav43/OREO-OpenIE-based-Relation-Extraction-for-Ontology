package edu.iitd.cse.tsf.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;


public class TipsterGazetteerProcessor {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		Set<String> setCity = new HashSet<String>(); 
		Set<String> setCountry = new HashSet<String>(); 
		Set<String> setProvince = new HashSet<String>(); 
		String filePath = "Resources/KBP/TipsterGazetteer.txt";

		String strLine="";
		BufferedReader reader = new BufferedReader(new FileReader(filePath));				

		while((strLine = reader.readLine()) != null) {
			//Find Pattern Split String 
			String parts[] = strLine.split("\\)");
			for(int k = 0; k<parts.length;k++){
				String subparts[] = parts[k].split("\\(");
				if(subparts.length == 2){
					String strName = subparts[0].trim().toLowerCase();
					String strNameType = subparts[1].split(" ")[0].trim();
					switch (strNameType) {
					case "CITY" :{
						if(!setCity.contains(strName))
							setCity.add(strName);
						break;
					}
					case "COUNTRY" :{
						if(!setCountry.contains(strName))
							setCountry.add(strName);
						break;
					}
					case "PROVINCE" :{
						if(!setProvince.contains(strName))
							setProvince.add(strName);
						break;
					}
					default :{
						// do nothing
						break;
					}

					}

				}//end if

				//	}//end innerfor
			}//end outerfor


		}

		writeSet("Resources/KBP/contriesComplete.lst", setCountry);
		writeSet("Resources/KBP/provinceComplete.lst", setProvince);
		writeSet("Resources/KBP/citiesComplete.lst", setCity);

		reader.close();
	}

	/**
	 * Writes elements of set to a output file in a format required by Yaml to load
	 * @param outputFilePath
	 * @param set
	 * @throws IOException 
	 */
	public static void writeSet(String outputFilePath, Set<String> set) throws IOException {

		PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath));
		for (String string : set) {
			writer.write("- "+string+"\n");
		}
		writer.close();
	}

}
