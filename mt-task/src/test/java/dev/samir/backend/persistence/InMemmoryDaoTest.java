package dev.samir.backend.persistence;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import dev.samir.backend.common.GenerateRandom;
import dev.samir.backend.common.validation.Validation;
import dev.samir.backend.persistence.model.CrawlTableResultSet;
import dev.samir.backend.route.model.CrawlStatus;

/**
 * Testing the databse layer {@link InMemmoryDao} which uses a in memmopry database map. <br>
 * Most common operations should be tested:
 * <ul>
 * 	<li>Persist: receives only the URL and randomly generates de ID before saving at the database;</li>
 *  <li>Update (url) or (status): Update de URL or the status. Depends on both valid identifier AND valid URL or STATUS;</li>
 * </ul>
 * @author Scheide, Samir
 */
class InMemmoryDaoTest {

    private Validation validation = mock(Validation.class);
    private GenerateRandom generateRandom = mock(GenerateRandom.class);
    private InMemmoryDao dao = new InMemmoryDao(validation, generateRandom);

    private String id = "is_valid";
    private String url = "http://valid.url.com";
    
    @BeforeAll
    static void init() {
    	System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }
    
    @BeforeEach
    void setUp() {
        dao.clear();
        Mockito
        	.when(generateRandom.alphaNumericSequence()).thenReturn(id);
        Mockito
        	.when(validation.applyIdValidation(id)).thenReturn(id);
        Mockito
        	.when(validation.applyURLValidation(url)).thenReturn(url);
        Mockito
        	.when(validation.applyStatusValidation(Mockito.any())).thenReturn(CrawlStatus.DONE);
    }
    
    @Test
    void testPersist_WithInvalidUrl() {
    	Mockito.when(validation.applyURLValidation(url)).thenThrow(IllegalArgumentException.class);
    	Assertions.assertThrows(IllegalArgumentException.class, () -> dao.persist(url));
    }

    @Test
    void testPersist_WithValidUrl() {
        CrawlTableResultSet result = dao.persist(url);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(id, result.getId());
        Assertions.assertTrue(result.getUrls().contains(url));
    }
    
    @Test
    void testPersist_WithValidUrlTwice() {
    	CrawlTableResultSet result = dao.persist(url);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(id, result.getId());
        Assertions.assertThrows(NotPersistedException.class, () -> dao.persist(result.getId()));
    }
    
    @Test
    void testUpdate_WithInvalidUrl() {
    	Mockito.when(validation.applyIdValidation(id)).thenThrow(IllegalArgumentException.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> dao.updateUrl(id, url));
    }
    
    @Test
    void testUpdate_WithInvalidId() {
    	Mockito.when(validation.applyIdValidation(id)).thenThrow(IllegalArgumentException.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> dao.updateUrl(id, url));
    }
    
    @Test
    void testUpdate_WithInvalidStatus() {
        Mockito.when(validation.applyStatusValidation(Mockito.any())).thenThrow(IllegalArgumentException.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> dao.updateStatus(id, "inexistent", null));
    }
    
    @Test
    void testUpdate_WhenCrawlNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> dao.updateStatus("invalid", "done", null));
    }
    
    @Test
    void testUpdate_WithValidIdAndUrl() {
    	String newUrl = "http://www.new.url";
        CrawlTableResultSet persistResult = dao.persist(url);
        
        Mockito
    		.when(validation.applyURLValidation(newUrl)).thenReturn(newUrl);
        CrawlTableResultSet updateResult = dao.updateUrl(persistResult.getId(), newUrl);
        
        Assertions.assertNotNull(updateResult);
        Assertions.assertEquals(id, updateResult.getId());
        Assertions.assertTrue(updateResult.getUrls().contains(newUrl));
    }
    
    @Test
    void testUpdateStatus_WithValidIdAndStatus() {
        CrawlTableResultSet persistResult = dao.persist(url);
        CrawlTableResultSet updateResult = dao.updateStatus(persistResult.getId(), "done", null);
        
        Assertions.assertNotNull(updateResult);
        Assertions.assertEquals(CrawlStatus.DONE.name().toLowerCase(), updateResult.getStatus().toLowerCase());
    }

}
