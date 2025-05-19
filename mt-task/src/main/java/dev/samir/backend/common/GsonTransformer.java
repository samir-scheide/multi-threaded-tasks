package dev.samir.backend.common;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import dev.samir.backend.client.StatusCode;
import dev.samir.backend.route.model.CrawlStatus;

/**
 * Adapter for transforming objects to JSON and vice versa using Goole {@link Gson}.
 * <p>
 * So far, it implements two interfaces: <br>
 * - {@link TransformToJson} for transforming objects to JSON <br>
 * - {@link TransformFromObject} for transforming JSON to objects
 * </p>
 * 
 * @author Scheide, Samir
 */
public final class GsonTransformer implements TransformToJson, TransformFromObject {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Gson.class);
	
	/**
	 * Gson instance for JSON serialization and deserialization using pretty printing.
	 */
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
		.registerTypeAdapter(StatusCode.class, new StatusCodeSerializer())
		.registerTypeAdapter(CrawlStatus.class, new CrawlStatusSerializer())
		.create();
	
	/**
	 * Private method to transform an object to JSON.
	 * @param src the source object
	 * @return the JSON representation of the object
	 */
	private static final String _toJson(Object src) {
		LOGGER.debug("Source object: {}", src);
		return GSON.toJson(src);
	}
	
	/**
	 * Private method to transform JSON to an object.
	 * @param <T> the type of the object
	 * @param src the source JSON string
	 * @param clazz the class of the object
	 * @return the object representation of the JSON
	 */
	private static final <T> T _fromJson(String src, Class<T> clazz) {
		LOGGER.debug("Source JSON: {}", src);
		return GSON.fromJson(src, clazz);
	}
	
	/**
	 * JsonSerializer for {@link StatusCode} enum. Instead of serializing the enum
	 * name, it serializes the code of the enum.
	 * 
	 * @see {@link StatusCode}
	 */
	static final class StatusCodeSerializer implements JsonSerializer<StatusCode> {
		@Override
		public JsonElement serialize(StatusCode src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.getCode());
		}
	}
	
	/**
	 * JsonSerializer for {@link CrawlStatus} enum. Instead of serializing the enum
	 * name, it serializes the name of the enum in lower case.
	 * 
	 * @see {@link CrawlStatus}
	 */
	static final class CrawlStatusSerializer implements JsonSerializer<CrawlStatus> {
		@Override
		public JsonElement serialize(CrawlStatus src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.name().toLowerCase());
		}
	}

	@Override
	public String toJson(Object src) {
		return GsonTransformer._toJson(src);
	}

	@Override
	public <T> T fromJson(String src, Class<T> clazz) {
		return GsonTransformer._fromJson(src, clazz);
	}
	
}
