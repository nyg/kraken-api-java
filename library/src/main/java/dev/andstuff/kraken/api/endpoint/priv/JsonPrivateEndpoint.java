package dev.andstuff.kraken.api.endpoint.priv;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * A private endpoint whose response is left as a {@link JsonNode}, used for endpoints the library doesn't implement.
 */
public class JsonPrivateEndpoint extends PrivateEndpoint<JsonNode> {

    /**
     * Creates an untyped private endpoint without parameters.
     *
     * @param path the endpoint path, e.g. {@code Balance} for {@code /0/private/Balance}
     */
    public JsonPrivateEndpoint(String path) {
        this(path, Map.of());
    }

    /**
     * Creates an untyped private endpoint.
     *
     * @param path the endpoint path, e.g. {@code Balance} for {@code /0/private/Balance}
     * @param postParams the POST parameters, as expected by Kraken, the nonce being added by the library
     */
    public JsonPrivateEndpoint(String path, Map<String, String> postParams) {
        super(path, new GenericPostParams(postParams), new TypeReference<>() {});
    }
}
