package dev.samir.backend.common;

import java.util.Random;

import dev.samir.backend.common.configuration.DefaultEnvironmentConfiguration;
import dev.samir.backend.common.configuration.Environment;

/**
 * This class provides methods to generate unique identifiers and random numbers.
 * 
 * @author Scheide, Samir <samir.scheide@gmail.com>
 */
public interface GenerateRandom {
	
	/**
	 * Random instance.
	 */
	static final Random RANDOM = new Random();
	
	/**
	 * Creates a random alphanumeric sequence.
	 * 
	 * @return a unique identifier containing only numbers and letters.
	 */
	default String alphaNumericSequence(int limit) {
		return RANDOM.ints(48, 122 + 1)
			.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
			.limit(limit)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();
	}
	
	default String alphaNumericSequence() {
		return alphaNumericSequence(getEnvironment().getIdLengthCreation());
	}
	
	default Environment getEnvironment() {
		return new Environment(new DefaultEnvironmentConfiguration());
	}
	
}
