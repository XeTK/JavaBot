package plugin.tools.stringresponse;

import java.util.ArrayList;

/**
 * This is data class to handle string responses it holds the original Regex and
 * the responses that go with that Regex.
 * 
 * @author Tom Rosier(XeTK)
 */
public class Response {
	// This is the data that is held for a response to take place
	private String regex_ = "";
	private ArrayList<String> responses_ = new ArrayList<String>();

	// Getters
	public String getRegex() {
		return regex_;
	}

	public String[] getResponses() {
		return responses_.toArray(new String[0]);
	}

	// Setters
	public void setRegex(String regex) {
		this.regex_ = regex;
	}

	public void addResponce(String response) {
		this.responses_.add(response);
	}

}
