/**
 * 
 */
package edu.iitd.cse.tsf.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import edu.iitd.cse.tsf.models.SentenceAnnotationBO;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * @author abhishek
 * Annotates the text with pos,ner,coreff.. using Stanford corenlp toolkit
 */
public class TextAnnotatorKBP {

	private StanfordCoreNLP stanfordPipeline;
	private Properties stanfordProperties;
	/**
	 * Initializes the member variables
	 */
	public TextAnnotatorKBP() {
		super();
		stanfordProperties = new Properties();
		//Commented to stop coref
		stanfordProperties.put("annotators", "tokenize,ssplit,pos,lemma,ner,parse,dcoref");
		//stanfordProperties.put("annotators", "tokenize,ssplit,pos,lemma,ner");
		stanfordPipeline = new StanfordCoreNLP(stanfordProperties);
	}
	/**
	 * Initializes the member variables with the specified annotations
	 */
	public TextAnnotatorKBP(String annotations) {
		super();
		stanfordProperties = new Properties();
		stanfordProperties.put("annotators", annotations);
		stanfordPipeline = new StanfordCoreNLP(stanfordProperties);
	}

	/**
	 * Performs the Stanford annotations on the text. 
	 */
	public Annotation performAnnotations(String text) {

		Annotation annotation = new Annotation(text);
		stanfordPipeline.annotate(annotation);

		return annotation;
	}

	/**
	 * Returns a list of sentences from the sanford annotations
	 * */
	public List<String> getSplittedSentence(Annotation annotation) {
		List<String> sentences = new ArrayList<String>();
		List<CoreMap> sentenceAnnotations = annotation.get(SentencesAnnotation.class);
		for (CoreMap coreMap : sentenceAnnotations) {
			sentences.add(coreMap.toString());
		}

		return sentences;
	}


	/**
	 * Extracts NER from annotation
	 * The annotation should first get processed in the Stanford pipeline
	 */
	public Map<String, String> getNER(Annotation annotation) {

		Map<String,String> tokenNERMap = new HashMap<String, String>();

		List<CoreMap> sentenceAnnotations = annotation.get(SentencesAnnotation.class);
		for (CoreMap coreMapSentence : sentenceAnnotations) {
			for(CoreLabel token :coreMapSentence.get(TokensAnnotation.class)) {
				String tokenText = token.get(TextAnnotation.class);
				String nerTag = token.get(NamedEntityTagAnnotation.class);

				//Add all except the others
				if(!nerTag.equals("O"))
					tokenNERMap.put(tokenText, nerTag);
			}
		}

		return tokenNERMap;
	}

	public List<SentenceAnnotationBO> process(String documentText) {

		List<SentenceAnnotationBO> sentenceAnnotationBOs = new ArrayList<SentenceAnnotationBO>();
		
		Annotation annotation = new Annotation(documentText);
		stanfordPipeline.annotate(annotation);

		List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);

		//List<Token> tokensList = new ArrayList<Token>();
		//List<NER> nerList = new ArrayList<NER>();
		//List<Coref> corefList = new ArrayList<Coref>();

