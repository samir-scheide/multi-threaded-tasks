package dev.samir.backend.route;

import dev.samir.backend.common.validation.Validation;
import dev.samir.backend.route.model.CrawlRequest;
import dev.samir.backend.route.model.CrawlResponse;
import dev.samir.backend.route.model.CrawlStatus;
import dev.samir.backend.route.model.ResultsResponse;
import dev.samir.backend.service.CrawlService;
import dev.samir.backend.service.ResultsService;
import spark.Request;

/**
 * Facade for crawl update service.
 * 
 * @author Scheide, Samir
 */
public final class UpdateRoute extends AroundInvoke<ResultsResponse> {
	
	/**
	 * Crawl service instance.
	 */
	private ResultsService service;
	
	/**
	 * 
	 */
	private Validation validation;
	
	/**
	 * Default constructor.
	 * @param service #service
	 */
	public UpdateRoute(ResultsService service, Validation validation) {
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
	public ResultsResponse handle(Request request) throws Exception {
		String id = validation.applyIdValidation(request.params(":id"));
		CrawlStatus status = validation.applyStatusValidation(request.params(":status"));
		return service.update(id, status);
	}
	
}
