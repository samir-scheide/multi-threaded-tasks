package dev.samir.backend.route;

import dev.samir.backend.common.GsonTransformer;
import dev.samir.backend.common.validation.Validation;
import dev.samir.backend.route.model.CrawlRequest;
import dev.samir.backend.route.model.CrawlResponse;
import dev.samir.backend.service.CrawlService;
import spark.Request;

/**
 * Facade for the crawl service.
 * 
 * @author Scheide, Samir
 */
public final class CrawlRoute extends AroundInvoke<CrawlResponse> {
	
	/**
	 * Crawl service instance.
	 */
	private CrawlService service;
	
	/**
	 * 
	 */
	private Validation validation;
			
	/**
	 * Default constructor.
	 * @param service #service
	 */
	public CrawlRoute(CrawlService service, Validation validation) {
		this.service = service;
		this.validation = validation;
	}
	
	/**
	 * {@inheritDoc} <br>
	 * It will parse the request body into a {@link CrawlRequest} object, 
	 * validate the keyword, and then call the {@link CrawlService#crawl(String)} method.
	 * @return a {@link CrawlResponse} object containing the crawl results.
	 * @throws Exception if the request body cannot be parsed or if the keyword is invalid.
	 */
	@Override
	public CrawlResponse handle(Request request) throws Exception {
		CrawlRequest crawl = new GsonTransformer().fromJson(request.body(), CrawlRequest.class);
		return service.crawl(validation.applyKeywordValidation(crawl.getKeyword()));
	}

}
