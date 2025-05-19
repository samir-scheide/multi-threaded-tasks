package dev.samir.backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import dev.samir.backend.client.HttpClientFacade;
import dev.samir.backend.common.configuration.Environment;
import dev.samir.backend.common.configuration.EnvironmentConfiguration;
import dev.samir.backend.persistence.DataAccessObject;
import dev.samir.backend.persistence.model.CrawlTableResultSet;
import dev.samir.backend.route.model.CrawlStatus;
import dev.samir.backend.service.exception.ProcessingFailedException;

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
class CrawlServiceTest {

	private static final ReentrantLock LOCK = new ReentrantLock();
	
	private DataAccessObject dao = mock(DataAccessObject.class);
	private Environment environment = new Environment(new EnvironmentConfiguration() {
		@Override
		public String baseUrl() {
			return "http://www.valid.url";
		}
		@Override
		public String maxResultsSize() {
			return "5";
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
    	LOCK.lock();
    	when(dao.updateStatus(id, status.name(), null)).thenReturn(new CrawlTableResultSet(id, url));
    	when(dao.get(id)).thenReturn(new CrawlTableResultSet(id, url));
    }
    
    @AfterEach
    void rollDown() {
    	LOCK.unlock();
    }
    
    @Test
    void testCrawl_WithExceptionThrown() {
    	when(dao.persist(any())).thenReturn(null);
        Assertions.assertThrows(ProcessingFailedException.class, () -> service.crawl("test"));
    }
    
    @Test
    void testCrawl_WithValidKeyword() {
        String keyword = "test";
        String html = "<html><body>test<a href='/link1'>Link</a></body></html>";
        String resolvedUrl = environment.getBaseUrl() + "/link1";
        
        CrawlTableResultSet saved = new CrawlTableResultSet(id, null);

        when(dao.persist(null)).thenReturn(saved);
        when(dao.get(id)).thenReturn(saved);

        saved.getUrls().add(environment.getBaseUrl());
        when(dao.updateUrl(id, environment.getBaseUrl())).thenReturn(saved);
        
        HttpResponseAsString response = mock(HttpResponseAsString.class);
        when(response.body()).thenReturn(html);
        when(httpClientFacade.requestAsync(URI.create(environment.getBaseUrl()))).thenReturn(CompletableFuture.completedFuture(response));
        
        HttpResponseAsString responseLink = mock(HttpResponseAsString.class);
        when(responseLink.body()).thenReturn("<html>test</html>");
        when(httpClientFacade.requestAsync(URI.create(resolvedUrl))).thenReturn(CompletableFuture.completedFuture(responseLink));
 
    	Assertions.assertEquals(saved.getId(), service.crawl(keyword).getId());
    }
    
    @Test
    void testCrawl_WithHaltStatus() {
        String keyword = "test";
        String html = "<html><body>test<a href='/link1'>Link</a></body></html>";
        String resolvedUrl = environment.getBaseUrl() + "/link1";
        
        CrawlTableResultSet saved = new CrawlTableResultSet(id, null);

        when(dao.persist(null)).thenReturn(saved);
        
        saved.getUrls().add(environment.getBaseUrl());
        when(dao.updateUrl(id, environment.getBaseUrl())).thenReturn(saved);

        saved.setStatus(CrawlStatus.HALT.name());
        when(dao.get(id)).thenReturn(saved);
        
        HttpResponseAsString response = mock(HttpResponseAsString.class);
        when(response.body()).thenReturn(html);
        when(httpClientFacade.requestAsync(URI.create(environment.getBaseUrl()))).thenReturn(CompletableFuture.completedFuture(response));
        
        HttpResponseAsString responseLink = mock(HttpResponseAsString.class);
        when(responseLink.body()).thenReturn("<html></html>");
        when(httpClientFacade.requestAsync(URI.create(resolvedUrl))).thenReturn(CompletableFuture.completedFuture(responseLink));
 
        Assertions.assertEquals(saved.getId(), service.crawl(keyword).getId());
    }
    
    @Test
    void testCrawl_WithDoneStatus() {
        String keyword = "test";
        String html = "<html><body>test<a href='/link1'>Link</a></body></html>";
        
        CrawlTableResultSet saved = new CrawlTableResultSet(id, null);

        when(dao.persist(null)).thenReturn(saved);
        
        saved.getUrls().add(environment.getBaseUrl());
        when(dao.updateUrl(id, environment.getBaseUrl())).thenReturn(saved);

        saved.setStatus(CrawlStatus.DONE.name());
        when(dao.get(id)).thenReturn(saved);
        
        HttpResponseAsString response = mock(HttpResponseAsString.class);
        when(response.body()).thenReturn(html);
        when(httpClientFacade.requestAsync(URI.create(environment.getBaseUrl()))).thenReturn(CompletableFuture.completedFuture(response));
 
        Assertions.assertEquals(saved.getId(), service.crawl(keyword).getId());
    }
    
    @Test
    void testCrawl_WithRuntimeExceptionThrown() {
        String keyword = "test";
        String html = "<html><body>test<a href='/link1'>Link</a></body></html>";
        
        CrawlTableResultSet saved = new CrawlTableResultSet(id, null);

        when(dao.persist(null)).thenReturn(saved);
        when(dao.updateUrl(id, environment.getBaseUrl())).thenThrow(IllegalArgumentException.class);

        HttpResponseAsString response = mock(HttpResponseAsString.class);
        when(response.body()).thenReturn(html);
        when(httpClientFacade.requestAsync(URI.create(environment.getBaseUrl()))).thenReturn(CompletableFuture.completedFuture(response));
        when(httpClientFacade.requestAsync(Mockito.any())).thenReturn(CompletableFuture.completedFuture(response));
        
        Assertions.assertEquals(saved.getId(), service.crawl(keyword).getId());
    }

    interface HttpResponseAsString extends HttpResponse<String> {}
    
}