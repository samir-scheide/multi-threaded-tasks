package dev.samir.backend.service;

import dev.samir.backend.route.model.CrawlStatus;
import dev.samir.backend.route.model.ResultsResponse;
import dev.samir.backend.service.exception.StatusNotUpdatedException;

/**
 * AnalysysResultsService is an interface that provides methods to retrieve analysis results.
 * 
 * @author Scheide, Samir <samir.scheide@gmail.com>
 */
public interface ResultsService {

	/**
	 * Retrieves the crawl results for a given identifier.
	 * @param id The identifier of the analysis results to be retrieved.
	 * @return An AnalisysResultsResponse object containing the analysis results.
	 */
	ResultsResponse list(String id);
	
	/**
	 * 
	 * @param id
	 * @param status
	 * @return
	 * @throws StatusNotUpdatedException
	 */
	ResultsResponse update(String id, CrawlStatus status);
	
}
