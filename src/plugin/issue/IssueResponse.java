package plugin.issue;

import java.util.List;

/**
 * This class holds data returned from github when an issue is created.
 */
public class IssueResponse {

	private String url;
	private String labels_url;
	private String comments_url;
	private String events_url;
	private String html_url;
	private int id;
	private int number;
	private String title;
	private Object user;
	private List<Object> labels;
	private String state;
	private Object assignee;
	private Object milestone;
	private int comments;
	private String created_at;
	private String updated_at;
	private String closed_at;
	private Object pull_request;
	private String body;
	private Object closed_by;

	public IssueResponse() {

	}

	public String getHtmlUrl() {
		return html_url;
	}

	public int getNumber() {
		return number;
	}

}
