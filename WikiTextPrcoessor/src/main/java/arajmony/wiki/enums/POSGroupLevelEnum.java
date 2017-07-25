/**
 * 
 */
package arajmony.wiki.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Arun
 * @see  https://cs.nyu.edu/grishman/jet/guide/PennPOS.html
 */
public enum POSGroupLevelEnum {

	LEVEL_ONE(1, 10.0f, "JJ","JJR","JJS" , "CD" ),
	LEVEL_TWO(2, 4.0f, "VB","VBD","VBG","VBN","VBP","VBZ","RB","RBR","RBS","NN" ,"NNS" ),
	LEVEL_THREE(3, 1.7f, "NNP" , "NNPS");
	
	
	
	
	public int level;
	public float weightScore;
	public String[] posTags;
	public Map<String,Float> posTagAndWeightScoreMap = new HashMap<String,Float>();
	
	POSGroupLevelEnum(int level, float weightScore, String... posTags) {
		this.level = level;
		this.weightScore = weightScore;
		this.posTags = posTags;
		for(String posTag : posTags)
			posTagAndWeightScoreMap.put(posTag, new Float(this.weightScore));
	}
	
	
	/**
	 * 
	 * @param level
	 * @return
	 */
	public static float getWeightScoreForLevel(int level){
		float weightScore = 0.0f;
		
		for (POSGroupLevelEnum pgle : POSGroupLevelEnum.values()) {
			   if(pgle.level == level){
				   weightScore = pgle.weightScore;
				   break;
			   }
		}
		
		return weightScore;
		
	}
	
	
	/**
	 * 
	 * @param posTag
	 * @return
	 */
	public static float getWeightScoreForPOSTag(String posTag){
		float weightScore = 0.0f;
		
		for (POSGroupLevelEnum pgle : POSGroupLevelEnum.values()) {
			   if(pgle.posTagAndWeightScoreMap.get(posTag) != null) {
				   weightScore = pgle.weightScore;
				   break;
			   }
		}
		
		return weightScore;
	}
	
	
	
	
	
}
