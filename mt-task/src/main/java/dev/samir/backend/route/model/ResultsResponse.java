package dev.samir.backend.route.model;

import java.util.Set;

/**
 * Results response model.
 * 
 * @author Scheide, Samir
 */
public final class ResultsResponse {

	/**
	 * Crawl unique identifier.
	 */
	private String id;
	
	/**
	 * Crawl status. It may be one of the following:
	 * <pre>
	 * - ACTIVE while the crawl is running, 
	 * - DONE when the crawl is finished and
	 * - FAILED when the crawl failed with an exception.
	 * </pre>
	 */
	private CrawlStatus status;
	
	/**
	 * URLs that was found the keyword
	 */
	private Set<String> urls;
	
	/**
	 * Error or exception messages. 
	 * Usually appears when the crawl has a {@link CrawlStatus#FAILED} status.
	 */
	private String message;
	
	/**
	 * Default constructor.
	 * @param id {@link #id}
	 * @param status {@link #status}
	 * @param urls {@link #urls}
	 */
	public ResultsResponse(String id, CrawlStatus status, Set<String> urls) {
		this.id = id;
		this.status = status;
		this.urls = urls;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CrawlStatus getStatus() {
		return status;
	}

	public void setStatus(CrawlStatus status) {
		this.status = status;
	}

	public Set<String> getUrls() {
		return urls;
	}

	public void setUrls(Set<String> urls) {
		this.urls = urls;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
}
