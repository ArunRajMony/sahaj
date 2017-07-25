package arajmony.wiki.samples;

import java.io.InputStream;

import arajmony.wiki.util.FileUtil;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class LemmatizerExample {

	public static void main(String[] args) {

		String stmt1 = "It has a population of 332,529 and an area of 103,000 km2 (40,000 sq mi), making it the most sparsely populated country in Europe.";
		String stmt2 = "Reykjavík and the surrounding areas in the southwest of the country are home to over two-thirds of the population.";
		String stmt3 = "whats the capital and largest city of Iceland?";
		
		String stmt = stmt3;
		
		try (InputStream modelIn = FileUtil.getResourceFileStream("onlp/models/en-token.bin")) {
			TokenizerModel model = new TokenizerModel(modelIn);
			Tokenizer tokenizer = new TokenizerME(model);
			String tokens[] = tokenizer.tokenize(stmt);
			//System.out.println("tokens :" + Arrays.toString(tokens));
			
			
			//POS tagging 
			InputStream posModelIn = FileUtil.getResourceFileStream("onlp/models/en-pos-maxent.bin");
			POSModel posModel = new POSModel(posModelIn);
			POSTaggerME posTaggerME = new POSTaggerME(posModel);
			String tags[] = posTaggerME.tag(tokens);
			
			
			InputStream lemDictIn = FileUtil.getResourceFileStream("onlp/dict/en-lemmatizer.dict");
			DictionaryLemmatizer dictionaryLemmatizer = new DictionaryLemmatizer(lemDictIn);
			
			String lemmas[] = dictionaryLemmatizer.lemmatize(tokens, tags);
			
			System.out.println("WORD -POSTAG : LEMMA");
			for(int i=0;i<tokens.length;i++){
				System.out.println(tokens[i] + " -" + tags[i] + " : " + lemmas[i]);
			}
			
			
		} catch (Exception e) {
			System.err.println("Exception : " + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	
	

}
