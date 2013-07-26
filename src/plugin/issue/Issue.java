package plugin.issue;
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

import core.Channel;
import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.Plugin;
import core.utils.Details;
import core.utils.IRC;
import core.utils.IRCException;

/**
 * Allows admin users to create a new bug on github's issue tracker.
 * @author Tom Leaman (tom@tomleaman.co.uk)
 */
public class Issue extends Plugin {

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

  public void onMessage(Message message) throws Exception {
    if (!loaded)
      return;

    if (message.getMessage().startsWith(".bug ") && details.isAdmin(message.getUser()))
      createIssue(message);
  }

  public String getHelpString() {
    return "ISSUE: .bug <one_line_bug_report>";
  }

  // TODO fix Exceptions
  private void createIssue(Message message) throws Exception {
    // Should remove ".bug " from the start of the message
    String issueTitle = message.getMessage().substring(5);
    if (issueTitle.length() <= 0) {
      irc.sendPrivmsg(message.getChannel(), message.getUser() + ": " +
          getHelpString());
    }
    String issueBody = ":octocat: This message was generated automatically by " +
                       message.getUser() + " in " + message.getChannel() +
                       ". Once confirmed, please remove `unconfirmed` tag.";
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
      // FIXME This exception cannot be raised from within this class
      // I think the custom class loader is doing odd things!
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

  public String toString() {
    return "Issue";
  }

  private class IssueException extends Exception {

    public IssueException(String msg) {
      super(msg);
    }
  }

}
