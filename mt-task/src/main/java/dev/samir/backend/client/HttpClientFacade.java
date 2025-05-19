package dev.samir.backend.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

/**
 * This class is a facade for making HTTP requests. 
 * It uses the {@link HttpClient} class to send asynchronous requests and handle responses.
 * 
 * @author Scheide, Samir
 */
public interface HttpClientFacade {

	/**
	 * This method should handle all the HTTP {@link HttpMethod#GET} requests made to external servers to retrieve the HTML or each URL provided.
	 * The client is created returning a {@link CompletableFuture}, so it is able to handle multiple requests at the same time.
	 * 
	 * @param uri The URI to send the request to.
	 * @return A {@link CompletableFuture} containing the HTTP response as a string.
	 */
	default CompletableFuture<HttpResponse<String>> requestAsync(URI uri) {
		return HttpClient.newBuilder().build().sendAsync(HttpRequest.newBuilder(uri).GET().build(), BodyHandlers.ofString());
	}
	
}