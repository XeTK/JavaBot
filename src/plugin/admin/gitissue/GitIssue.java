package plugin.admin.gitissue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;

import core.menu.AuthGroup;
import core.menu.MenuItem;
import core.plugin.Plugin;
import core.utils.IRCException;

/**
 * Allows admin users to create a new bug on github's issue tracker.
 *
 * @author Tom Leaman (tom@tomleaman.co.uk)
 */
public class GitIssue extends Plugin {
	
	private static final String CMD_BUG = "bug";
	
	private static final String HLP_BUG = String.format("%s <one_line_bug_report>", CMD_BUG);

	private static final String AUTH_TOKEN_FILE = "auth_token";
	private static final String GITHUB_URL      = "https://api.github.com";
	private static final String REPO_OWNER      = "XeTK";
	private static final String REPO_NAME       = "JavaBot";

	private String authToken;
	private boolean isLoaded = false;

	private String[] dependencies_ = {};

	public String[] getDependencies() {
		return dependencies_;
	}

	public boolean hasDependencies() {
		return (dependencies_.length > 0);
	}

	public GitIssue() {
		try {
			authToken = loadAuthToken(AUTH_TOKEN_FILE);
		} catch (FileNotFoundException e) {
			System.err.println("No auth token file found in " + AUTH_TOKEN_FILE);
			System.err.println("Issue plugin failed to load");
			return;
		}

		if (!authToken.equals(new String()))
			isLoaded = true;
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
			isLoaded = false;
			line = new String();
			e.printStackTrace();
		}

		return line;
	}

	// TODO fix Exceptions
	private void createIssue(String message, String user) throws Exception {
		String channel = channel_.getChannelName();
		
		if (message.length() <= 0) {
			irc.sendPrivmsg(channel, user + ": " + HLP_BUG);
		}
		String issueBody = ":octocat: This message was generated automatically by "
				+ user
				+ " in "
				+ channel
				+ ". Once confirmed, please remove `unconfirmed` tag.";
		String jsonContent = "{\"title\":\"" + message + "\""
				+ ",\"body\":\"" + issueBody + "\""
				+ ",\"labels\":[\"bug\", \"unconfirmed\"]}";

		try {
			URL endPoint = new URL(GITHUB_URL + "/repos/" + REPO_OWNER + "/" + REPO_NAME + "/issues");
			HttpsURLConnection conn = (HttpsURLConnection) endPoint.openConnection();

			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "token " + authToken);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.getOutputStream().write(jsonContent.getBytes("UTF-8"));

			int responseCode = conn.getResponseCode();
			// FIXME This exception cannot be raised from within this class
			// I think the custom class loader is doing odd things!
			if (responseCode >= 400) {
				throw new IssueException("Failed to create issue (" + responseCode + ").");
			}

			StringBuffer response = new StringBuffer();
			String line = null;
			
			BufferedReader in = new BufferedReader(new InputStreamReader( conn.getInputStream()));
			
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			in.close();

			// send issue url to user
			Gson parser = new Gson();
			IssueResponse responseData = (IssueResponse) parser.fromJson(response.toString(), IssueResponse.class);
			
			irc.sendPrivmsg(channel, user + ": " + "Issue #" + responseData.getNumber() + " created: " + responseData.getHtmlUrl());
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IRCException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		return "Issue";
	}

	@SuppressWarnings("serial")
	private class IssueException extends Exception {

		public IssueException(String msg) {
			super(msg);
		}
	}

	@Override
	public void getMenuItems(MenuItem rootItem) {
		MenuItem pluginRoot = rootItem;
		
		MenuItem issueSetBug = new MenuItem(CMD_BUG, rootItem, 1, AuthGroup.REGISTERD){
			@Override
			public void onExecution(String args, String username) { 
				if (!isLoaded)
					return;
				
				try {
					createIssue(args, username);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public String onHelp() {
				return HLP_BUG;
			}
		};

		pluginRoot.addChild(issueSetBug);
		
		rootItem = pluginRoot;
	}

}
