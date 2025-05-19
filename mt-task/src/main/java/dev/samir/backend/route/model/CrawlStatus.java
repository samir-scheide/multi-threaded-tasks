package dev.samir.backend.route.model;

/**
 * This enum represents the status of an crawl.
 */
public enum CrawlStatus {
	
	/**
	 * The crawl is still running.
	 */
	ACTIVE, 
	
	/**
	 * The crawl has finished. 
	 */
	DONE,
	
	/**
	 * The crawl has marked to stop. 
	 */
	HALT,
	
	/**
	 * The crawl has failed. 
	 */
	FAILED;
	
}
