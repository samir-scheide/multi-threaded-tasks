package dev.samir.backend.persistence;

/**
 * Thrown whenever a crawl analysis could not be found. The exception has a 1001 error code, which may be trnslated to
 * a {@link StatusCode#NOT_FOUND} because the crawl was not found in the database.
 * 
 * @author Scheide, Samir <samir.scheide@gmail.com>
 */
class NotFoundException extends DataAccessObjectException {

	/**
	 * Serial version UID for serialization.
	 */
	private static final long serialVersionUID = 4240893733707103099L;

	/**
	 * A message that describes the error.
	 */
	private static final String MESSAGE = "Crawl was not found. Please, check if the ID is correct.";
	
	/**
	 * Default constructor.
	 */
	public NotFoundException() {
		super(1001, MESSAGE);
	}
	
}
