package plugin.stringresponse;

import java.util.ArrayList;

/**
 * This a farm for all the responses, it encapsulates them so they can be later
 * saved as a JSON object.
 * 
 * @author Tom Rosier(XeTK)
 */
public class ResponseList {
	// Keep a list of responses
	private ArrayList<Response> responses_ = new ArrayList<Response>();

	// Add a Response
	public void addResponse(Response replies) {
		responses_.add(replies);
	}

	// Return the list of Responses
	public ArrayList<Response> getResponses() {
		return responses_;
	}
}
