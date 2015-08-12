/**
 * 
 */
package edu.iitd.cse.tsf.extractors;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
//import edu.stanford.nlp.pipeline.PTBTokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.time.SUTime.Temporal;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;

/**
 * @author abhishek
 *
 */
public class SUTimeExtractorKBP {
	private Properties properties;
	private AnnotationPipeline pipeline;
	public SUTimeExtractorKBP() {
		super();
		properties = new Properties();
		pipeline = new AnnotationPipeline();
		//pipeline.addAnnotator(new PTBTokenizerAnnotator(false));
		pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
		pipeline.addAnnotator(new POSTaggerAnnotator(false));
		pipeline.addAnnotator(new TimeAnnotator("sutime", properties));
	}
	/**
	 * Normalizes the temporal expression w.r.t to the Document Creation time
	 * */
	public Temporal getNormalizedDate(String temporalExpression, String DocCreationTime) {

		Temporal temporal = null;

		Annotation annotation = new Annotation(temporalExpression);
		annotation.set(CoreAnnotations.DocDateAnnotation.class, DocCreationTime);
		pipeline.annotate(annotation);

		List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
		if(timexAnnsAll != null && timexAnnsAll.size() > 0) {
			temporal = timexAnnsAll.get(0).get(TimeExpression.Annotation.class).getTemporal();
		}
		return temporal;
	}

}
