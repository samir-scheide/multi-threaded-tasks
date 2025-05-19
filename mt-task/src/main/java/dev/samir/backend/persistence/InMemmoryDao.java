package dev.samir.backend.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.samir.backend.common.GenerateRandom;
import dev.samir.backend.common.validation.Validation;
import dev.samir.backend.persistence.model.CrawlTableResultSet;
import dev.samir.backend.route.model.CrawlStatus;

/**
 * CrawlerInMemmoryDao is a data access object (DAO) that provides methods to interact with the database.
 * It implements the {@link DataAccessObject} interface and provides methods to get, persist, and update data.
 * It uses a ConcurrentHashMap to store the data in memory.
 * 
 * @author Scheide, Samir
 */ 
public final class InMemmoryDao implements DataAccessObject {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(InMemmoryDao.class);
	
	/**
	 * A HashMap that stores the CrawlTableResultSet objects.
	 * The key is a String representing the ID of the object, and the value is the CrawlTableResultSet object itself.
	 */
    private static final Map<String, CrawlTableResultSet> ANALYSIS_TABLE = new ConcurrentHashMap<>();
    
    /**
     * Default validation for this
     */
    private Validation validation;
    
    /**
     * 
     */
    private GenerateRandom generateRandom;
    
    public InMemmoryDao(Validation validation, GenerateRandom generateRandom) {
		this.validation = validation;
		this.generateRandom = generateRandom;
	}
    
	/**
	 * If the ID is null or empty, an IllegalArgumentException is thrown because not having the identifier becomes impossible to retrieve the analysis.
	 * Whereas, if the ID is not found in the database, a warning is logged and it is returned the same null element, expliciting the absence of the record. 
	 * Eventually, if the ID is found in the database, returns the CrawlTableResultSet object processing at least 1 (one) URL.
	 */
	public final CrawlTableResultSet get(String id) {
		LOGGER.debug("Retrieving ID: {}", id);
		return Optional.ofNullable(validation.applyIdValidation(id))
			.map(ANALYSIS_TABLE::get)
            .orElseThrow(() -> {
            	LOGGER.error("crawl was not found: {}", id);
            	throw new NotFoundException();
            });
	}
	
	/**
	 */
	public final CrawlTableResultSet persist(String url) {
		LOGGER.debug("Persisting URL: {}", url);
		
		CrawlTableResultSet crawlResultSet = new CrawlTableResultSet(generateRandom.alphaNumericSequence(), 
				url != null ? validation.applyURLValidation(url) : null);
		
		Optional.ofNullable(ANALYSIS_TABLE.get(crawlResultSet.getId()))
			.ifPresentOrElse(result -> {
				LOGGER.error("crawl already exists: {} with {} urls", result.getId(), result.getUrls().size());
				throw new NotPersistedException();
			}, () -> {
				ANALYSIS_TABLE.put(crawlResultSet.getId(), crawlResultSet);
			});
		
		return crawlResultSet;
		
	}

	/**
	 * 
	 */
	@Override
	public CrawlTableResultSet updateUrl(String id, String url) {
		
		LOGGER.debug("Updating ID: {}, Url: {}", id, url);
		
		Optional.ofNullable(validation.applyURLValidation(url))
			.ifPresent(validUrl -> Optional.ofNullable(ANALYSIS_TABLE.get(validation.applyIdValidation(id)))
					.ifPresentOrElse(result -> {
						result.getUrls().add(url);
						ANALYSIS_TABLE.put(id, result);
					}, () -> {
						LOGGER.error("error while updating crawl {} with url {}", id, url);
						throw new NotUpdatedException();
				})
			);
		
		return ANALYSIS_TABLE.get(id);
		
	}
	
	/**
	 * Updates the status of the analysis with the given ID.
	 * If the ID is null or empty,, an IllegalArgumentException is thrown.
	 * If the status is null or empty, an IllegalArgumentException is thrown.
	 * @param id the ID of the analysis to update
	 * @param status the new status to set
	 */
	@Override
	public final CrawlTableResultSet updateStatus(String id, String status, String optionalMessage) {
		LOGGER.debug("Updating ID: {}, Status: {}", id, status);
		
		Optional.ofNullable(validation.applyStatusValidation(status))
			.ifPresent(incomingStatus -> Optional.ofNullable(get(id)).ifPresentOrElse(current -> {
				if (!CrawlStatus.DONE.equals(CrawlStatus.valueOf(current.getStatus()))) {
					current.setStatus(status);
					current.setMessage(optionalMessage);
					ANALYSIS_TABLE.put(id, current);
				} else {
					LOGGER.warn("Current crawl status {} does not allow update to {}",
							current.getStatus(), incomingStatus);
				}
			}, () -> {
				LOGGER.error("error while updating crawl {} status to {}", id, status);
				throw new NotUpdatedException();
			})
		);
		
		return ANALYSIS_TABLE.get(id);
	}

	/**
	 * This method clears the database by removing all entries from the ANALYSIS_TABLE.
	 * !Important: this method is used for testing purposes only.
	 * @see InMemmoryDaoTest
	 */
	final void clear() {
		ANALYSIS_TABLE.clear();
		LOGGER.debug("InMemmoryDao cleared.");
	}
	
}
