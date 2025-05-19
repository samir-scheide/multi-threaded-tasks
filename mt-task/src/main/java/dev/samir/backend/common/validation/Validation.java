package dev.samir.backend.common.validation;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.samir.backend.common.configuration.DefaultEnvironmentConfiguration;
import dev.samir.backend.common.configuration.Environment;
import dev.samir.backend.route.model.CrawlStatus;

/**
 * Contains most of the business validations made throught the application, including the required cases:
 * <ul>
 *   <li>The identifier (ID) created must be alphanumeric;</li>
 *   <li>The length of the identifier (ID) generated for each crawl must be equals to 8;</li>
 *   <li>The keyword sent to match the HTML contents must be between 4 and 32 characters.</li>
 * </ul>
 * @see Environment
 * @see RegularExpressions
 */
public interface Validation {

	/**
	 * Logger
	 */
	static final Logger LOGGER = LoggerFactory.getLogger(Validation.class);
	
	/**
	 * Check if the ID is valid.
	 * @param id crawl analysis unique identifier.
	 * @return checks if the URL is not null, not empty, has 8 characters - by default - and 
	 * matches the regular expression {@link RegularExpressions#IDENTIFIER_COMPILED_REGEX} to allow only alphanumeric chars.
	 */
	default String applyIdValidation(String identifier) {
		LOGGER.debug("Parameter ID: {}", identifier);
		Predicate<String> idPredicate = RegularExpressions.IDENTIFIER_COMPILED_REGEX.asMatchPredicate()
			.and(id -> getEnvironment().getIdLengthsValidation().contains(id.length()));
		return Optional.ofNullable(identifier)
			.filter(idPredicate)
			.orElseThrow(() -> new IllegalArgumentException(String.format("The ID must have %s characters long.", getEnvironment().getIdLengthsValidation())));
	}
	
	/**
	 * Check if the URL is valid.
	 * @param url crawl analysis URL.
	 * @return checks if the URL is not null, not empty, and matches the regular expression {@link RegularExpressions#URL_COMPILED_REGEX}
	 */
	default String applyURLValidation(String url) {
		LOGGER.debug("Parameter URL: {}", url);
		return Optional.ofNullable(url)
			.filter(RegularExpressions.URL_COMPILED_REGEX.asMatchPredicate())
			.orElseThrow(() -> new IllegalArgumentException("The URL must match the correct pattern. Eg. http://example.com, http://example.com/somepage.jsp, etc."));
	}
	
	/**
	 * Check if the crawl status is valid.
	 * @param status the status.
	 * @return if the status is not null, not empty, and matches one of the constants from {@link CrawlStatus}.
	 */
	default CrawlStatus applyStatusValidation(String status) {
		LOGGER.debug("Parameter STATUS: {}", status);
		return Optional.ofNullable(status)
			.map(String::toUpperCase)
			.map(crawlStatus -> CrawlStatus.valueOf(crawlStatus))
			.orElseThrow(() -> new IllegalArgumentException("The status must be one of these: " + Arrays.asList(CrawlStatus.values())));
	}
	
	/**
	 * Check if the keyword is valid.
	 * @param keyword term to be searched.
	 * @return checks if the keyword length is between 4 and 32 characters.
	 */
	default String applyKeywordValidation(String keyword) {
		LOGGER.debug("Parameter KEYWORD: {}", keyword);
		Predicate<String> keywordPredicate = k -> k.length() >= 4 && k.length() <= 32;
		return Optional.ofNullable(keyword)
			.filter(keywordPredicate)
			.orElseThrow(() -> new IllegalArgumentException("The keyword must have between 4 and 32 characters long."));
	}
	
	/**
	 * 
	 * @return
	 */
	default Environment getEnvironment() {
		return new Environment(new DefaultEnvironmentConfiguration());
	}
	
}
