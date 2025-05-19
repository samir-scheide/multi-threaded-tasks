package dev.samir.backend.persistence.model;

import java.util.HashSet;
import java.util.Set;

import dev.samir.backend.route.model.CrawlStatus;

/**
 * This class represents a result set for an analysis table.
 * 
 * @author Scheide, Samir
 */
public final class CrawlTableResultSet {

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
	private String status = CrawlStatus.ACTIVE.name();
	
	/**
	 * Set of URLs the keyword was found.
	 */
	private Set<String> urls = new HashSet<>(1);
	
	/**
	 * Error or exception messages. 
	 * Usually appears when the crawl has a {@link CrawlStatus#FAILED} status.
	 */
	private String message;
	
	/**
	 */
	public CrawlTableResultSet(String id, String url) {
		this.id = id;
		if (url != null) this.urls.add(url);
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
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
