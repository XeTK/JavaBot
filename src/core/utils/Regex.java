package core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {

	public static Matcher getMatcher(String regex, String str) {
		return Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(str);
	}
	
}
