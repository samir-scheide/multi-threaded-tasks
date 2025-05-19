package dev.samir.backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.samir.backend.client.HttpClientFacade;
import dev.samir.backend.common.configuration.Environment;
import dev.samir.backend.common.configuration.EnvironmentConfiguration;
import dev.samir.backend.persistence.DataAccessObject;
import dev.samir.backend.persistence.model.CrawlTableResultSet;
import dev.samir.backend.route.model.CrawlStatus;
import dev.samir.backend.route.model.ResultsResponse;
import dev.samir.backend.service.exception.NoResultsException;

/**
 * Testing the service layer. 
 * The {@link ServicesImpl} contains the implementation of two required actions:
 * <ul>
 * 	<li>Results: brings all resultas from parcial and completed crawlings;</li>
 * 	<li>Crawl': which does the heavy job, crawling HTML documents starting from the base URL.</li>
 * </ul>
 * Even thoug this another action is not signed as required, I thought it woudl become in hand implement it:
 * <ul>
 * 	<li>Update: was built to enable updating crawl status and somehow stop (halt) a specific crawling by ID;</li>
 * </ul>
 * Tests will cover most success cases and some edge caes as well.
 * 
 * @author Scheide, Samir
 */
class GetResultsServiceTest {

	private DataAccessObject dao = mock(DataAccessObject.class);
	private Environment environment = new Environment(new EnvironmentConfiguration() {
		@Override
		public String baseUrl() {
			return "http://www.valid.url";
		}
	});
	private HttpClientFacade httpClientFacade = mock(HttpClientFacade.class);
	private ServicesImpl service = new ServicesImpl(dao, environment, httpClientFacade);
	
	private String id = "12341234";
	private String url = "http://some.url.stored.com";
	
    @BeforeAll
    static void init() {
    	System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }
    
    @BeforeEach
    void setUp() {
    	when(dao.get(id)).thenReturn(new CrawlTableResultSet(id, url));
    }
    
    @Test
    void testGetResults_WithValidId() {
    	ResultsResponse resultsList = service.list(id);
    	Assertions.assertNotNull(resultsList);
    	Assertions.assertNotNull(resultsList.getId());
    	Assertions.assertTrue(resultsList.getUrls().contains(url));
    	Assertions.assertEquals(CrawlStatus.ACTIVE, resultsList.getStatus());
    }
    
    @Test
    void testGetResults_WithInvalidId() {
    	when(dao.get("1")).thenThrow(IllegalArgumentException.class);
    	Assertions.assertThrows(IllegalArgumentException.class, () -> service.list("1"));
    }
    
    @Test
    void testGetResults_WhenNotFound() {
    	when(dao.get(any())).thenReturn(null);
    	Assertions.assertThrows(NoResultsException.class, () -> service.list("any"));
    }
	
}
