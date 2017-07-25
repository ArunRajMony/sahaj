/**
 * 
 */
package arajmony.wiki.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import arajmony.wiki.NLPProcessedStatementDetails;
import arajmony.wiki.WikiProcessConstants;
import arajmony.wiki.WikiTextDetails;
import arajmony.wiki.enums.POSGroupLevelEnum;
import arajmony.wiki.enums.WordTypeEnum;
import arajmony.wiki.util.TextProcessingUtil;

/**
 * @author Arun
 * This class is the main class of the application, the trigger to process a wiki file and the main application logic for processing the file 
 * is all done in this class. 
 */
public class WikiParser {

	final static Logger log = Logger.getLogger(WikiParser.class);
	
	
	String m_paragraphText;
	String[] m_questions;
	String[] m_answers;

	SortedMap<Integer, String> m_sentenceNumberAndSentenceStringMap;
	SortedMap<Integer, String> m_sentenceNumberAndLemmaSentenceStringMap;
	MatchingRelatedInfo m_matchingRelatedInfo;

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		WikiParser wiki = new WikiParser();
		wiki.processTextFile("inputs/edison_3.txt");
		//wiki.processTextFile("inputs/zebras_2.txt");
		//wiki.processTextFile("inputs/iceland_1.txt");
		
	}
	
	
	
	public WikiParser() {
		super();
	}



	/**
	 * 
	 * @param inputFileRelPath
	 * @return a String array of the matched answers in the order of the corresponding question.
	 */
	public String[] processTextFile(String inputFileRelPath){
		
		return processMatching(TextProcessingUtil.createWikiTextDetailsFromFile(inputFileRelPath));
		
	}
	
	
	/**
	 * 
	 * @param wikiTextDetails
	 * @return a String array of the matched answers in the order of the corresponding question.
	 */
	private String[] processMatching(WikiTextDetails wikiTextDetails){
		
		m_paragraphText = wikiTextDetails.getParagraphText();
		m_questions = wikiTextDetails.getQuestions();
		m_answers = wikiTextDetails.getAnswers();
		String[] matchedAnswers = new String[WikiProcessConstants.NUM_OF_QUESTION_ANSWERS];

		
		m_sentenceNumberAndSentenceStringMap  = TextProcessingUtil.getOrderedSentencesMapForString(m_paragraphText);
		m_sentenceNumberAndLemmaSentenceStringMap  =  TextProcessingUtil.getOrderedLemmaSentenceMap(m_sentenceNumberAndSentenceStringMap);
		
		m_matchingRelatedInfo  =  prepareMatchingRelatedInfo(m_sentenceNumberAndSentenceStringMap,m_answers); //key is the "answer index" and the val is the list of statement numbers which has the key
		log.debug("matchingRelatedInfo : " + m_matchingRelatedInfo);
		
		
		
		/*
		 * logic :
		 * 1. for each question 
		 * 1.1 identify the best sentence which it maps to. (primarily in terms of lemma word match btw the question string and the individual sentences' string)
		 * 1.2 identify the best answer from the identified best match sentence (in some cases , a single sentence in the paragraph might be holding answers for more than one question, hence the need for this)
		 * 1.3 no need to do 1.1 and 1.2 for the last question as we can just map the only un-matched answer at that stage 
		 */
		for(int q=0; q < m_questions.length ; q++){
			
			log.debug(String.format("question # %d : %s ",q, m_questions[q]));
			log.debug(m_questions[q]);
			
			if(q != (WikiProcessConstants.NUM_OF_QUESTION_ANSWERS - 1)){
			
				// get the best matching sentence for the current question
				int bestMatchingSentenceIndex = getIndexOfBestMatchingSentence(m_questions[q]);
				log.debug(String.format("Best Matching sentence's index : %d", bestMatchingSentenceIndex ));
				log.debug(String.format("Best Matching sentence  string : %s", m_sentenceNumberAndSentenceStringMap.get(bestMatchingSentenceIndex) ));
				
				//get the best matching answer (from the un-matched answers yet) from the selected sentence
				int bestMatchingAnswersIndex = getIndexOfBestMatchingAnswerFromSentence(m_questions[q],bestMatchingSentenceIndex);
				matchedAnswers[q] = m_answers[bestMatchingAnswersIndex];
				
				log.debug("best Matching Answers Index : " + bestMatchingAnswersIndex);
				log.debug("best Matching Answer      : " + m_answers[bestMatchingAnswersIndex]);
				log.debug("unMatchedAnswerDetails : " + m_matchingRelatedInfo);
			}else{
				matchedAnswers[q]  = m_matchingRelatedInfo.getAnswersPendingMatch().get(0);//for last question just use the only one pending unmatched answer
			}
			
			log.debug(matchedAnswers[q]);
			log.debug("\n");
			
		}
		
		//log.info("\n\n\n");
		for(String matchedAnswer : matchedAnswers){
			log.info(matchedAnswer);
		}
		
		return matchedAnswers;
		
	}
	
	
	

	/**
	 * 
	 * @param question
	 * @return the index of the best matching sentence for the given question, i.e the sentence which is deemed to have the best chance for a matching answer to the question
	 */
	private  int getIndexOfBestMatchingSentence(String question) {
		
		
		SortedMap<Float,List<Integer>> scoreAndSentenceNumberMap = new TreeMap<Float,List<Integer>>(Collections.reverseOrder());
		
		Map<Integer,List<String>> lemmaLevelAndLemmasMapForQuestionStr = TextProcessingUtil.getWordTokensForAllLevels(question,WordTypeEnum.LEMMA_WORD);
			
		for (Map.Entry<Integer, String> entry : m_sentenceNumberAndLemmaSentenceStringMap.entrySet()) {
			
			int indexOfCurrLemmaSentence = entry.getKey();

			if(!m_matchingRelatedInfo.getIndexOfSentencesHavingAnswers().contains(entry.getKey())){
				continue; // no value in processing a sentence if it does not hold any answer strings. 
			}
			
			String currLemmaSentence = entry.getValue();
			
			float score = 0.0f;

			for (POSGroupLevelEnum currPOSGroup : POSGroupLevelEnum.values()) {
				
				List<String> applicableLemmasForCurrentLevel = lemmaLevelAndLemmasMapForQuestionStr.get(currPOSGroup.level);
				
				for (String currLemma : applicableLemmasForCurrentLevel) {
					if (currLemmaSentence.contains(currLemma))
						score += currPOSGroup.weightScore;
				}
			}

			List<Integer> sentenceList = scoreAndSentenceNumberMap.get(score);
			if (sentenceList == null)
				sentenceList = new ArrayList<Integer>();
			sentenceList.add(indexOfCurrLemmaSentence);

			scoreAndSentenceNumberMap.put(score, sentenceList);
		}
		
		log.debug("scoreAndSentenceNumberMap : "+ scoreAndSentenceNumberMap);
		
		List<Integer> sentencesWithHighestScore = scoreAndSentenceNumberMap.get(scoreAndSentenceNumberMap.firstKey());
		if(sentencesWithHighestScore.size() > 1){
			log.debug("more than one sentence with highest score!!!");
			
			
/*			Logic : 
 * 			for each available answer(i.e pending to be matched) find matching score with the current question
			pick the answer which gets the highest score
			for the selected answer do a substring match for each of the best matching sentences 
			return the sentence which has the selected answer as a substring
*/

			SortedMap<Float,String> scoreForPendingAnswersCurrentQuestionMatch = new TreeMap<Float,String>(Collections.reverseOrder());
			for(String yetToBeMatchedAnswer : m_matchingRelatedInfo.getAnswersPendingMatch()){
				float sco = generateScoreForWordTokenMatch(question,yetToBeMatchedAnswer,WordTypeEnum.LEMMA_WORD);
				scoreForPendingAnswersCurrentQuestionMatch.put(sco, yetToBeMatchedAnswer);
			}

			String answerWithBestMatchToCurrentQuestion = scoreForPendingAnswersCurrentQuestionMatch.get(scoreForPendingAnswersCurrentQuestionMatch.firstKey());
			int sentenceBestMatch = -1;
			
			for(Integer sentNum : sentencesWithHighestScore){
				String sentence = m_sentenceNumberAndSentenceStringMap.get(sentNum);
				if(sentence.contains(answerWithBestMatchToCurrentQuestion)){
					sentenceBestMatch = sentNum;
					break;
				}
			}
			
			log.debug("sentenceBestMatch :" + sentenceBestMatch);
			
			if(sentenceBestMatch != -1){
				return sentenceBestMatch;
			}else {
				return sentencesWithHighestScore.get(0);
			}
            
			
		}else{
			return sentencesWithHighestScore.get(0);
		}

		
		
	}
	
	



	/**
	 * 
	 * @param question  - the question (String) for which this function shall look for the answer which matches best available in the sentence corresponding to the input @param bestMatchingSentenceIndexNumber 
	 * @param bestMatchingSentenceIndexNumber - the sentence against which the given question will be matched upon. 
	 * @return the index of the best matching answer (w.r.t to the answers array)
	 */
	private int getIndexOfBestMatchingAnswerFromSentence(String question,
			int bestMatchingSentenceIndexNumber) {
		
		Map<Integer,List<String>> posGroupLevelAndNormalWordTokensMap = TextProcessingUtil.getWordTokensForAllLevels(question,WordTypeEnum.NORMAL_WORD);
		
		String normalFormOfBestMatchingSentence = m_sentenceNumberAndSentenceStringMap.get(new Integer(bestMatchingSentenceIndexNumber));
		int indexOfBestMatchingAnswer = 0;
		
		
		List<Integer> unMatchedAnswerIndexList = m_matchingRelatedInfo.getSentenceNumberAndUnMatchedAnswerIndexNumberMap().get(bestMatchingSentenceIndexNumber);
		
		if(unMatchedAnswerIndexList.size() == 1){
			indexOfBestMatchingAnswer = unMatchedAnswerIndexList.get(0);
		}else if(unMatchedAnswerIndexList.size() > 1){
			
			log.debug("greater than one match case");
			
			SortedMap<Float,Integer> scoreAndUnMatchedAnswerIndexMap = new TreeMap<Float, Integer>();
			
			for(int currUnMatchedAnswerIndex : unMatchedAnswerIndexList){
				
				float score = 0.0f;
				String unMatchedAnswer = m_answers[currUnMatchedAnswerIndex];
				log.debug(String.format("currUnMatchedAnswerIndex %d : %s" , currUnMatchedAnswerIndex,unMatchedAnswer));
				
				int startingIndexPositionOfUnMatchedAnswerStringInSentence = normalFormOfBestMatchingSentence.indexOf(unMatchedAnswer);
				//int endIndexPositionOfUnMatchedAnswerStringInSentence = startingIndexPositionOfUnMatchedAnswerStringInSentence + unMatchedAnswer.length();
				
				for(Map.Entry<Integer,List<String>> entry : posGroupLevelAndNormalWordTokensMap.entrySet()) {
					  
					int currPOSGroupLevel = entry.getKey();
					float scoreWeightToUse = POSGroupLevelEnum.getWeightScoreForLevel(currPOSGroupLevel);
					
					for(String currWord : entry.getValue()){
						int indexOfCurrWordInSentence = normalFormOfBestMatchingSentence.indexOf(currWord);
						score += Math.abs((startingIndexPositionOfUnMatchedAnswerStringInSentence - indexOfCurrWordInSentence)) * scoreWeightToUse;
					}
					
					//TODO in the above for loop , how to handle cases where the currWord appears more than once in the matching sentence ???
				}
				
				log.debug("score : "+ score);
				scoreAndUnMatchedAnswerIndexMap.put( score, currUnMatchedAnswerIndex);
				
			}
			
			indexOfBestMatchingAnswer = scoreAndUnMatchedAnswerIndexMap.get(scoreAndUnMatchedAnswerIndexMap.firstKey());
			
		}else{
			log.error("unMatchedAnswerIndexList is empty");
		}
		
		
		//updating the unMatchedAnswerDetails object
		m_matchingRelatedInfo.getSentenceNumberAndUnMatchedAnswerIndexNumberMap().put(bestMatchingSentenceIndexNumber,unMatchedAnswerIndexList);
		for(Integer containingSentenceNumber : m_matchingRelatedInfo.getUnMatchedAnswerIndexNumberAndContainingSentencesIndexMap().get(indexOfBestMatchingAnswer)){
			m_matchingRelatedInfo.getSentenceNumberAndUnMatchedAnswerIndexNumberMap().get(containingSentenceNumber).remove(new Integer(indexOfBestMatchingAnswer));
		}
		m_matchingRelatedInfo.getUnMatchedAnswerIndexNumberAndContainingSentencesIndexMap().put(indexOfBestMatchingAnswer,null);
		m_matchingRelatedInfo.getIndexOfSentencesHavingAnswers().remove(new Integer(bestMatchingSentenceIndexNumber));
		m_matchingRelatedInfo.getIndexOfAnswersPendingMatch().remove(new Integer(indexOfBestMatchingAnswer));
		m_matchingRelatedInfo.getAnswersPendingMatch().remove(m_answers[indexOfBestMatchingAnswer]);
		
		
		return indexOfBestMatchingAnswer;
	}



	/**
	 * This is a utility method which prepares some details related to matching which shall be updated as the process of matching moves forward. After each matching this object's
	 * content would be updated to reflect the current status of the various matching attributes  
	 * @param sentenceNumberAndSentenceStringMap
	 * @param answers
	 * @return instance of MatchingRelatedInfo
	 */
	private MatchingRelatedInfo prepareMatchingRelatedInfo(
			SortedMap<Integer, String> sentenceNumberAndSentenceStringMap,
			String[] answers) {
		
		
		Map<Integer, List<Integer>> sentencesIndexNumberAndUnMatchedAnswerIndexNumberMap = new HashMap<Integer, List<Integer>>();//key is the "answer index" and the val is the list of statement numbers which has the key
		Map<Integer, List<Integer>> unMatchedAnswerIndexNumberAndContainingSentencesIndexNumberMap = new HashMap<Integer, List<Integer>>();//reverse of above map
		List<Integer> indexOfSentencesHavingAnswers = new ArrayList<Integer>();
		Set<Integer> indexOfAnswersPendingMatch = new HashSet<Integer>();
		
		for(int a=0;a < answers.length ; a++){
			
			String currAnswerText = answers[a];
			
			for(Map.Entry<Integer,String> entry : sentenceNumberAndSentenceStringMap.entrySet()) {
				  Integer currSentNum =entry.getKey();
				  String currSentStr =  entry.getValue();
				  
				  if(currSentStr.contains(currAnswerText)){
					  List<Integer> unMatchedAnswersIndexNumber = sentencesIndexNumberAndUnMatchedAnswerIndexNumberMap.get(currSentNum);
					  if(unMatchedAnswersIndexNumber == null)
						  unMatchedAnswersIndexNumber = new ArrayList<Integer>();
					  unMatchedAnswersIndexNumber.add(a);
					  sentencesIndexNumberAndUnMatchedAnswerIndexNumberMap.put(currSentNum,unMatchedAnswersIndexNumber);
					  
					  List<Integer> sentenceNumbers = unMatchedAnswerIndexNumberAndContainingSentencesIndexNumberMap.get(a);
					  if(sentenceNumbers == null)
						  sentenceNumbers = new ArrayList<Integer>();
					  sentenceNumbers.add(currSentNum);
					  unMatchedAnswerIndexNumberAndContainingSentencesIndexNumberMap.put(a, sentenceNumbers);
					  
					  indexOfSentencesHavingAnswers.add(currSentNum);
				  }
			}
			
			indexOfAnswersPendingMatch.add(new Integer(a));

		}
		

		
		MatchingRelatedInfo matchingRelatedInfo = new MatchingRelatedInfo(sentencesIndexNumberAndUnMatchedAnswerIndexNumberMap, unMatchedAnswerIndexNumberAndContainingSentencesIndexNumberMap,indexOfSentencesHavingAnswers,indexOfAnswersPendingMatch);
		List<String> answerList = new ArrayList<String>();
		for(String ans:answers){
			answerList.add(ans);
		}
		matchingRelatedInfo.setAnswersPendingMatch(answerList);

		
		return matchingRelatedInfo;
	}
	
	
	
	
	

	/**
	 * 
	 * @param stmtOne the first of the two statements which shall be compared with one another
	 * @param stmtTwo the second of the two statements which shall be compared with one another
	 * @param wordType indicates whether the matching of tokens shall be based on lemma tokens or normal tokens of the provided strings.
	 * @return a score to indicate the matching degree. A higher score shall be deemed as a better match.  
	 */
	private float generateScoreForWordTokenMatch(String stmtOne, String stmtTwo,WordTypeEnum wordType ){
		
		String[] stmtOneTokensToUse = null;
		String[] stmtTwoTokensToUse = null;
		 
		NLPProcessedStatementDetails stmt1Details = TextProcessingUtil.getNLPProcessedStatementDetails(stmtOne);
		NLPProcessedStatementDetails stmt2Details = TextProcessingUtil.getNLPProcessedStatementDetails(stmtTwo);
		String[] stmt1POSTags = stmt1Details.getPosTags(); 
		
		
		if(wordType == WordTypeEnum.NORMAL_WORD){
			stmtOneTokensToUse = stmt1Details.getNormalWordTokens();
			stmtTwoTokensToUse = stmt2Details.getNormalWordTokens();
		}	else if(wordType == WordTypeEnum.LEMMA_WORD){
			stmtOneTokensToUse = stmt1Details.getLemmWordTokens();
			stmtTwoTokensToUse = stmt2Details.getLemmWordTokens();
		}

		
		float score = 0.0f;
		
		for(int i = 0 ; i < stmtOneTokensToUse.length ; i++){
			for(int j = 0 ; j < stmtTwoTokensToUse.length ; j++){
				if(stmtOneTokensToUse[i].equalsIgnoreCase(stmtTwoTokensToUse[j]))
				{
				  score += POSGroupLevelEnum.getWeightScoreForPOSTag(stmt1POSTags[i]) * (stmtOneTokensToUse[i].length());
				}
			}
		}
		
		
		return score;
	}
	
	
	
	
	
	
	
	

}

