package dev.samir.backend.service.exception;

/**
 * 
 * @author Scheide, Samir
 */
public class StatusNotUpdatedException extends RuntimeException {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -6025308781772788935L;
	
	/**
	 * Message to be shown when the exception is thrown.
	 */
	private static final String MESSAGE = "Crawl status not updated. Please, try again later.";
	
	/**
	 */
	public StatusNotUpdatedException() {
		super(MESSAGE);
	}
	
	/**
	 */
	public StatusNotUpdatedException(Throwable throwable) {
		super(throwable.getMessage(), throwable);
	}

}
