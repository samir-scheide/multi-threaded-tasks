package dev.samir.backend;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.samir.backend.client.HttpHtmlClientFacade;
import dev.samir.backend.common.GsonTransformer;
import dev.samir.backend.common.RandomUtils;
import dev.samir.backend.common.TransformToJson;
import dev.samir.backend.common.configuration.DefaultEnvironmentConfiguration;
import dev.samir.backend.common.configuration.Environment;
import dev.samir.backend.common.validation.DefaultValidation;
import dev.samir.backend.persistence.InMemmoryDao;
import dev.samir.backend.route.CrawlRoute;
import dev.samir.backend.route.ResultsRoute;
import dev.samir.backend.route.UpdateRoute;
import dev.samir.backend.service.ServicesImpl;

/**
 * This class is a simple HTTP server that handles GET and POST requests.
 * It uses the Spark framework to define the routes and their corresponding handlers.
 * <p>
 * The server has two endpoints: <br>
 * <ol>
 *   <li>GET /crawl/:id - Responds with a message containing the ID from the URL.</li>
 *   <li>POST /crawl - Responds with a message containing the body of the request.</li>
 * </ol>
 * </p>
 * The server go throught different URLs to find the keyword provided and then lists the URLs where the keyword was found. 
 * The analysis is made assynchronously, so the server is able to handle multiple requests at the same time. 
 * Every analysys should be persisted and keept as long as the application is running. 
 * The analysis should be divided into two groups: <br>
 * <ul>
 *  <li>Active: The analysis is still running.</li>
 *  <li>Done: The analysis has finished.</li>
 * </ul>
 * A third group is also created to handle the errors, even thought it is not in the scope: <br>
 * <ul>
 *   <li>Failed: The analysis has failed.</li>
 * </ul>
 * 
 * The server will do the following validations: <br>
 * <ul>
 *   <li>The URL will be a environment variable named <b>BASE_URL</b>.</li>
 *   <li>The keyword must have at least <b>4</b> characters and a maximum of <b>32</b>.</li>
 *   <li>The analysys must have a <b>unique</b> identifier.</li>
 *   <li>The crawling must follow both relative links, and absolute links when they have the same <b>BASE_URL</b>.</li>
 * </ul>
 * 
 * @author Scheide, Samir
 */
public class Main {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	/**
	 * 
	 */
	public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
	
    /**
     * Releasing thread resources.
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.debug("Shutting down application gracefully.");
            if (Main.EXECUTOR_SERVICE != null && !Main.EXECUTOR_SERVICE.isShutdown()) {
            	Main.EXECUTOR_SERVICE.shutdown();
                try {
                    if (!Main.EXECUTOR_SERVICE.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)) {
                    	Main.EXECUTOR_SERVICE.shutdownNow();
                    }
                } catch (InterruptedException e) {
                	Main.EXECUTOR_SERVICE.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            LOGGER.debug("Shutdown complete.");
        }));
    }
	
	/**
	 * The main method sets up the server and defines the routes.
	 * @param args Command line arguments (not used).
	 */
    public static void main(String[] args) {
    	
    	DefaultValidation validation = new DefaultValidation();
    	ServicesImpl service = new ServicesImpl(
    			new InMemmoryDao(validation, new RandomUtils()), 
    			new Environment(new DefaultEnvironmentConfiguration()),
    			new HttpHtmlClientFacade());
    	
    	TransformToJson transformer = new GsonTransformer();
    	
        get("/crawl/:id", 
        		new ResultsRoute(service, validation), transformer::toJson);
        
        post("/crawl", 
        		new CrawlRoute(service, validation), transformer::toJson);
        
        put("/crawl/:id/status/:status", 
        		new UpdateRoute(service, validation), transformer::toJson);
        
    }
    
}
