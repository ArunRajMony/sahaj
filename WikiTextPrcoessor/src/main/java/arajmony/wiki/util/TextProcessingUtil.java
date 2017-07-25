package arajmony.wiki.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import arajmony.wiki.NLPProcessedStatementDetails;
import arajmony.wiki.WikiProcessConstants;
import arajmony.wiki.WikiTextDetails;
import arajmony.wiki.enums.POSGroupLevelEnum;
import arajmony.wiki.enums.WordTypeEnum;
import arajmony.wiki.impl.ApacheONLPProcessor;
import arajmony.wiki.intf.NLPProcessor;

/**
 * 
 * @author Arun
 *
 */
public class TextProcessingUtil {

	
    private static NLPProcessor s_nlpProcessor;  
    
    final static Logger log = Logger.getLogger(TextProcessingUtil.class);
    
	
	static {
		s_nlpProcessor = new ApacheONLPProcessor();
	}
	
	/**
	 * 
	 * @return
	 */
	public static SortedMap<Integer, String> getOrderedSentencesMapForString(
			String inputString) {

		String sentences[] = s_nlpProcessor.getSentencesFromText(inputString);
		return getStringArrayInASortedMap(sentences);
	}
	

	/**
	 * 
	 * @param sentenceNumberAndSentenceStringMap
	 * @return
	 */
	public static SortedMap<Integer, String> getOrderedLemmaSentenceMap(
			SortedMap<Integer, String> sentenceNumberAndSentenceStringMap) {
		
		String lemStrArray[] = new String[sentenceNumberAndSentenceStringMap.size()];
		
		for(Map.Entry<Integer,String> entry : sentenceNumberAndSentenceStringMap.entrySet()) {
			  Integer sentNum =entry.getKey();
			  String sentStr =  entry.getValue();
			  
			  String lemmas[] = s_nlpProcessor.getLemmaTokensForText(sentStr);
			  lemStrArray[sentNum] = String.join("", lemmas);
			  
		}
		
		return getStringArrayInASortedMap(lemStrArray);
	}
	
	
	
	
	/**
	 * 
	 * @param strArray
	 * @return
	 */
	public static  SortedMap<Integer,String> getStringArrayInASortedMap(String[] strArray){
		SortedMap<Integer,String> resMap = new TreeMap<Integer,String>();
		
		for (int i = 0; i < strArray.length; i++) {
			resMap.put(new Integer(i),strArray[i]);
		}

		return resMap;
	}
	
	
	/**
	 * 
	 * @param map
	 */
    public static <K,V> void printMap(Map<K,V> map){
		for(Map.Entry<K,V> entry : map.entrySet()) {
			  System.out.println(entry.getKey() + " => " + entry.getValue());
		}
    }

    
    /**
     * 
     * @param tokens
     * @param startIndex  - inclusive
     * @param endIndex - exclusive
     * @return
     */
    public static String getConcatenatedTokensForOffsets(String[] tokens, int startIndex, int endIndex){
    	
    	int arrLen = tokens.length;
    	
    	if(startIndex >= arrLen || endIndex >= arrLen )
    		return null;
    	
    	if(endIndex <= startIndex)
    		return null;
    	
    	StringBuffer strBuf = new StringBuffer();
    	for(int i = startIndex ; i < endIndex ; i++ )
    		strBuf.append(tokens[i]);
    	
    	
    	return strBuf.toString();
    }


    /**
     * 
     * @param sentStr
     * @return
     */
	public static String[] getLemmaTokensForSentence(String sentStr) {
		String lemmas[] = s_nlpProcessor.getLemmaTokensForText(sentStr);
		return lemmas;
	}
	