		for (CoreMap sentence : sentences) {

			SentenceAnnotationBO sentenceAnnotationBO = new SentenceAnnotationBO();
			
			List<CoreLabel> tokenList = sentence.get(TokensAnnotation.class);
			
			sentenceAnnotationBO.setTokenList(tokenList);
			
			int sentenceStart = tokenList.get(0).beginPosition();
			int sentenceEnd = tokenList.get(tokenList.size() - 1).endPosition();

			//Adding sentences to CAS
			
			sentenceAnnotationBO.setBegin(sentenceStart);
			sentenceAnnotationBO.setEnd(sentenceEnd);
			sentenceAnnotationBO.setSentence(sentence.toString());

			String prevNERType = "O";
			String longestNER = "";
			int startNERIndex = -1;
			int endNERIndex = -1;

			for (CoreLabel token : tokenList) {


				String nerType = token.get(NamedEntityTagAnnotation.class);
				int startPos = token.beginPosition();
				int endPos = token.endPosition();
				
				//Adding NER to CAS
				if(!nerType.equalsIgnoreCase("O")) {

					if(nerType.equals(prevNERType)) {
						//The previous and current NER are of same Type
						longestNER = longestNER+" "+token.get(TextAnnotation.class).trim();
						endNERIndex = endPos;

					}else if(longestNER.length() > 0) {
						//Longest NER is not empty
						
						sentenceAnnotationBO.addNER(longestNER.toLowerCase(), prevNERType);
		
						prevNERType = nerType;
						longestNER = token.get(TextAnnotation.class).trim();
						startNERIndex = startPos;
						endNERIndex = endPos;
					}else {
						//If the longest NER is empty
						prevNERType = nerType;
						longestNER = token.get(TextAnnotation.class).trim();
						startNERIndex = startPos;
						endNERIndex = endPos;
					}

		
				}else if(longestNER.length() > 0) {
					sentenceAnnotationBO.addNER(longestNER.toLowerCase(), prevNERType);
					
					prevNERType = "O";
					longestNER = "";
					startNERIndex = -1;
					endNERIndex = -1;
				}
			}
			
			//Extracting the ProperNoun(NNP) Phrases
			Set<String> NNPphrases = new HashSet<String>();
			String phrase = "";
			for (CoreLabel token : tokenList) {
				
				if(token.get(PartOfSpeechAnnotation.class).equalsIgnoreCase("NNP")) {
					phrase+=" "+token.get(TextAnnotation.class);
				}else if(!phrase.isEmpty()) {
					NNPphrases.add(phrase);
					phrase = "";
				}
				
			}
			sentenceAnnotationBO.setProperNounSet(NNPphrases);
			//Commenting the coref for fast execution
			Map<String,Set<String>> corefMap = getCorefValues(annotation, sentenceStart, sentenceEnd);
			sentenceAnnotationBO.setCorefMap(corefMap);
			sentenceAnnotationBOs.add(sentenceAnnotationBO);
		}
		return sentenceAnnotationBOs;
	}
	
	/**
	 * 
	 * Extracts Map of coreference mentions in the text and their representative mention
	 * @param annotation
	 * @param sentenceStart
	 * @param sentenceEnd
	 * @return
	 */
	private Map<String, Set<String>> getCorefValues(Annotation annotation,
			int sentenceStart, int sentenceEnd) {
		// TODO Auto-generated method stub
		
		//Map of coref mention and its representative mention
		Map<String, Set<String>> sentenceCorefMap = new HashMap<String,Set<String>>();
		
		Map<Integer, CorefChain> coref = annotation.get(CorefChainAnnotation.class);
		
		if(coref == null || coref.size() == 0) {
			System.out.println("No coref present");
			return null;
		}
		
		for(Map.Entry<Integer, CorefChain> entry : coref.entrySet()) {
            CorefChain corefChain = entry.getValue();

            //this is because it prints out a lot of self references which aren't that useful
            if(corefChain.getMentionsInTextualOrder().size() <= 1)
                    continue;

            CorefMention representativeMention = corefChain.getRepresentativeMention();

            //Required to get the exact character position of the representative mention
            List<CoreLabel> tokens = annotation.get(SentencesAnnotation.class).get(representativeMention.sentNum-1).get(TokensAnnotation.class);
            CoreLabel startTokenRep = tokens.get(representativeMention.startIndex -1);
            CoreLabel endTokenRep = tokens.get(representativeMention.endIndex-2);

            Set<String> corefRepresentativeMentions = new HashSet<String>();
            Set<String> corefMapKeys = new HashSet<String>();
            
            for(CorefMention corefMention : corefChain.getMentionsInTextualOrder()){

                    //Skip if the corefMention and the representative mentions are the same
                    /*if(corefMention.toString().equals(representativeMention.toString())) {
                            continue;
                    }*/

                    //This is required for getting the exact start and end Index of the mentions in the document
                    List<CoreLabel> tokenList = annotation.get(SentencesAnnotation.class).get(corefMention.sentNum-1).get(TokensAnnotation.class);
                    CoreLabel startToken = tokenList.get(corefMention.startIndex -1);
                    //End token gives the next token to the phrase
                    CoreLabel endToken = tokenList.get(corefMention.endIndex-2);


                    if(startToken.get(CharacterOffsetBeginAnnotation.class) >= sentenceStart && endToken.get(CharacterOffsetEndAnnotation.class) <= sentenceEnd) {
                    	corefMapKeys.add(corefMention.mentionSpan.toLowerCase());
                    	
                    	//CorefMention is also its Representative Mention
                    	corefRepresentativeMentions.add(corefMention.mentionSpan.toLowerCase());
                    }else{
                    	corefRepresentativeMentions.add(corefMention.mentionSpan.toLowerCase());
                    }

/*String clust2 = "";
                    CoreMap coreMap2 = annotateText.get(SentencesAnnotation.class).get(corefMention.sentNum-1);
                    tokens = annotateText.get(SentencesAnnotation.class).get(corefMention.sentNum-1).get(TokensAnnotation.class);
                    //System.out.println(corefMention.startIndex+"\t"+corefMention.endIndex);
                    //Coreff resolution only for pronouns
                    boolean isCorefPRP = true;
                    for(int i = corefMention.startIndex-1; i < corefMention.endIndex-1; i++) {
                            
                            //Choose only pronoun coreffs
                            if(!tokens.get(i).get(PartOfSpeechAnnotation.class).equals("PRP")) {
                                    isCorefPRP = false;
                                    break;
                            }
                            //System.out.println(tokens.get(i)+"\t"+tokens.get(i).get(PartOfSpeechAnnotation.class));
                            clust2 += tokens.get(i).get(TextAnnotation.class) + " ";
                    }
                    if(!isCorefPRP)
                            continue;
                    
                    clust2 = clust2.trim();*/

            }
            corefRepresentativeMentions.add(representativeMention.mentionSpan.toLowerCase());
            if(corefMapKeys != null && corefMapKeys.size() > 0) {
            	
            	for (String string : corefMapKeys) {
            		sentenceCorefMap.put(string, corefRepresentativeMentions);
				}
            }
            
    }
		
		return sentenceCorefMap;
	}
	
	public static void main(String args[]) {
		TextAnnotatorKBP annotator = new TextAnnotatorKBP();
		List<SentenceAnnotationBO> list = annotator.process("Dr. Hanmin Jung works as the head of the Dept. of S/W research and chief researcher at Korea Institute of Science and Technology Information (KISTI), Korea since 2004. He received his B.S., M.S., and Ph.D. degrees in Computer Science and Engineering from POSTECH, Korea in 1992, 1994, and 2003. Previously, he was senior researcher at Electronics and Telecommunications Research Institute (ETRI), Korea, and worked as CTO at DiQuest Inc, Korea, Now, he is also adjunct professor at University of Science &amp; Technology (UST), Korea, executive director at Korea Contents Association, and committee member of ISO/IEC JTC1/SC32 and ISO/IEC JTC1/SC34. His current research interests include decision making support mainly based in the Semantic Web and text mining technologies, Big Data, information retrieval, human-computer interaction (HCI), data analytics, and natural language processing (NLP). For these research areas, over 250 papers and 200 patents have been published and created.");
		for (SentenceAnnotationBO sentenceAnnotationBO : list) {
			
			//Printing the ProperNounSet
			/*Set<String> properNounSet = sentenceAnnotationBO.getProperNounSet();
			for (String string : properNounSet) {
				System.out.println(string);
			}*/
			
			//Printing the NER's
			Map<String, String> nerMap = sentenceAnnotationBO.getNerMap();
			for (Entry<String, String> entry : nerMap.entrySet()) {
				System.out.println(entry.getKey()+"\t"+entry.getValue());
			}
			
			Map<String, Set<String>> corefMap = sentenceAnnotationBO.getCorefMap();
			for (Entry<String, Set<String>> corefMapEntry : corefMap.entrySet()) {
				String key = corefMapEntry.getKey();
				Set<String> value = corefMapEntry.getValue();
				System.out.println("Key =========== "+key);
				for (String string : value) {
					System.out.println(string);
				}
				System.out.println("=============");
			}
		}
		
	}
}
