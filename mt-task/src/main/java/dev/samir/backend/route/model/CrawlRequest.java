package dev.samir.backend.route.model;

/**
 * Crawl request model.
 * 
 * @author Scheide, Samir
 */
public final class CrawlRequest {

	/**
	 * The keyword that should be matched against the crawled data.
	 */
	private String keyword;
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
}
