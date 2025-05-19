package dev.samir.backend.service.exception;

/**
 * Throw whenever the parcial results of a crawl is not found.
 * 
 * @author Scheide, Samir <samir.scheide@gmail.com>
 */
public class NoResultsException extends RuntimeException {

	/**
	 * Serial version UID for serialization.
	 */
	private static final long serialVersionUID = 3333383166344173406L;

	/**
	 * Message to be shown when the exception is thrown.
	 */
	private static final String MESSAGE = "Crawl results not found. Please, try again later.";
	
	/**
	 * Default constructor.
	 */
	public NoResultsException() {
		super(MESSAGE);
	}
	
	/**
	 * Constructor with a throwable message.
	 */
	public NoResultsException(Throwable throwable) {
		super(throwable.getMessage(), throwable);
	}
	
}
