package dev.samir.backend.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.samir.backend.client.StatusCode;
import dev.samir.backend.persistence.DataAccessObjectException;
import dev.samir.backend.route.model.ErrorResponse;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * An abstraction created to enable a better exception handling in the controller layer. It implements
 * the Route interface and provides a {@link #handle(Request, Response))} method that is called when a request is received.
 * 
 * @param <T> the type of the object that is returned by the {@link #handle(Request)} method.
 * @autho Scheide, Samir
 */
public abstract class AroundInvoke<T> implements Route {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AroundInvoke.class);
	
	/**
	 * {@inheritDoc} <br>
	 * Calls for the {@link #handle(Request)} method and handles the exception
	 * throws using {@link #exceptionHandling(Response, ErrorResponse, Throwable)} method.
	 */
	@Override
	public final Object handle(Request request, Response response) throws Exception {
		ErrorResponse errorResponse = new ErrorResponse(StatusCode.BAD_REQUEST);
		try {
			LOGGER.debug("Received {} request with the following path and query params and body: {} ({}) {{}}", 
    			request.requestMethod(), request.params(), request.queryParams(), request.body());
    	
	    	response.type("application/json");
    	
    		LOGGER.debug("Before request handling.");
    		return handle(request);
    		
    	} catch (Exception exception) {
    		exceptionHandling(response, errorResponse, exception);
    	}
    	LOGGER.debug("After request handling, with error response {}.", errorResponse);
    	return errorResponse;
	}
	
	/**
	 * This method is called when a request is received. It should be implemented by the subclasses
	 * @param request the request object that contains the request data
	 * @return the object that is returned by the method
	 * @throws Exception if an error occurs while handling the request
	 */
	protected abstract T handle(Request request) throws Exception;
	
	/**
	 * This method is called when an exception is thrown in the {@link #handle(Request)} method. 
	 * Basically any exception handled by the method creates a error response object and sets 
	 * the status from errorReponse param and the message from throwable param. But, when the 
	 * exception is a {@link DataAccessObjectException} it sets the status code to NOT_FOUND or BAD_REQUEST based on the
	 * error code of the exception. The statis code will reflect the client response status code.
	 * @param response the response object that contains the response data
	 * @param errorResponse the error response object that contains the error data
	 * @param throwable the exception that was thrown
	 */
	protected void exceptionHandling(Response response, ErrorResponse errorResponse, Throwable throwable) {
		errorResponse.setMessage(throwable.getMessage());
		response.status(errorResponse.getStatus().getCode());
		if (throwable instanceof DataAccessObjectException) {
			DataAccessObjectException daoe = ((DataAccessObjectException) throwable);
			// TODO: create a proper error x status translation
			StatusCode statusCode = daoe.getErrorCode() == 1001 
					? StatusCode.NOT_FOUND : StatusCode.BAD_REQUEST;
			response.status(statusCode.getCode());
			errorResponse.setStatus(statusCode);
			errorResponse.setMessage(daoe.getMessage());
		}
	}
	
}
