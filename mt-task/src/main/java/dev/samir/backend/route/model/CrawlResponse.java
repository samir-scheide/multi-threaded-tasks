package dev.samir.backend.route.model;

/**
 * Crawl response model.
 * 
 * @author Scheide, Samir
 */
public final class CrawlResponse {

	/**
	 * The unique identifier of the crawl.
	 */
	private String id;
	
	public CrawlResponse(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
}
