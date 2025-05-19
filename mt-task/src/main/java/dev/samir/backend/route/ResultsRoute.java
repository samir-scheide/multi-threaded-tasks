package dev.samir.backend.route;

import dev.samir.backend.common.validation.Validation;
import dev.samir.backend.route.model.ResultsResponse;
import dev.samir.backend.service.ResultsService;
import spark.Request;

/**
 * Facade for the results service.
 * 
 * @author Scheide, Samir
 */
public final class ResultsRoute extends AroundInvoke<ResultsResponse> {
	
	/**
	 * The results service.
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
	public ResultsRoute(ResultsService service, Validation validation) {
		this.service = service;
		this.validation = validation;
	}
	
	/**
	 * Receive the request, extracts anbd validate the given ID and return all crawl results - parcials too - for it.
	 * @param request {@link Request} containing the id of the crawl results to be retrieved.
	 * @return {@link ResultsResponse} containing the crawl results for the given id.
	 */
	@Override
	protected ResultsResponse handle(Request request) {
		return service.list(validation.applyIdValidation(request.params("id")));
	}

}
