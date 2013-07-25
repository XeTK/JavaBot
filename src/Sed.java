import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack;

import core.Channel;
import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.Plugin;
import core.utils.Colour;
import core.utils.Details;
import core.utils.IRC;

/**
 * Sed plugin.
 *
 * Handles messages of the form:
 * s/<search>/<replacement>/
 * s/<search>/<replacement>
 * <username>: s/<search>/<replacement>/
 * <username>: s/<search>/<replacement>
 */
public class Sed implements Plugin {

  /**
   * The max number of Message objects stored per user.
   */
  private static final int CACHE_SIZE = 10;
  /**
   * The ASCII SOH character.
   */
  private static final char ASCII_SOH = (char) 1;

  /**
   * The first group of the sed regex.
   */
  private static final int SED_USERNAME_GROUP = 1;
  /**
   * The second group of the sed regex.
   */
  private static final int SED_SEARCH_GROUP = 2;
  /**
   * The third group of the sed regex.
   */
  private static final int SED_REPLACEMENT_GROUP = 3;

  /**
   * Details instance.
   */
  private Details details = Details.getInstance();
  /**
   * IRC instance.
   */
  private IRC irc = IRC.getInstance();

  /**
   * Stores Messages objects per-user.
   */
  private Map<String, Stack<Message>> cache =
    new HashMap<String, Stack<Message>>();

  @Override
  public final String name() {
    return "Sed";
  }

  @Override
  public final void onMessage(final Message messageObj) throws Exception {
    String message = messageObj.getMessage();
    String channel = messageObj.getChannel();
    String user = messageObj.getUser();

    // debug commands
    if (message.equals(".seddumpcache")
        && details.isAdmin(user)) {
      dumpCache(user, channel);
      return;
    }

    if (message.equals(".seddropcache")
        && details.isAdmin(user)) {
      dropCache(user, channel);
      return;
    }

    // set up sed finding regex
    // (?:      starts a non-capture group
    // ([\\w]+) captures the username
    // )?       makes the group optional
    // ([^/]+)  captures the search string
    // ([^/]*)  captures the replacement
    Pattern sedFinder = Pattern.compile(
        "^(?:([\\w]+): )?s/([^/]+)/([^/]*)/?");
    Matcher m = sedFinder.matcher(message);

    // it's sed time, baby
    if (m.find()) {
      String targetUser = new String();
      if (m.group(SED_USERNAME_GROUP) != null) {
        targetUser = m.group(SED_USERNAME_GROUP);
      }
      String search = m.group(SED_SEARCH_GROUP);
      String replacement = m.group(SED_REPLACEMENT_GROUP);

      Stack<Message> userCache = new Stack<Message>();
      boolean hasTarget = (
          !targetUser.equals(new String())
          && !targetUser.equals(user));
      if (!hasTarget) {
        // no target, use last message from
        // sourceUser's cache (or do nothing)
        userCache = getUserCache(user);
      } else {
        // target specified, run through the cache
        // (most recent first) and either match and
        // replace or do nothing
        userCache = getUserCache(targetUser);
      }

      while (!userCache.empty()) {
        Message mmessage = userCache.pop();
        String text = mmessage.getMessage();

        if (isActionMessage(text)) {
          text = processActionMessage(
            text, mmessage.getUser());
        }

        m = Pattern.compile(
            search,
            Pattern.CASE_INSENSITIVE
            | Pattern.DOTALL)
            .matcher(text);

        if (m.find()) {
          String reply = new String();
          if (hasTarget) {
            reply = user + " thought ";
          }

          reply += "%s meant : %s";
          text = text.replaceAll(
              search, replacement);
          irc.sendPrivmsg(channel, String.format(
              reply,
              mmessage.getUser(),
              text));
          break;
        }
      }

    } else {
    // no dice, add message to history queue
      addToCache(messageObj);
    }
  }

  /**
   * Identifies ACTION messages.
   * @param msg The message string
   * @return true if msg is an ACTION message
   */
  private boolean isActionMessage(final String msg) {
    return msg.startsWith(ASCII_SOH + "ACTION")
      && msg.endsWith(Character.toString(ASCII_SOH));
  }

  /**
   * Converts an ACTION message into a friendlier form.
   * @param msg The message string
   * @param user The speaker
   * @return The modified message string
   */
  private String processActionMessage(
      final String msg, final String user) {
    if (!isActionMessage(msg)) {
      return msg;
    }

    // remove special chars
    String newMsg = msg.substring(1);
    newMsg = newMsg.substring(0, newMsg.length() - 1);

    // format for output
    newMsg = newMsg.replace("ACTION",
        Colour.colour("* " + user, Colour.MAGENTA));
    return newMsg;
  }

  /**
   * Adds a message object to the cache.
   * @param msg The message object
   */
  private void addToCache(final Message msg) {
    String username = msg.getUser();
    if (!cache.containsKey(username)) {
      cache.put(username, new Stack<Message>());
    }
    cache.get(username).push(msg);
    if (cache.get(username).size() > CACHE_SIZE) {
      cache.get(username).remove(0);
    }
  }

  /**
   * Stack of messages from a single user.
   * @param username The name of the user
   * @return The user's cache
   */
  private Stack<Message> getUserCache(final String username) {
    Stack<Message> userCache = new Stack<Message>();
    if (cache.containsKey(username)) {
      userCache.addAll(cache.get(username));
    }
    return userCache;
  }

  /**
   * Dumps entire cache into query window.
   * @param target The username of the requester
   * @param channel The channel originating the request
   * @throws Exception Inherited badness from core
   */
  private void dumpCache(final String target, final String channel)
    throws Exception {
    irc.sendPrivmsg(target, "sed cache for " + channel);
    for (String user : cache.keySet()) {
      Stack<Message> userCache = getUserCache(user);

      irc.sendPrivmsg(target, " ");
      irc.sendPrivmsg(target,
          user + ": "
          + userCache.size() + "/" + CACHE_SIZE);
      for (Message msg : userCache) {
        irc.sendPrivmsg(target,
            "\"" + msg.getMessage() + "\"");
      }
    }
  }

  /**
   * Drops the entire sed cache and reinstantiates cache.
   * @param target The username of the requester
   * @param channel The channel originating the request
   * @throws Exception Inherited badness from core
   */
  private void dropCache(final String target, final String channel)
    throws Exception {
    cache = new HashMap<String, Stack<Message>>();
    irc.sendPrivmsg(target, "dropped sed cache for " + channel);
  }

  @Override
  public final String getHelpString() {
    return "SED: "
        + "[<username>: ]s/<search>/<replacement>/ - "
        + "e.g XeTK: s/.*/hello/ is used to "
        + "replace the previous statement with hello :";
  }

  @Override
  public void onCreate(final Channel in_channel) throws Exception {

  }

  @Override
  public void onTime() throws Exception {

  }

  @Override
  public void onJoin(final Join join) throws Exception {

  }

  @Override
  public void onQuit(final Quit quit) throws Exception {

  }

  @Override
  public void onKick(final Kick kick) throws Exception {

  }

  @Override
  public void rawInput(final String rawInput) throws Exception {

  }

}
