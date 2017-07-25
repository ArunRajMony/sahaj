/**
 * 
 */
package arajmony.wiki.impl;

import opennlp.tools.lemmatizer.Lemmatizer;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.tokenize.Tokenizer;
import arajmony.wiki.intf.NLPProcessor;
import arajmony.wiki.util.ApacheONLPUtil;

/**
 * @author Arun
 *
 */
public class ApacheONLPProcessor implements NLPProcessor {

	@Override
	public String[] getSentencesFromText(String text) {
		SentenceDetector sentenceDetector = ApacheONLPUtil
				.getSentenceDetector();
		String sentences[] = sentenceDetector.sentDetect(text);
		return sentences;
	}

	@Override
	public String[] getPOSTagsForTokens(String[] tokens) {
		POSTagger posTagger = ApacheONLPUtil.getPOSTagger();
		String tags[] = posTagger.tag(tokens);
		return tags;
	}

	@Override
	public String[] getTokensForText(String text) {
		Tokenizer tokenizer = ApacheONLPUtil.getTokenizer();
		String tokens[] = tokenizer.tokenize(text);
		return tokens;
	}

	@Override
	public String[] getLemmaTokensForText(String text) {

		String tokens[] = getTokensForText(text);
		String tags[] = getPOSTagsForTokens(tokens);
		String lemmas[] = getLemmasForTokensAndTags(tokens, tags);

		return lemmas;
	}
	

	@Override
	public String[] getLemmasForTokensAndTags(String[] tokens, String[] tags) {
		Lemmatizer lemmatizer = ApacheONLPUtil.getLemmatizer();
		String lemmas[] = lemmatizer.lemmatize(tokens, tags);
		// post-processing of lemmas . looking for 'O's
		for (int i = 0; i < lemmas.length; i++) {
			if (lemmas[i].equals("O"))
				lemmas[i] = tokens[i];
		}
		return lemmas;
	}

}
