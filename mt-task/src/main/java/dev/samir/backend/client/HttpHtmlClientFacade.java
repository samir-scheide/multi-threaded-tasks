package dev.samir.backend.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import dev.samir.backend.Main;

/**
 * 
 * @author Scheide, Samir
 */
public final class HttpHtmlClientFacade implements HttpClientFacade {

	/**
	 * 
	 */
	private static final HttpClient HTTP_CLIENT =  HttpClient.newBuilder().executor(Main.EXECUTOR_SERVICE).build();
	
	/**
	 * Nice to have "like-cache" table.
	 */
	private static final Map<URI, CompletableFuture<HttpResponse<String>>> CACHE = new ConcurrentHashMap<>();
	
	/**
	 * 
	 */
	static {
        // Schedule the task to run every 3 minutes (180 seconds)
        Executors.newSingleThreadScheduledExecutor()
        	.scheduleAtFixedRate(CACHE::clear, 0, 180, TimeUnit.SECONDS);
	}
	
	/**
	 * This method should handle all the HTTP {@link HttpMethod#GET} requests made to external servers to retrieve the HTML or each URL provided.
	 * The client is created returning a {@link CompletableFuture}, so it is able to handle multiple requests at the same time.
	 * 
	 * @param uri The URI to send the request to.
	 * @return A {@link CompletableFuture} containing the HTTP response as a string.
	 */
	public final CompletableFuture<HttpResponse<String>> requestAsync(URI uri) {
		if (!CACHE.containsKey(uri)) {
			CACHE.put(uri, HTTP_CLIENT.sendAsync(HttpRequest.newBuilder(uri).GET().build(), BodyHandlers.ofString()));
		}
		return CACHE.get(uri);
	}
	
}
