package dev.samir.backend.service;

import java.net.URI;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.samir.backend.Main;
import dev.samir.backend.client.HttpClientFacade;
import dev.samir.backend.common.configuration.Environment;
import dev.samir.backend.common.validation.RegularExpressions;
import dev.samir.backend.persistence.DataAccessObject;
import dev.samir.backend.persistence.model.CrawlTableResultSet;
import dev.samir.backend.route.model.CrawlResponse;
import dev.samir.backend.route.model.CrawlStatus;
import dev.samir.backend.route.model.ResultsResponse;
import dev.samir.backend.service.exception.NoResultsException;
import dev.samir.backend.service.exception.ProcessingFailedException;
import dev.samir.backend.service.exception.StatusNotUpdatedException;

/**
 * 
 * @author Scheide, Samir
 */
public final class ServicesImpl implements ResultsService, CrawlService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicesImpl.class);

    private final URI baseUri;
    private final DataAccessObject dao;
    private final Environment environment;
    private final HttpClientFacade httpClientFacade;
    
	/**
	 * 
	 * @param dao
	 * @param environment
	 * @param httpClientFacade
	 */
    public ServicesImpl(DataAccessObject dao, Environment environment, HttpClientFacade httpClientFacade) {
        this.dao = dao;
        this.environment = environment;
        this.httpClientFacade = httpClientFacade;
        this.baseUri = URI.create(environment.getBaseUrl());
    }
    
    /**
     * 
     */
	@Override
	public ResultsResponse update(String id, CrawlStatus status) throws StatusNotUpdatedException {
		LOGGER.info("Updating crawl {} status to {}", id, status);
		return Optional.ofNullable(dao.updateStatus(id, status.name(), null))
			.map(result -> new ResultsResponse(result.getId(), status, result.getUrls()))
			.orElseThrow(StatusNotUpdatedException::new);
	}
    
    @Override
    public ResultsResponse list(String id) {
        LOGGER.info("Retrieving results for ID: {}", id);
        return Optional.ofNullable(dao.get(id))
            .map(result -> new ResultsResponse(id, CrawlStatus.valueOf(result.getStatus()), result.getUrls()))
            .orElseThrow(NoResultsException::new);
    }

    @Override
    public CrawlResponse crawl(String keyword) throws ProcessingFailedException {
        LOGGER.info("Starting crawl for given KEYWORD: {}", keyword);
        try {
        	long start = System.currentTimeMillis();
        	// The crawl should be persisted as soon it starts so we have access to the generated ID beforehand
        	final String identifier = dao.persist(null).getId();
        	LOGGER.info("The following identifier was created: {}", identifier);
        	// Retrieve the first html data from base URL provided by our environment variable BASE_URL
        	pickHtmlFromUri(baseUri)
    			// If the html data has the keyword, persist into the database.
        		.thenAcceptAsync(htmlData -> {
        			if (isKeywordFound(keyword, htmlData)) {
        				dao.updateUrl(identifier, baseUri.toString());
        			}
        			// Then the same verification should be made with each anchor inside the HTML document
        			pickAnchorsHrefFromHTML(htmlData).thenAcceptAsync(links -> {
        	        	// Since we shall be using a queue and to avoid repeated request, a set instance holds the visited URLs
        	        	Set<String> visited = ConcurrentHashMap.newKeySet();
        	        	// In order to kep all synchronized, a concurrent queue is created from the set list of anchors URLs
        	        	Queue<Set<String>> queue = new ConcurrentLinkedQueue<>();
        	        	queue.add(links);
        	            // The counter is used to validate the total of results we may return in this method, like a circuit-breaker,
        	            // preventing unnecessary processing and persitence
        	            AtomicInteger resultsCounter = new AtomicInteger(0);
        	            // The "while" executionn can be tricky because if no results if found, then our counter remains the same,
        	            // and if remains the same, the processing will stop only when reaches the queue's end and that can
        	            // take a while, wasting time and processing. A rule to exit the loop is coinfigured ahead.
        	            AtomicInteger executionsCounter = new AtomicInteger(0);
        	            int chunkSize = environment.getMaxResultsSize() < 100 ? environment.getMaxResultsSize() : 100;
        	            while (!queue.isEmpty() && resultsCounter.get() < environment.getMaxResultsSize()) {
        	            	// Get - or remove the head - from the queue and, if the URL is already visited, loop next
        	            	Set<String> hrefsElementsFromQueue = queue.poll();
        	            	LOGGER.debug("Crawling {}", hrefsElementsFromQueue);
        	            	
        	            	Iterator<String> iterator = hrefsElementsFromQueue.iterator();
        	            	while (iterator.hasNext() && resultsCounter.get() < environment.getMaxResultsSize()) {
        	            		
        	            		String link = iterator.next();
        	            		
        	            		if (!visited.add(link)) continue;
        	            		else {
        	            			
        	            			// The completable future should do the request to GET the HTML data,
        	            			// verify if contains the keyword and, if contains it, persit the crawl data.
        	            			CompletableFuture<String> linkHtmlData = pickHtmlFromUri(URI.create(link));
        	            			queue.add(linkHtmlData.thenApplyAsync(hrefHtmlData -> {
        	            				synchronized (dao) {
        	            					CrawlStatus currentStatus = CrawlStatus.valueOf(dao.get(identifier).getStatus());
        	            					LOGGER.debug("Executions {} and queue size {}. Actual crawl status {}", 
        	            							executionsCounter.get(), queue.size(), currentStatus);
        	                	            // Instead, if the status is HALT, we skip the crawling every Environment.ENV_RESULTS_SIZE or 100 searches. 
        	            					// If the current status is DONE, then we skip the whole thing immediately.
        	            					if ((executionsCounter.getAndIncrement() % chunkSize == 0
        	            							&& CrawlStatus.HALT.equals(currentStatus)) || CrawlStatus.DONE.equals(currentStatus)) {
        	            						LOGGER.warn(
        	            								"Crawl {} was cancelled, skipping further processing by setting the results counter {} to its max {}",
        	            								identifier,
        	            								resultsCounter.get(), 
        	            								environment.getMaxResultsSize());
        	            						resultsCounter.set(environment.getMaxResultsSize());
        	            						queue.clear();
        	            					}
	            	            			// Evaluates again because, in a parallel scenario, the counter may have already reached the limit
	        	            				if (isKeywordFound(keyword, hrefHtmlData) && resultsCounter.get() < environment.getMaxResultsSize()) {
        	            						dao.updateUrl(identifier, link);
        	            						resultsCounter.incrementAndGet();
        	            						LOGGER.debug("Persisted {} and incremented result counter to {} while execution counter is at {}", link, resultsCounter.get(), executionsCounter.get());
	        	            				}
        	            				}
        	            				// At the end, we add the anchors links from the current HTML data into tue queue for processing
        	            				return pickAnchorsHrefFromHTML(hrefHtmlData).join();
        	            			}, Main.EXECUTOR_SERVICE)
	            					// After processing, in case of exception, marks the crawl as failed 
	            	        		// and update the message with the details
	            					.exceptionally(exception -> {
	            	        			synchronized (dao) {
	            	        				try {
	            	        					if (exception != null) {
	            	        						// Failed attempts may have a description
	            	        						LOGGER.error(exception.getMessage(), exception);
	            	        						dao.updateStatus(identifier, CrawlStatus.FAILED.name(), exception.getMessage());
	            	        					}
	            	        				} catch (Exception ex) {
	            	        					LOGGER.error(ex.getMessage(), ex);
	            	        				}
	            	        			}
	            	        			return null;
	            					})
	            					.join());
        	            			LOGGER.debug("Main queue contains {} elements", queue.size());
        	            		}
        	            	}
        	            }
        	            CrawlTableResultSet updated = dao.updateStatus(identifier, CrawlStatus.DONE.name(), null);
    	            	Duration duration = Duration.ofMillis(System.currentTimeMillis() - start);
    	            	LOGGER.info("Time elapsed fetching {} result(s) during {} execution(s) for '{}': {}",
    	            			updated.getUrls().size(), executionsCounter.get(), identifier,
    	            			String.format("%d:%02d:%02d", duration.toHours() % 60, duration.toMinutes() % 60, duration.toSeconds() % 60));
        	        }, Main.EXECUTOR_SERVICE);
        		}, Main.EXECUTOR_SERVICE)
        		// After processing, in case of exception, marks the crawl as failed 
        		// and update the message with the details
        		.exceptionally(exception -> {
        			synchronized (dao) {
        				try {
        					if (exception != null) {
        						// Failed attempts may have a description
        						LOGGER.error(exception.getMessage(), exception);
        						dao.updateStatus(identifier, CrawlStatus.FAILED.name(), exception.getMessage());
        					}
        				} catch (Exception ex) {
        					LOGGER.error(ex.getMessage(), ex);
        				}
        			}
        			return null;
				});
        	return new CrawlResponse(identifier);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new ProcessingFailedException(ex);
        }
    }
    
    /**
     * 
     * @param uri
     * @return
     */
    private CompletableFuture<String> pickHtmlFromUri(URI uri) {
    	LOGGER.debug("Fecthing HTML data from {}", uri);
    	return httpClientFacade.requestAsync(uri).thenApplyAsync(HttpResponse::body, Main.EXECUTOR_SERVICE);
    }
    
    /**
     * 
     * @param html
     * @return
     */
    private CompletableFuture<Set<String>> pickAnchorsHrefFromHTML(String html) {
    	LOGGER.debug("HTML sent over to fecth anchors: {} ", html);
    	return CompletableFuture.supplyAsync(() -> 
    		RegularExpressions.A_HREF_COMPILED_REGEX.matcher(html)
				.results()
	            .map(uriResolver())
	            .filter(Objects::nonNull)
	            .filter(uri -> uri.startsWith(environment.getBaseUrl()))
	            .collect(Collectors.toCollection(LinkedHashSet::new))
        , Main.EXECUTOR_SERVICE);
    }
    
    /**
     * 
     * @return
     */
    private Function<MatchResult, String> uriResolver() {
    	return matcher -> {
        	String href = matcher.group(1).trim();
            try {
                return baseUri.resolve(href).toString();
            } catch (IllegalArgumentException e) {
                return null; // malformed URL, skip
            }
    	};
    }
    
    /**
     * 
     * @param htmlData
     * @param keyword
     * @return
     */
    private Boolean isKeywordFound(String keyword, String htmlData) {
    	LOGGER.debug("Verifying if the html ({}) data contains the keyword", htmlData.hashCode());
    	return Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE).matcher(htmlData).find();
    }
    
}
