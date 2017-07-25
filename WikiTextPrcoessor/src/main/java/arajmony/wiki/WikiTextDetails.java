/**
 * 
 */
package arajmony.wiki;

/**
 * @author Arun
 * Class to model/hold the input details in structured form
 */
public class WikiTextDetails {

	final String paragraphText;
	final String[] questions;
	final String[] answers;
	
	
	public WikiTextDetails(String paragraphText, String[] questions,
			String[] answers) {
		super();
		this.paragraphText = paragraphText;
		this.questions = questions;
		this.answers = answers;
	}


	public String getParagraphText() {
		return paragraphText;
	}


	public String[] getQuestions() {
		return questions;
	}


	public String[] getAnswers() {
		return answers;
	}
	
	
	

}
