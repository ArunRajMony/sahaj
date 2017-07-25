/**
 * 
 */
package arajmony.wiki.intf;

/**
 * @author Arun
 * an interface to abstract the possible use of more than one NLP processing library of choice.
 */
public interface NLPProcessor {

	
	String[] getSentencesFromText(String text);
	
	String[] getLemmaTokensForText(String text);
	
	String[] getPOSTagsForTokens(String[] tokens);
	
	String[] getTokensForText(String text);
	
	String[] getLemmasForTokensAndTags(String[] tokens, String[] tags);
}
