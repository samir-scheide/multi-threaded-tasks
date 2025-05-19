package dev.samir.backend.common.validation;

import java.util.regex.Pattern;

/**
 * 
 * @author Scheide, Samir
 */
public final class RegularExpressions {
	
	/**
	 * A regular expression that matches valid identifiers checking for the presence of 8 alphanumeric characters.
	 */
	public static final Pattern IDENTIFIER_COMPILED_REGEX = Pattern.compile("^[a-zA-Z0-9]*$");
	
	/**
	 * A regular expression that matches valid URLs.
	 * It checks for the presence of http or https at the beginning, followed by a valid domain and optional path/query parameters.
	 */
	public static final Pattern URL_COMPILED_REGEX = Pattern.compile("^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]$");
	
	/**
	 * A regular expression that matches anchor tags checking for the presence of a href value.
	 */
	public static final Pattern A_HREF_COMPILED_REGEX = Pattern.compile("<a\\s+[^>]*?href=[\"']([^\"']+)[\"'][^>]*?>.*?</a>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	
	/**
	 * Constructor disabled.
	 * @throws IllegalAccessException everytime this method is called.
	 */
	private RegularExpressions() throws IllegalAccessException {
		throw new IllegalAccessException("Constructor should not be used.");
	}

}
