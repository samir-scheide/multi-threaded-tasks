package dev.samir.backend.service.exception;

/**
 * @author Scheide, Samir <samir.scheide@gmail.com>
 */
public class ProcessingFailedException extends RuntimeException {

	/**
	 * Serial version UID for serialization.
	 */
	private static final long serialVersionUID = 3333383166344173406L;

	/**
	 * 
	 */
	private static final String MESSAGE = "Crawl failed. Please, try again later.";
	
	/**
	 */
	public ProcessingFailedException() {
		super(MESSAGE);
	}
	
	/**
	 */
	public ProcessingFailedException(Throwable throwable) {
		super(throwable.getMessage(), throwable);
	}
	
}
