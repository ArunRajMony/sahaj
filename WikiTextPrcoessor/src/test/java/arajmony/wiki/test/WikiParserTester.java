/**
 * 
 */
package arajmony.wiki.test;


import org.junit.Assert;
import org.junit.Test;

import arajmony.wiki.impl.WikiParser;

/**
 * @author Arun
 *
 */
public class WikiParserTester {

	private WikiParser wikiParser;
	
	@Test
	public void testEdison1() {
		
		String filePath = "inputs/edison_1.txt";
		
		String[] validateArray = new String[5];
		validateArray[0] = "Thomas Alva Edison";
		validateArray[1] = "the phonograph, the motion picture camera, and the long-lasting, practical electric light bulb";
		validateArray[2] = "he is often credited with the creation of the first industrial research laboratory";
		validateArray[3] = "practical electric light bulb";
		validateArray[4] = "Dubbed \"The Wizard of Menlo Park\"";
		
		wikiParser = new WikiParser();
		
		Assert.assertArrayEquals(filePath,validateArray,wikiParser.processTextFile(filePath));
	}
	
	
	@Test
	public void testEdison2() {
		
		String filePath = "inputs/edison_2.txt";
		
		String[] validateArray = new String[5];
		validateArray[1] = "Thomas Alva Edison";
		validateArray[0] = "the phonograph, the motion picture camera, and the long-lasting, practical electric light bulb";
		validateArray[4] = "he is often credited with the creation of the first industrial research laboratory";
		validateArray[3] = "practical electric light bulb";
		validateArray[2] = "Dubbed \"The Wizard of Menlo Park\"";
		
		wikiParser = new WikiParser();
		
		Assert.assertArrayEquals(filePath,validateArray,wikiParser.processTextFile(filePath));
	}
	
	
	@Test
	public void testEdison3() {
		
		String filePath = "inputs/edison_3.txt";
		
		String[] validateArray = new String[5];
		validateArray[1] = "Thomas Alva Edison";
		validateArray[0] = "the phonograph, the motion picture camera, and the long-lasting, practical electric light bulb";
		validateArray[4] = "he is often credited with the creation of the first industrial research laboratory";
		validateArray[3] = "practical electric light bulb";
		validateArray[2] = "Dubbed \"The Wizard of Menlo Park\"";
		
		wikiParser = new WikiParser();
		
		Assert.assertArrayEquals(filePath,validateArray,wikiParser.processTextFile(filePath));
	}
	
	
	@Test
	public void testZebras1() {
		
		String filePath = "inputs/zebras_1.txt";
		
		String[] validateArray = new String[5];
		validateArray[0] = "Grévy's zebra and the mountain zebra";
		validateArray[1] = "aims to breed zebras that are phenotypically similar to the quagga";
		validateArray[2] = "horses and donkeys";
		validateArray[3] = "the plains zebra, the Grévy's zebra and the mountain zebra";
		validateArray[4] = "subgenus Hippotigris";
		
		wikiParser = new WikiParser();
		
		Assert.assertArrayEquals(filePath,validateArray,wikiParser.processTextFile(filePath));
	}
	
	@Test
	public void testZebras2() {
		
		String filePath = "inputs/zebras_2.txt";
		
		String[] validateArray = new String[5];
		validateArray[1] = "Grévy's zebra and the mountain zebra";
		validateArray[0] = "aims to breed zebras that are phenotypically similar to the quagga";
		validateArray[4] = "horses and donkeys";
		validateArray[3] = "the plains zebra, the Grévy's zebra and the mountain zebra";
		validateArray[2] = "subgenus Hippotigris";
		
		wikiParser = new WikiParser();
		
		Assert.assertArrayEquals(filePath,validateArray,wikiParser.processTextFile(filePath));
	}
	
	
	@Test
	public void testIceland1() {
		
		String filePath = "inputs/iceland_1.txt";
		
		String[] validateArray = new String[5];
		validateArray[0] = "the North Atlantic Ocean";
		validateArray[1] = "the archipelago having a tundra climate";
		validateArray[2] = "capital and largest city is Reykjavík";
		validateArray[3] = "population of 332,529";
		validateArray[4] = "Reykjavík and the surrounding areas in the southwest";
		
		wikiParser = new WikiParser();
		
		Assert.assertArrayEquals(filePath,validateArray,wikiParser.processTextFile(filePath));
	}
	
	@Test
	public void testIceland2() {
		
		String filePath = "inputs/iceland_2.txt";
		
		String[] validateArray = new String[5];
		validateArray[2] = "the North Atlantic Ocean";
		validateArray[0] = "the archipelago having a tundra climate";
		validateArray[1] = "capital and largest city is Reykjavík";
		validateArray[4] = "population of 332,529";
		validateArray[3] = "Reykjavík and the surrounding areas in the southwest";
		
		wikiParser = new WikiParser();
		
		Assert.assertArrayEquals(filePath,validateArray,wikiParser.processTextFile(filePath));
	}

	
	@Test
	public void testIceland3() {
		
		String filePath = "inputs/iceland_3.txt";
		
		String[] validateArray = new String[5];
		validateArray[2] = "the North Atlantic Ocean";
		validateArray[0] = "the archipelago having a tundra climate";
		validateArray[4] = "capital and largest city is Reykjavík";
		validateArray[1] = "population of 332,529";
		validateArray[3] = "Reykjavík and the surrounding areas in the southwest";
		
		wikiParser = new WikiParser();
		
		Assert.assertArrayEquals(filePath,validateArray,wikiParser.processTextFile(filePath));
	}
}
