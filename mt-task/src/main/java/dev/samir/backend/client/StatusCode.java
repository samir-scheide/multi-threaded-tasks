package dev.samir.backend.client;

/**
 * This class contains the HTTP status codes and their descriptions.
 * 
 * @author Scheide, Samir
 */
public enum StatusCode {
    /**
     * 200 OK, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.1">HTTP/1.1 documentation</a>.
     */
    OK(200, "OK"),
    
    /**
     * 201 Created, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.2">HTTP/1.1
     * documentation</a>.
     */
    CREATED(201, "Created"),
    
    /**
     * 202 Accepted, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.3">HTTP/1.1
     * documentation</a>.
     */
    ACCEPTED(202, "Accepted"),
    
    /**
     * 400 Bad Request, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.1">HTTP/1.1
     * documentation</a>.
     */
    BAD_REQUEST(400, "Bad Request"),
    
    /**
     * 404 Not Found, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.5">HTTP/1.1
     * documentation</a>.
     */
    NOT_FOUND(404, "Not Found"),
    
    /**
     * 500 Internal Server Error, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.1">HTTP/1.1
     * documentation</a>.
     */
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    
    /**
     * 501 Not Implemented, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.2">HTTP/1.1
     * documentation</a>.
     *
     * @since 2.0
     */
    NOT_IMPLEMENTED(501, "Not Implemented"),
    
    /**
     * 502 Bad Gateway, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.3">HTTP/1.1
     * documentation</a>.
     *
     * @since 2.0
     */
    BAD_GATEWAY(502, "Bad Gateway"),
    
    /**
     * 503 Service Unavailable, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.4">HTTP/1.1
     * documentation</a>.
     */
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    
    /**
     * 504 Gateway Timeout, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.5">HTTP/1.1
     * documentation</a>.
     *
     * @since 2.0
     */
    GATEWAY_TIMEOUT(504, "Gateway Timeout");
    
    final int code;
    
    final String description;
    
    StatusCode(int code, String description) {
		this.code = code;
		this.description = description;
	}
    
    public int getCode() {
		return code;
	}
    
    public String getDescription() {
		return description;
	}
	
}
