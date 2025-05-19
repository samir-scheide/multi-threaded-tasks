package dev.samir.backend.common.configuration;

/**
 * 
 */
public interface EnvironmentConfiguration {

	static final String ENV_BASE_URL = "BASE_URL";
	
	static final String ENV_ID_LENGTH_CREATION = "ID_LENGTH_CREATION";
	
	static final String ENV_ID_LENGTH_VALIDATION = "ID_LENGTHS_VALIDATION";
	
	static final String ENV_RESULTS_SIZE = "RESULTS_SIZE";
	
	default String baseUrl() {
		return System.getenv(ENV_BASE_URL);
	}
	
	default String idLengthCreation() {
		return System.getenv(ENV_ID_LENGTH_CREATION);
	}
	
	default String idLengthsValidation() {
		return System.getenv(ENV_ID_LENGTH_VALIDATION);
	}
	
	default String maxResultsSize() {
		return System.getenv(ENV_RESULTS_SIZE);
	}
	
}
