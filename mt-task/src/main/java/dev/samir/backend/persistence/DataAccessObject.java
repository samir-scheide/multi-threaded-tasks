package dev.samir.backend.persistence;

import java.util.Random;

import dev.samir.backend.persistence.model.CrawlTableResultSet;

/**
 * 
 * @author Scheide, Samir
 */
public interface DataAccessObject {
	
	/**
	 * Random number generator for generating unique identifiers.
	 */
	static final Random RANDOM = new Random();

	/**
	 * Retrieves an CrawlTableResultSet object from the database using the given ID.
	 * @param id crawl ID
	 * @return
	 */
	CrawlTableResultSet get(String id);
	
	/**
	 * 
	 * @param id
	 * @param url
	 * @return
	 */
	CrawlTableResultSet persist(String url);
	
	/**
	 * 
	 * @param id
	 * @param status
	 */
	CrawlTableResultSet updateUrl(String id, String url);
	
	/**
	 * d
	 * @param id
	 * @param status
	 * @param message
	 */
	CrawlTableResultSet updateStatus(String id, String status, String message);
	
}
