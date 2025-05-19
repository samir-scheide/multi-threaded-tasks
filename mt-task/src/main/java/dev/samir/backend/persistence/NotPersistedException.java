package dev.samir.backend.persistence;

/**
 * Thrown whenever a crawl analysis could not be persisted. The exception has a 1002 error code, which may be trnslated to
 * a {@link StatusCode#BAD_REQUEST} because some sort of error os miswritten logic. 
 * 
 * @author Scheide, Samir <samir.scheide@gmail.com>
 */
class NotPersistedException extends DataAccessObjectException {

	/**
	 * Serial version UID for serialization.
	 */
	private static final long serialVersionUID = 4240893733707103099L;
	
	/**
	 * A message that describes the error.
	 */
	private static final String MESSAGE = "Crawl request not be persisted. Please, check if the identifier is correct or if it already exists in database.";
	
	/**
	 * Default constructor.
	 */
	public NotPersistedException() {
		super(1002, MESSAGE);
	}
	
}
