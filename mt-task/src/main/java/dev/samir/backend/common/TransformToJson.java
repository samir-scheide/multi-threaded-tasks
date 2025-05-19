package dev.samir.backend.common;

/**
 * Interface containing the method to convert an object to a JSON string.
 * 
 * @author Scheide, Samir
 */
public interface TransformToJson {

	/**
	 * Converts the given object to a JSON string.
	 * @param src the object to convert
	 * @return the JSON string representation of the object
	 */
	String toJson(Object src);
	
}
