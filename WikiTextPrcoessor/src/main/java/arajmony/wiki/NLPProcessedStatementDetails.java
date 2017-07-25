/**
 * 
 */
package arajmony.wiki;

import java.util.Arrays;


/**
 * 
 * @author Arun
 *
 */
public class NLPProcessedStatementDetails {
	String[] normalWordTokens;
	String[] posTags;
	String[] lemmWordTokens;
	
	public NLPProcessedStatementDetails() {
		super();
	}
	public String[] getNormalWordTokens() {
		return normalWordTokens;
	}
	public void setNormalWordTokens(String[] normalWordTokens) {
		this.normalWordTokens = normalWordTokens;
	}
	public String[] getPosTags() {
		return posTags;
	}
	public void setPosTags(String[] posTags) {
		this.posTags = posTags;
	}
	public String[] getLemmWordTokens() {
		return lemmWordTokens;
	}
	public void setLemmWordTokens(String[] lemmWordTokens) {
		this.lemmWordTokens = lemmWordTokens;
	}
	
	@Override
	public String toString() {
		return "NLPProcessedStatementDetails [normalWordTokens="
				+ Arrays.toString(normalWordTokens) + ", posTags="
				+ Arrays.toString(posTags) + ", lemmWordTokens="
				+ Arrays.toString(lemmWordTokens) + "]";
	}
	
	
	
	
}






