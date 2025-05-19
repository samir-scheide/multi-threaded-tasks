package dev.samir.backend.service;

import dev.samir.backend.route.model.CrawlResponse;
import dev.samir.backend.service.exception.ProcessingFailedException;

/**
 * CrawlService is an interface that provides methods to crawl data based on a given keyword.
 * 
 * @author Scheide, Samir
 */
public interface CrawlService {
	
	/**
	 * Crawls data based on the provided keyword.
	 * @param keyword The keyword to be used for crawling data.
	 * @return A CrawlResponse object containing the crawled data.
	 * @throws ProcessingFailedException if the crawling process fails.
	 */
	CrawlResponse crawl(String keyword) throws ProcessingFailedException;
	
}
