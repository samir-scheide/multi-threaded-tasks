package dev.samir.backend.route.model;

import java.util.Objects;

import dev.samir.backend.client.StatusCode;

/**
 * Error response model for API responses.
 * 
 * @author Scheide, Samir
 */
public final class ErrorResponse {

	/**
	 * The status code of the error.
	 */
	private StatusCode status;
	
	/**
	 * The message describing the error.
	 */
	private String message;
	
	public ErrorResponse(StatusCode status, String message) {
		this.status = Objects.requireNonNull(status);
		this.message = message;
	}
	
	public ErrorResponse(StatusCode status) {
		this(status, status.getDescription());
	}

	public StatusCode getStatus() {
		return status;
	}

	public void setStatus(StatusCode status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
