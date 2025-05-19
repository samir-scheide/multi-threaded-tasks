package dev.samir.backend.common;

/**
 * Interface containing the method to convert a JSON string to an object.
 * 
 * @author Scheide, Samir
 */
public interface TransformFromObject {

	/**
	 * Converts a JSON string to an object of the specified class.
	 * @param <T> the type of the object to be returned
	 * @param src the JSON string to be converted
	 * @param clazz the class of the object to be returned
	 * @return the object of the specified class
	 */
	<T> T fromJson(String src, Class<T> clazz);
	
}
