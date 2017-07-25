/**
 * 
 */
package arajmony.wiki.util;

import java.io.InputStream;

import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.lemmatizer.Lemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * @author Arun
 *
 */
public class ApacheONLPUtil {

	private static SentenceDetector s_sentenceDetector;
	private static Tokenizer s_tokenizer;
	private static POSTagger s_posTagger;
	private static Lemmatizer s_lemmatizer;
	
	//TODO are all the above instances thread safe ? 
	
	/**
	 * 
	 * @return
	 */
	public static SentenceDetector getSentenceDetector(){
		
		if(s_sentenceDetector == null){
			try (InputStream sentModelIn = FileUtil
					.getResourceFileStream("onlp/models/en-sent.bin")) {

				SentenceModel model = new SentenceModel(sentModelIn);
				s_sentenceDetector = new SentenceDetectorME(model);
			} catch (Exception e) {
				System.err.println("Exception in getSentenceDetector : " + e.getMessage());
			}
		}
		return s_sentenceDetector;
	}
	
	
	

	/**
	 * 
	 * @return
	 */
	public static Tokenizer getTokenizer(){
		
		if(s_tokenizer == null){
			try (InputStream tokenModelIn = FileUtil
					.getResourceFileStream("onlp/models/en-token.bin")) {

				TokenizerModel model = new TokenizerModel(tokenModelIn);
				s_tokenizer = new TokenizerME(model);
				
			} catch (Exception e) {
				System.err.println("Exception in getTokenizer : " + e.getMessage());
			}
		}
		return s_tokenizer;
	}
	
	
	
	
	/**
	 * 
	 * @return
	 */
	public static POSTagger getPOSTagger(){
		
		if(s_posTagger == null){
			try (InputStream posModelIn = FileUtil
					.getResourceFileStream("onlp/models/en-pos-maxent.bin")) {

				POSModel model = new POSModel(posModelIn);
				s_posTagger = new POSTaggerME(model);
				
			} catch (Exception e) {
				System.err.println("Exception in getPOSTagger : " + e.getMessage());
			}
		}
		return s_posTagger;
	}
	
	
	
	
	/**
	 * 
	 * @return
	 */
	public static Lemmatizer getLemmatizer(){
		
		if(s_lemmatizer == null){
			try (InputStream lemDictIn = FileUtil
					.getResourceFileStream("onlp/dict/en-lemmatizer.dict")) {

				s_lemmatizer = new DictionaryLemmatizer(lemDictIn);
				
			} catch (Exception e) {
				System.err.println("Exception in getLemmatizer : " + e.getMessage());
			}
		}
		return s_lemmatizer;
	}
	
	
	
}
