/**
 * 
 */
package edu.iitd.cse.tsf.dependencyPattern;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.spec.PSource;

import org.ho.yaml.Yaml;

import edu.iitd.cse.tsf.constants.PatternConstantEnumKBP;
import edu.iitd.cse.tsf.models.DependencyPattern;
import edu.iitd.cse.tsf.normalizer.KeywordTagger;
import edu.iitd.cse.tsf.normalizer.PreProcessTextNormalization;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;

/**
 * @author abhishek
 *
 */
public class DependencyAnnotation {

	//KeywordTagger keywordTagger;

	//String propertyFilePath = "/home/abhishek/workspace_project_part2/RelationClassificationUsingPattern_ModifiedRuleLang/Resources/KBP/kbpDefault.properties";

	List<DependencyPattern> patternList;

	//Properties props;

	//StanfordCoreNLP pipeline;

	public DependencyAnnotation(String propertyFilePath, String patternFilePath) {
		super();
		// TODO Auto-generated constructor stub
		/*props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
		pipeline = new StanfordCoreNLP(props);*/



		//keywordTagger = new KeywordTagger();
		patternList = new ArrayList<DependencyPattern>();

		/*Properties properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(propertyFilePath);
			properties.load(inputStream);

			//Loading lists
			keywordTagger.loadJobTitles(properties.getProperty("jobTitleFilePath"));
			keywordTagger.loadHeadJobTitles(properties.getProperty("headJobTitleFilePath"));
			keywordTagger.loadCountry(properties.getProperty("countriesFilePath"));
			keywordTagger.loadCity(properties.getProperty("citiesFilePath"));
			keywordTagger.loadStateOrProvince(properties.getProperty("statesOrProvincesFilePath"));
			keywordTagger.loadNationality(properties.getProperty("nationalitiesFilePath"));
			keywordTagger.loadSchool(properties.getProperty("schoolFilePath"));
			keywordTagger.loadCrime(properties.getProperty("crimesFilePath"));
			keywordTagger.loadReligion(properties.getProperty("religionsFilePath"));
			keywordTagger.loadProduct(properties.getProperty("productsFilePath"));
			keywordTagger.loadTechnology(properties.getProperty("technologyFilePath"));
		} catch(Exception e) {
			e.printStackTrace();
		}*/


		try {
			patternList = loadDependencyPatterns(patternFilePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

/*	public Annotation runPipeline(String text) {

		Annotation document = new Annotation(text);

		pipeline.annotate(document);

		return document;
	}*/


	/**
	 * Loads tab seperated dependency patterns
	 * */
	public List<DependencyPattern> loadDependencyPatterns(String filePath) throws FileNotFoundException {

		List<DependencyPattern> dependencyPatternList = new ArrayList<DependencyPattern>();
		List<String> patternStringList;

		//InputStream inputStream = new FileInputStream(new File(path));
		InputStream inputStream = new FileInputStream(filePath);
		patternStringList = (ArrayList<String>)Yaml.load(inputStream);

		for (String string : patternStringList) {
			DependencyPattern dependencyPattern = new DependencyPattern();
			String[] patternSplits = string.split("\t");
			List<String> pat = new ArrayList<String>();

			dependencyPattern.setPatternType(patternSplits[0]);
			for(int i=1;i<patternSplits.length;i++) {
				pat.add(patternSplits[i].trim());
			}
			dependencyPattern.setPattern(pat);

			dependencyPatternList.add(dependencyPattern);
		}

		return dependencyPatternList;
	}

	public List<SemanticGraphEdge> printShortestPath(SemanticGraph dependencies, String source, String target) {

		String[] splitSource = source.split(" ");
		String[] splitTarget = target.split(" ");

		List<SemanticGraphEdge> shortestDirectedPathEdges = null;
		int shortestDirectedPathEdgesSize =Integer.MAX_VALUE;

		for (String sourceString : splitSource) {
			for (String targetString : splitTarget) {

				IndexedWord sourceIndex = null;
				IndexedWord targetIndex = null;

				for (IndexedWord indexedWord : dependencies.vertexListSorted()) {

					if(indexedWord.originalText().equalsIgnoreCase(sourceString)) {
						sourceIndex = indexedWord;
					}else if (indexedWord.originalText().equalsIgnoreCase(targetString)) {
						targetIndex = indexedWord;
					}
				}


				List<SemanticGraphEdge> undirectedPathEdges = dependencies.getShortestUndirectedPathEdges(sourceIndex, targetIndex);
				if(undirectedPathEdges != null && undirectedPathEdges.size()>0 && undirectedPathEdges.size() < shortestDirectedPathEdgesSize) {

					shortestDirectedPathEdges = undirectedPathEdges;
					shortestDirectedPathEdgesSize = shortestDirectedPathEdges.size();

				}
			}
		}

		/*for (SemanticGraphEdge semanticGraphEdge : shortestDirectedPathEdges) {

			System.out.println(semanticGraphEdge.getDependent().toString()+"--"+semanticGraphEdge.getRelation().toString()+"--->"+semanticGraphEdge.getGovernor().toString());
		}*/

		return shortestDirectedPathEdges;
	}


	/**
	 * Renormalizes the text
	 * PatternConstant#Num --> PatternConstant
	 * 
	 * */
	private String renormalizeText(String text) {
		// TODO Auto-generated method stub

		String renormalizedText = " "+text+" ";

		for (PatternConstantEnumKBP patternConstantEnum : PatternConstantEnumKBP.values()) {

			Pattern pattern = Pattern.compile("\\s+"+patternConstantEnum.toString()+"#[0-9]+\\s+");
			Matcher matcher = pattern.matcher(renormalizedText);

			renormalizedText = matcher.replaceAll(" "+patternConstantEnum+" ");
		}
		return renormalizedText.trim();
	}


	/**
	 * 
	 * converts the compounded node to String
	 * */
	public String compoundedNodesToString(List<IndexedWord> compoundNodes) {
		String result = "";
		for (IndexedWord indexedWord : compoundNodes) {
			result+=indexedWord.originalText()+" ";
		}

		return result.trim();
	}

	public String matchDependencyPattern(List<IndexedWord> nodes, List<String> pattern, SemanticGraph dependencies) {

		String slotFill = "";

		if(nodes == null || nodes.size() ==0)
			return null;

		String nodePattern = pattern.get(0).trim();
		String edgePattern = "";
		if(pattern.size() > 1)
			edgePattern = pattern.get(1);

		/*if(nodePattern.equals("SLOTFILL")) {
			return nodes.get(0).originalText();
		}*/

		//No edge pattern means that the current node is the last node and possible slotfill
		if(edgePattern == null || edgePattern.length() == 0) {

			for(int i=0;i<nodes.size();i++) {
				/*String originalText = nodes.get(i).originalText();
				List<IndexedWord> compoundNodes = getEntireCompoundNodes(nodes.get(i), dependencies);
				String completeText = compoundedNodesToString(compoundNodes);

				String normalizedCompleteText = keywordTagger.normalizeString(" "+completeText.toLowerCase()+" ", null);
				normalizedCompleteText = renormalizeText(normalizedCompleteText);*/
				IndexedWord indexedWord2 = nodes.get(i);
				IndexedWord nodeByIndex = dependencies.getNodeByIndex(indexedWord2.index());
				String[] split = nodeByIndex.originalText().split(":");
				String originalText = split[0];
				String completeText = split[1];
				String normalizedCompleteText = split[2];
				List<IndexedWord> compoundNodes = getEntireCompoundNodes(nodes.get(i), dependencies);


				/*if(node.originalText().equalsIgnoreCase(nodePattern))
				positiveNodes.add(node);*/
				if(normalizedCompleteText.trim().equals(nodePattern.trim())/*normalizedCompleteText.trim().contains(nodePattern.trim())*/) {
					return completeText;
				}

				//NER
				for (IndexedWord indexedWord : compoundNodes) {
					if(indexedWord.ner().equals(nodePattern))
						return completeText;
				}

				//Just the node match(sometimes the word may be prefixed or postfixed with something)
				if(originalText.equalsIgnoreCase(nodePattern)) {
					return completeText;
				}
			}
			/*if(nodes.get(0).ner().equals(nodePattern))
				return completeText;*/
			return null;
		}

		//Getting all node that satisfies the node pattern
		List<IndexedWord> positiveNodes = new ArrayList<IndexedWord>();
		if(nodePattern.equals("TARGETENTITY") || nodePattern.equals("ANY")){
			positiveNodes.addAll(nodes);

		}else{
			for (IndexedWord node : nodes) {

				/*String originalText = node.originalText();
				List<IndexedWord> compoundNodes = getEntireCompoundNodes(node, dependencies);
				String completeText = compoundedNodesToString(compoundNodes);
				String normalizedCompleteText = keywordTagger.normalizeString(" "+completeText.toLowerCase()+" ", null);
				normalizedCompleteText = renormalizeText(normalizedCompleteText);*/
				IndexedWord nodeByIndex = dependencies.getNodeByIndex(node.index());
				String[] split = nodeByIndex.originalText().split(":");
				String originalText = split[0];
				String completeText = split[1];
				String normalizedCompleteText = split[2];

				/*if(node.originalText().equalsIgnoreCase(nodePattern))
					positiveNodes.add(node);*/
				if(normalizedCompleteText.trim().equals(nodePattern.trim())/*normalizedCompleteText.trim().contains(nodePattern.trim())*/)
					positiveNodes.add(node);
				else if(node.ner().equals(nodePattern)) {
					positiveNodes.add(node);
				}else if(originalText.equalsIgnoreCase(nodePattern)) {
					positiveNodes.add(node);
				}


			}
		}

		for (IndexedWord indexedWord : positiveNodes) {


			//Getting and matching all the edges
			List<SemanticGraphEdge> outEdgesSorted = dependencies.getOutEdgesSorted(indexedWord);
			List<SemanticGraphEdge> positiveOutEdge = new ArrayList<SemanticGraphEdge>();
			for (SemanticGraphEdge semanticGraphEdge : outEdgesSorted) {
				if(semanticGraphEdge.getRelation().getShortName().split(":")[0].equalsIgnoreCase(edgePattern.split(":")[0]))
					positiveOutEdge.add(semanticGraphEdge);
			}

			List<SemanticGraphEdge> incomingEdgesSorted = dependencies.getIncomingEdgesSorted(indexedWord);
			List<SemanticGraphEdge> positiveIncomingEdge = new ArrayList<SemanticGraphEdge>();
			for (SemanticGraphEdge semanticGraphEdge : incomingEdgesSorted) {
				if(semanticGraphEdge.getRelation().getShortName().split(":")[0].equalsIgnoreCase(edgePattern.split(":")[0]))
					positiveIncomingEdge.add(semanticGraphEdge);
			}

			//Getting the next nodes from the edges
			List<IndexedWord> nextNodeList = new ArrayList<IndexedWord>();
			for (SemanticGraphEdge semanticGraphEdge : positiveIncomingEdge) {

				nextNodeList.add(semanticGraphEdge.getGovernor());
			}
			for (SemanticGraphEdge semanticGraphEdge : positiveOutEdge) {
				nextNodeList.add(semanticGraphEdge.getDependent());
			}


			//Getting the subPattern
			ArrayList<String> newPatternList = new ArrayList<String>(pattern.subList(2, pattern.size()));

			//Recursing
			slotFill = matchDependencyPattern(nextNodeList, newPatternList, dependencies);
			if(slotFill != null && slotFill.length() > 1)
				break;
		}

		return slotFill;

	}

	public List<IndexedWord> getEntireCompoundNodes(IndexedWord indexWord, SemanticGraph dependencies) {

		List<IndexedWord> compoundedNodesList = new ArrayList<IndexedWord>();

		IndexedWord rightMostIndexWord = null;

		//If the indexWord is not the rightMost word in the compound phrase
		List<SemanticGraphEdge> incomingEdgesSorted = dependencies.getIncomingEdgesSorted(indexWord);
		for (SemanticGraphEdge semanticGraphEdge : incomingEdgesSorted) {

			if(semanticGraphEdge.getRelation().getShortName().equalsIgnoreCase("compound")) {

				rightMostIndexWord = semanticGraphEdge.getGovernor();
				break;
			}
		}

		//Handling if the index word itself is the rightmost node
		if(rightMostIndexWord == null) {

			List<SemanticGraphEdge> outEdgesSorted = dependencies.getOutEdgesSorted(indexWord);
			for (SemanticGraphEdge semanticGraphEdge : outEdgesSorted) {

				if(semanticGraphEdge.getRelation().getShortName().equalsIgnoreCase("compound")) {

					rightMostIndexWord = indexWord;
					break;
				}
			}
		}

		if(rightMostIndexWord != null){
			//If compound phrase exist for the indexword
			List<SemanticGraphEdge> outEdgesSorted = dependencies.getOutEdgesSorted(rightMostIndexWord);
			for (SemanticGraphEdge semanticGraphEdge : outEdgesSorted) {

				if(semanticGraphEdge.getRelation().getShortName().equalsIgnoreCase("compound")) {
					compoundedNodesList.add(semanticGraphEdge.getDependent());
				}
			}
			compoundedNodesList.add(rightMostIndexWord);

		}else {
			//No compound phrase exist for the indexword
			compoundedNodesList.add(indexWord);
		}

		return compoundedNodesList;
	}


	/**
	 * DependencyPatternProcessing for KBP
	 * */
	public List<String> processKBP(String entity, Annotation annotation, PreProcessTextNormalization textNormalizer, Map<String, String> nerMap) {

		List<String> result = new ArrayList<String>();
		String targetEntity = " "+entity.trim()+" ";

		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {

			SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
			NormalizeNodesKBP(dependencies, textNormalizer, nerMap);

			//Finding the starting nodes
			List<IndexedWord> startingNodes = new ArrayList<IndexedWord>();
			List<IndexedWord> vertexListSorted = dependencies.vertexListSorted();
			for (IndexedWord indexedWord : vertexListSorted) {
				if(targetEntity.toLowerCase().contains(" "+indexedWord.originalText().split(":")[0].toLowerCase()+" "))
					startingNodes.add(indexedWord);
			}

			for (int i=0;i<patternList.size();i++) {
				String slotFill = matchDependencyPattern(startingNodes, patternList.get(i).getPattern(), dependencies);

				if(slotFill != null && slotFill.length() > 0)
					result.add(sentence.toString()+"\t"+patternList.get(i).getPatternType()+"\t"+slotFill+"\t"+i);
			}

		}

		return result;
	}


	/**
	 * Original
	 * */
	/*public List<String> process(String text, String entity) {

		List<String> result = new ArrayList<String>();
		String targetEntity = " "+entity+" ";

		Annotation annotation = runPipeline(text);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {

			SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);


			//Normalizing the nodes in the dependency graph
			normalizeNodes(dependencies);

			//Finding the starting nodes
			List<IndexedWord> startingNodes = new ArrayList<IndexedWord>();
			List<IndexedWord> vertexListSorted = dependencies.vertexListSorted();
			for (IndexedWord indexedWord : vertexListSorted) {
				if(targetEntity.toLowerCase().contains(" "+indexedWord.originalText().split(":")[0].toLowerCase()+" "))
					startingNodes.add(indexedWord);
			}

			for (int i=0;i<patternList.size();i++) {
				String slotFill = matchDependencyPattern(startingNodes, patternList.get(i).getPattern(), dependencies);

				if(slotFill != null && slotFill.length() > 0)
					result.add(sentence.toString()+"\t"+patternList.get(i).getPatternType()+"\t"+slotFill+"\t"+i);
			}

		}

		return result;

	}*/

	//Normalizes node content with fprmat "originalText:completeCompoundedText:normalizedText"
	/*private void normalizeNodes(SemanticGraph dependencies) {
		// TODO Auto-generated method stub

		List<String> normalizedTextList = new ArrayList<String>();

		for (IndexedWord indexedWord : dependencies.vertexListSorted()) {
			String originalText = indexedWord.originalText();
			List<IndexedWord> compoundNodes = getEntireCompoundNodes(indexedWord, dependencies);
			String completeText = compoundedNodesToString(compoundNodes);
			String normalizedCompleteText = keywordTagger.normalizeString(" "+completeText.toLowerCase()+" ", null);
			normalizedCompleteText = renormalizeText(normalizedCompleteText);

			String newOriginalText = originalText.trim()+":"+completeText.trim()+":"+normalizedCompleteText.trim();
			//indexedWord.setOriginalText(newOriginalText);
			normalizedTextList.add(newOriginalText);
		}

		//Setting the values in the nodes
		int i=0;
		for (IndexedWord indexedWord : dependencies.vertexListSorted()) {
			indexedWord.setOriginalText(normalizedTextList.get(i));
			i++;
		}
	}*/

	/**
	 * Node text normalizer for KBP
	 * */
	private void NormalizeNodesKBP(SemanticGraph dependencies,
			PreProcessTextNormalization textNormalizer,
			Map<String, String> nerMap) {
		// TODO Auto-generated method stub

		List<String> normalizedTextList = new ArrayList<String>();

		for (IndexedWord indexedWord : dependencies.vertexListSorted()) {
			String originalText = indexedWord.originalText();
			List<IndexedWord> compoundNodes = getEntireCompoundNodes(indexedWord, dependencies);
			String completeText = compoundedNodesToString(compoundNodes);
			//String normalizedCompleteText = keywordTagger.normalizeString(" "+completeText.toLowerCase()+" ", null);
			Map<String,List<String>> tagMapTemp = new HashMap<String, List<String>>();
			String normalizedCompleteText = textNormalizer.performTextNormalization(completeText.toLowerCase(), nerMap, tagMapTemp);
			normalizedCompleteText = renormalizeText(normalizedCompleteText);
			
		//	System.out.println("@@@@@@@@@ normalized Text "+normalizedCompleteText);

			String newOriginalText = originalText.trim()+":"+completeText.trim()+":"+normalizedCompleteText.trim();
			//System.out.println("@@@@@@@@@ new Original Text  "+newOriginalText);
			//indexedWord.setOriginalText(newOriginalText);
			normalizedTextList.add(newOriginalText);
		}

		//Setting the values in the nodes
		int i=0;
		for (IndexedWord indexedWord : dependencies.vertexListSorted()) {
			indexedWord.setOriginalText(normalizedTextList.get(i));
			
			//System.out.println(indexedWord.originalText()+";;;;;;;;;;;;;;;;");
			i++;
		}
	}
	
	
	public static void main(String[] args) throws IOException {

		DependencyAnnotation depAnnotations = new DependencyAnnotation("/home/abhishek/workspace_project_part2/RelationClassificationUsingPattern_ModifiedRuleLang/Resources/KBP/kbpDefault.properties","/home/abhishek/workspace-project-test/DependencyPathMatching/resources/dependencyPathPatternFile");
		/*Annotation annotation = depAnnotations.runPipeline("Generalissimo Chiang Kai-shek was commissioned a colonel in the United States Air Force Reserve.");

		//Annotation annotation = depAnnotations.runPipeline("The command of the Chinese National Revolutionary Army was directed by the National Military Commission ( ) , chaired by Generalissimo Chiang Kai-shek during the Second Sino-Japanese War and World War II .");
		//Annotation annotation = depAnnotations.runPipeline("The last remaining assets of bankrupt Russian oil company Yukos, including its headquarters in Moscow, were sold at auction for nearly 3.9 billion U.S. dollars Friday .");
		//depAnnotations.prettyPrint(annotation);
		//String pat = "TAREGETENTITY--nmod--chaired--acl--SLOTFILL";
		//String pat = "TARGETENTITY--nsubjpass--commissioned--dobj--JOBTITLE--nmod--ORGANIZATION";
		String pat = "TARGETENTITY--nsubjpass--married--nmod--PERSON";
		List<String> pattern = new ArrayList<String>();
		for (String string : pat.split("--")) {
			pattern.add(string);
		}

		String targetEntity = " Generalissimo Chiang Kai-shek ";
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {

			SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);



			//Finding the starting nodes
			List<IndexedWord> startingNodes = new ArrayList<IndexedWord>();
			List<IndexedWord> vertexListSorted = dependencies.vertexListSorted();
			for (IndexedWord indexedWord : vertexListSorted) {
				if(targetEntity.toLowerCase().contains(" "+indexedWord.originalText().toLowerCase()+" "))
					startingNodes.add(indexedWord);
			}

			//System.out.println(startingNodes.get(0).ner());
			String slotFill = depAnnotations.matchDependencyPattern(startingNodes, pattern, dependencies);
			System.out.println(slotFill);


		}*/

		/*List<String> results = depAnnotations.process("Generalissimo Chiang Kai-shek was commissioned a colonel in the United States Air Force Reserve.", "Generalissimo Chiang Kai-shek");
		for (String string : results) {
			System.out.println(string);
		}

		List<String> list = depAnnotations.process("Hayhurst graduated from Indiana University at Bloomington in 1964 .", "Hayhurst");
		for (String string : list) {
			System.out.println(string);
		}*/


		/*Annotation annotation = depAnnotations.runPipeline("McGregor is survived by his wife, Lori, and four children, daughters Jordan, Taylor and Landri, and a son, Logan. ");

		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);

			Set<IndexedWord> vertexSet = dependencies.vertexSet();
			for (IndexedWord indexedWord : vertexSet) {
				indexedWord.setOriginalText("TEST");
			}

			Set<IndexedWord> vertexSet2 = dependencies.vertexSet();
			for (IndexedWord indexedWord : vertexSet2) {
				System.out.println(indexedWord.originalText());
			}
		}*/


		//BufferedReader reader = new BufferedReader(new FileReader("/home/abhishek/Desktop/depRuleTestInputOrg"));
		/*BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String s= "";
		while((s=reader.readLine())!=null) {
			String[] split = s.split("\t");
			List<String> list = depAnnotations.process(split[1], split[0]);
			for (String string : list) {
				System.out.println(string);
			}
			System.out.println("-----");
		}*/
	}
}
