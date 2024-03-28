package dev.andstuff.kraken.api.endpoint.priv;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonPrivateEndpoint extends PrivateEndpoint<JsonNode> {

    public JsonPrivateEndpoint(String path) {
        this(path, Map.of());
    }

    public JsonPrivateEndpoint(String path, Map<String, String> postParams) {
        super(path, new GenericPostParams(postParams), new TypeReference<>() {});
    }
}