	public static String getLemmaSentenceForString(String sentStr) {
		String lemmas[] = getLemmaTokensForSentence(sentStr);
		return String.join("", lemmas);
	}
	
	
	/**
	 * 
	 * @param normalStmt
	 * @return
	 */
    public static NLPProcessedStatementDetails getNLPProcessedStatementDetails(String normalStmt){
    	NLPProcessedStatementDetails res = new NLPProcessedStatementDetails();
    	
    	String[] normalWordTokens = s_nlpProcessor.getTokensForText(normalStmt);
    	
    	res.setNormalWordTokens(normalWordTokens);
    	res.setPosTags(s_nlpProcessor.getPOSTagsForTokens(normalWordTokens));
    	res.setLemmWordTokens(s_nlpProcessor.getLemmaTokensForText(normalStmt));
    	
    	return res;
    }

	
	/**
	 * 
	 * @param inputFileRelPath
	 * @return
	 */
	public static  WikiTextDetails createWikiTextDetailsFromFile(String inputFileRelPath){
		
		WikiTextDetails wikiTextDetails=null;
		File inputFile = FileUtil.getResourceAsFile(inputFileRelPath);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(inputFile));
			
			
			String paragraphText=null;
			String[] questions = new String[WikiProcessConstants.NUM_OF_QUESTION_ANSWERS];
			String[] answers = new String[WikiProcessConstants.NUM_OF_QUESTION_ANSWERS];
			
			String currLineStr= null;
			int lineIndex =0;
			while((currLineStr = br.readLine()) != null && lineIndex <= 6){
				
				if(lineIndex >= 1 && lineIndex <= WikiProcessConstants.NUM_OF_QUESTION_ANSWERS ){
					questions[lineIndex - 1] = currLineStr;
				}else if(lineIndex == (WikiProcessConstants.NUM_OF_QUESTION_ANSWERS + 1)){
					StringTokenizer answerTokens = new StringTokenizer(currLineStr, ";");
					int answersIndex = 0;
					while(answerTokens.hasMoreTokens() && answersIndex < WikiProcessConstants.NUM_OF_QUESTION_ANSWERS){
						answers[answersIndex] = answerTokens.nextToken();
						answersIndex++;
					}
				}else if(lineIndex == 0){
					paragraphText = currLineStr;
				}
				lineIndex++;
			}
			
			wikiTextDetails = new WikiTextDetails(paragraphText, questions, answers);
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(br!= null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return wikiTextDetails;
	}



	
	
	/**
	 * 
	 * @param string
	 * @param posTags
	 * @param wordType
	 * @return
	 */
	public static List<String> getWordTokensOfPosTags(String string,List<String> posTagsToFilter,WordTypeEnum wordType) {
	
		String wordtokens[]=null;
		
		String[] normalWordTokens = s_nlpProcessor.getTokensForText(string);
		
		if(wordType == WordTypeEnum.LEMMA_WORD)
			wordtokens = s_nlpProcessor.getLemmaTokensForText(string);
		
		String posTags[] = s_nlpProcessor.getPOSTagsForTokens(normalWordTokens);
		
		List<String> res = new ArrayList<String>();
		
		for(int i =0 ; i< posTags.length;  i++){
			String currPosTag = posTags[i];
			if(posTagsToFilter.contains(currPosTag)){
				if(wordType == WordTypeEnum.NORMAL_WORD)
					res.add(normalWordTokens[i]);
				else if(wordType == WordTypeEnum.LEMMA_WORD)
					res.add(wordtokens[i]);
			}
		}
		
		return res;
	}


	/**
	 * 
	 * @param text
	 * @return
	 */
	public static  String[] getTokensForText(String text) {
		return s_nlpProcessor.getTokensForText(text);
	}


	/**
	 * 
	 * @param string
	 * @param wordTokenType
	 * @return
	 */
	public static Map<Integer, List<String>> getWordTokensForAllLevels(
			String string, WordTypeEnum wordTokenType) {
		
		Map<Integer,List<String>> posGroupLevelAndWordTokensMap = new HashMap<Integer, List<String>>();
		for (POSGroupLevelEnum pgle : POSGroupLevelEnum.values()) {
			
			List<String> applicableWordTokensForCurrLevel = getWordTokensOfPosTags(string,Arrays.asList(pgle.posTags),wordTokenType);
			posGroupLevelAndWordTokensMap.put(pgle.level, applicableWordTokensForCurrLevel);
			
		}
		
		return posGroupLevelAndWordTokensMap;
		
	}
	

	
}


