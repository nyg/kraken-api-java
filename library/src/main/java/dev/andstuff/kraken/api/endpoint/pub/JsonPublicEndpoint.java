package dev.andstuff.kraken.api.endpoint.pub;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * A public endpoint whose response is left as a {@link JsonNode}, used for endpoints the library doesn't implement.
 */
public class JsonPublicEndpoint extends PublicEndpoint<JsonNode> {

    /**
     * Creates an untyped public endpoint without query parameters.
     *
     * @param path the endpoint path, e.g. {@code Trades} for {@code /0/public/Trades}
     */
    public JsonPublicEndpoint(String path) {
        super(path, QueryParams.EMPTY, new TypeReference<>() {});
    }

    /**
     * Creates an untyped public endpoint.
     *
     * @param path the endpoint path, e.g. {@code Trades} for {@code /0/public/Trades}
     * @param queryParams the URL query parameters, as expected by Kraken
     */
    public JsonPublicEndpoint(String path, Map<String, String> queryParams) {
        super(path, new GenericQueryParams(queryParams), new TypeReference<>() {});
    }
}
