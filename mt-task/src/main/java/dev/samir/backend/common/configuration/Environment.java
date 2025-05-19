package dev.samir.backend.common.configuration;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import dev.samir.backend.common.RandomUtils;
import dev.samir.backend.common.validation.Validation;

/**
 * This class is used to retrieve the environment variables that are used in the application.
 * These variable are set as static constants and uses the {@link System#getenv(String)} method to retrieve them.
 * <p>
 * Variables: <pre>BASE_URL, ID_LENGTH_CREATION, ID_LENGTHS_VALIDATION, RESULTS_SIZE</pre>
 * </p>
 * @author Scheide, Samir
 */
public final class Environment {

	/**
	 * Environment configuration implementation.
	 */
	private EnvironmentConfiguration configuration;
	
	/**
	 * Default constructor.
	 * @param configuration
	 */
	public Environment(EnvironmentConfiguration configuration) {
		this.configuration = configuration;
	}
	
	/**
	 * The environment variable named <b>BASE_URL</b> is used to set the base URL of the application.
	 * Since it is required, if it is not set, an exception will be thrown.
	 * 
	 * @throws NoSuchElementException if the environment variable name <b>BASE_URL</b> is not set.
	 */
	public String getBaseUrl() {
		return Optional.ofNullable(configuration.baseUrl())
			.orElseThrow(() -> new NoSuchElementException("Please, check if the base URL is configured as expected."));
	}
	
	/**
	 * The environment variable named <b>ID_LENGTH_CREATION</b> is used to set the length of the ID when creating a new crawl.
	 * It is not required but, if the variable is not set, has a default value of <b>8</b>.
	 * 
	 * @see RandomUtils#randomUniqueIdentifier()
	 */
	public Integer getIdLengthCreation() {
		return Optional.ofNullable(configuration.idLengthCreation()).map(Integer::valueOf).orElse(8);
	}
	
	/**
	 * The environment variable named <b>ID_LENGTH_VALIDATION</b> is used to validate the size of an ID.
	 * This variable has a particular use case. Is can be used to validate multiple sizes of IDs. It is a nice way
	 * to evaluate past and present values of the ID length, otherwise, those created with a past value will not be
	 * valid.
	 * 
	 * @see Validation#applyIdValidation(String)
	 */
	public Set<Integer> getIdLengthsValidation() {
		return Arrays.stream(Optional.ofNullable(configuration.idLengthsValidation())
			.filter(s -> !s.isBlank()).orElse(String.valueOf(getIdLengthCreation())).split(","))
			.map(String::trim).map(Integer::parseInt).collect(Collectors.toSet());
	}
	
	/**
	 * Tyhe environment variable named <b>RESULTS_SIZE</b> is used to set the size of the results when
	 * retrieving a list of crawls. If not set, it will have a default value of <b>100</b>. Keep in mid this is 
	 * the value of <u>total results</u> and not of <u>deepness</u>; the crawl will keep runnig until the results
	 * reach this value independently of the deepness.
	 */
	public Integer getMaxResultsSize() {
		return Optional.ofNullable(configuration.maxResultsSize()).map(Integer::valueOf).orElse(100);
	}
	
}
