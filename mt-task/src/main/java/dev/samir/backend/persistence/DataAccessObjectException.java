package dev.samir.backend.persistence;

/**
 * 
 * @author Scheide, Samir
 */
public abstract class DataAccessObjectException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5509151418887657344L;
	
	/**
	 * Error code for the exception.
	 */
	protected int errorCode;
	
	/**
	 * Default constructor.
	 * @param errorCode {@link #errorCode}
	 * @param errorMessage message for the exception.
	 */
	public DataAccessObjectException(int errorCode, String errorMessage) {
		super(errorMessage);
		this.errorCode = errorCode;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
}