/**
 * 
 * @author Arun
 * This class holds information related to the matching process, the details available in the instance of this class shall be updated 
 * during the process of matching to reflect the current status of various matching entities.  
 */
class MatchingRelatedInfo {
	public Map<Integer, List<Integer>> sentenceNumberAndUnMatchedAnswerIndexNumberMap;
	public Map<Integer, List<Integer>> unMatchedAnswerIndexNumberAndContainingSentencesIndexMap;
	public List<Integer> indexOfSentencesHavingAnswers; // this can have duplicates (i.e. if one sentence has more than one answer)
	public Set<Integer> indexOfAnswersPendingMatch; 
	public List<String> answersPendingMatch;
	
	public MatchingRelatedInfo(
			Map<Integer, List<Integer>> sentenceNumberAndUnMatchedAnswerIndexNumberMap,
			Map<Integer, List<Integer>> unMatchedAnswerIndexNumberAndContainingSentencesIndexMap,
			List<Integer> indexOfSentencesHavingAnswers,
			Set<Integer> indexOfAnswersPendingMatch) {
		super();
		this.sentenceNumberAndUnMatchedAnswerIndexNumberMap = sentenceNumberAndUnMatchedAnswerIndexNumberMap;
		this.unMatchedAnswerIndexNumberAndContainingSentencesIndexMap = unMatchedAnswerIndexNumberAndContainingSentencesIndexMap;
		this.indexOfSentencesHavingAnswers = indexOfSentencesHavingAnswers;
		this.indexOfAnswersPendingMatch = indexOfAnswersPendingMatch;
	}


