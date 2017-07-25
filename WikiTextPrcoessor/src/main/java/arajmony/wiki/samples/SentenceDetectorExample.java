package arajmony.wiki.samples;

import java.io.InputStream;

import arajmony.wiki.util.FileUtil;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class SentenceDetectorExample {

	public static void main(String[] args) {
		String stmt = "Iceland is a Nordic island country in the North Atlantic Ocean. It has a population of 332,529 and an area of 103,000 km2 (40,000 sq mi), making it the most sparsely populated country in Europe.[8] The capital and largest city is Reykjavík. Reykjavík and the surrounding areas in the southwest of the country are home to over two-thirds of the population. Iceland is volcanically and geologically active. The interior consists of a plateau characterised by sand and lava fields, mountains and glaciers, while many glacial rivers flow to the sea through the lowlands. Iceland is warmed by the Gulf Stream and has a temperate climate, despite a high latitude just outside the Arctic Circle. Its high latitude and marine influence still keeps summers chilly, with most of the archipelago having a tundra climate.";
		
		try (InputStream sentModelIn = FileUtil.getResourceFileStream("onlp/models/en-sent.bin")) {
			
			
			SentenceModel model = new SentenceModel(sentModelIn);
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
			String sentences[] = sentenceDetector.sentDetect(stmt);
			
			for(int i=0;i<sentences.length;i++){
				System.out.println(String.format("Sentence %d --> %s ",(i+1) , sentences[i]));
			}
			
		} catch (Exception e) {
			System.err.println("Exception : " + e.getMessage());
		}


	}

}
