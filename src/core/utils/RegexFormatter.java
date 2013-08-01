package core.utils;
@author Alexander Brown (SoftlySplinter) 
public enum RegexFormatter {
	/**
	 * IRC Channel Regex as defined by RFC 1459
	 */
	REG_CHAN("[#&][^\\x07\\x2C\\s]{0,200}"),
	/**
	 * IRC Nickname regex. Not quite as defined by a RFC at the moment though.
	 */
	REG_NICK("[a-zA-Z_\\-\\[\\]\\^{}|`][a-zA-Z0-9_\\-\\[\\]\\^{}|`]*");
	
	private String regex;
	private RegexFormatter(String regex)
	{
		this.regex = regex;
	}
	
	public static String format(String command, RegexFormatter type)
	{
		return String.format("^\\.%s %s$", command,type);
	}
}