	public Map<Integer, List<Integer>> getSentenceNumberAndUnMatchedAnswerIndexNumberMap() {
		return sentenceNumberAndUnMatchedAnswerIndexNumberMap;
	}

	public Map<Integer, List<Integer>> getUnMatchedAnswerIndexNumberAndContainingSentencesIndexMap() {
		return unMatchedAnswerIndexNumberAndContainingSentencesIndexMap;
	}

	public List<Integer> getIndexOfSentencesHavingAnswers() {
		return indexOfSentencesHavingAnswers;
	}
	

	public Set<Integer> getIndexOfAnswersPendingMatch() {
		return indexOfAnswersPendingMatch;
	}
	
	


	public List<String> getAnswersPendingMatch() {
		return answersPendingMatch;
	}


	public void setAnswersPendingMatch(List<String> answersPendingMatch) {
		this.answersPendingMatch = answersPendingMatch;
	}


	@Override
	public String toString() {
		return "MatchingRelatedInfo [sentenceNumberAndUnMatchedAnswerIndexNumberMap="
				+ sentenceNumberAndUnMatchedAnswerIndexNumberMap
				+ ", unMatchedAnswerIndexNumberAndContainingSentencesIndexMap="
				+ unMatchedAnswerIndexNumberAndContainingSentencesIndexMap
				+ ", indexOfSentencesHavingAnswers="
				+ indexOfSentencesHavingAnswers
				+ ", indexOfAnswersPendingMatch="
				+ indexOfAnswersPendingMatch
				+ ", answersPendingMatch=" + answersPendingMatch + "]";
	}


	


	
	
	
}
