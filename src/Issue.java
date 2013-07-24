import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;

import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.helpers.IRCException;
import core.plugin.Plugin;
import core.utils.Details;
import core.utils.IRC;

/**
 * Allows admin users to create a new bug on github's issue tracker.
 * @author Tom Leaman (tom@tomleaman.co.uk)
 */
public class Issue implements Plugin {

	private static final String AUTH_TOKEN_FILE = "auth_token";
	private static final String GITHUB_URL = "https://api.github.com";
	private static final String REPO_OWNER = "XeTK";
	private static final String REPO_NAME = "JavaBot";

	private Details details = Details.getInstance();
	private IRC irc = IRC.getInstance();

	private String authToken;
	private boolean loaded = false;

	public Issue() {
		try {
			authToken = loadAuthToken(AUTH_TOKEN_FILE);
		} catch (FileNotFoundException e) {
			System.err.println("No auth token file found in " + AUTH_TOKEN_FILE);
			System.err.println("Issue plugin failed to load");
		}

		if (!authToken.equals(new String()))
			loaded = true;
	}

	private String loadAuthToken(String filename) throws FileNotFoundException {
		File f = new File(filename);
		BufferedReader in = new BufferedReader(new FileReader(f));

		String line = new String();
		try {
			line = in.readLine();
			in.close();
		} catch (IOException e) {
			// tin foil hats a-plenty
			loaded = false;
			line = new String();
			e.printStackTrace();
		}

		return line;
	}

	@Override
	public void onMessage(Message message) throws Exception {
		if (!loaded)
			return;

		if (message.getMessage().startsWith(".bug") && details.isAdmin(message.getUser()))
			createIssue(message);
	}

	@Override
	public String getHelpString() {
		return "ISSUE: .bug <one_line_bug_report>";
	}

	private void createIssue(Message message) throws IssueException {
		String issueTitle = message.getMessage().substring(5);
		String issueBody = "This message was generated automatically by " +
											 message.getUser() + " in " + message.getChannel() +
											 ". Once confirmed, please remove `unconfirmed` tag. :octocat:";
		String jsonContent = "{\"title\":\"" + issueTitle + "\"" +
												 ",\"body\":\"" + issueBody + "\"" +
												 ",\"labels\":[\"bug\", \"unconfirmed\"]}";

		try {
			URL endpoint = new URL(
				GITHUB_URL + "/repos/" + REPO_OWNER + "/" + REPO_NAME + "/issues");
			HttpsURLConnection conn = (HttpsURLConnection) endpoint.openConnection();

			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "token " + authToken);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.getOutputStream().write(jsonContent.getBytes("UTF-8"));

			int responseCode = conn.getResponseCode();
			if (responseCode >= 400) {
				throw new IssueException(
						"Failed to create issue (" + responseCode + ").");
			}

			StringBuffer response = new StringBuffer();
			String line = null;
			BufferedReader in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			in.close();

			// send issue url to user
			Gson parser = new Gson();
			IssueResponse responseData =
				(IssueResponse)parser.fromJson(response.toString(), IssueResponse.class);
			irc.sendPrivmsg(message.getChannel(), message.getUser() + ": " +
				"Issue #" + responseData.getNumber() +
				" created: " + responseData.getHtmlUrl());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IRCException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void rawInput(String input) {

	}

	@Override
	public void onCreate(String create) {

	}

	@Override
	public void onTime() throws Exception {

	}

	@Override
	public void onJoin(Join join) throws Exception {

	}

	@Override
	public void onQuit(Quit quit) throws Exception {

	}

	@Override
	public void onKick(Kick kick) throws Exception {

	}

	@Override
	public String name() {
		return this.toString();
	}

	@Override
	public String toString() {
		return "Issue";
	}

	private class IssueException extends Exception {

		public IssueException(String msg) {
			super(msg);
		}

	}

}
