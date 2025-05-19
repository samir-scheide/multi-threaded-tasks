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
import dev.samir.backend.service.exception.StatusNotUpdatedException;

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
class UpdateServiceTest {

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
	private CrawlStatus status = CrawlStatus.DONE;
	private String url = "http://some.url.stored.com";
	
    @BeforeAll
    static void init() {
    	System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }
    
    @BeforeEach
    void setUp() {
    	when(dao.updateStatus(id, status.name(), null)).thenReturn(new CrawlTableResultSet(id, url));
    	when(dao.get(id)).thenReturn(new CrawlTableResultSet(id, url));
    }
    
    @Test
    void testUpdate_WhenNotFound() {
    	when(dao.updateStatus(any(), any(), any())).thenReturn(null);
    	Assertions.assertThrows(StatusNotUpdatedException.class, () -> service.update(id, status));
    }
    
    @Test
    void testUpdate_WithInvalidId() {
    	when(dao.updateStatus(any(), any(), any())).thenThrow(IllegalArgumentException.class);
    	Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(null, status));
    }
    
    @Test
    void testUpdate_WithInvalidStatus() {
    	when(dao.updateStatus(any(), any(), any())).thenThrow(IllegalArgumentException.class);
    	Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(id, CrawlStatus.HALT));
    }
    
    @Test
    void testUpdate_WithValidIdAndStatus() {
    	ResultsResponse updatedResult = service.update(id, status);
    	Assertions.assertNotNull(updatedResult);
    	Assertions.assertNotNull(updatedResult.getId());
    	Assertions.assertEquals(status, updatedResult.getStatus());
    }
	
}
