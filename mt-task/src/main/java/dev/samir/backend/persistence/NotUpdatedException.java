package dev.samir.backend.persistence;

import dev.samir.backend.client.StatusCode;

/**
 * Thrown whenever a crawl could not be updated. The exception has a 1003 error code, mostrly tranlated to 
 * a {@link StatusCode#BAD_REQUEST} staus rather than a {@link StatusCode#NOT_FOUND}. This is because the crawl was found, 
 * but it was not possible to update it.
 * 
 * @author Scheide, Samir <samir.scheide@gmail.com>
 */
class NotUpdatedException extends DataAccessObjectException {

	/**
	 * Serial version UID for serialization.
	 */
	private static final long serialVersionUID = 1493189427288823254L;
	
	/**
	 * A message that describes the exception.
	 */
	private static final String MESSAGE = "The crawl could not be updated. Please, check if the ID and URLs are correct.";

	/**
	 * Default constructor.
	 */
	public NotUpdatedException() {
		super(1003, MESSAGE);
	}
	
}
