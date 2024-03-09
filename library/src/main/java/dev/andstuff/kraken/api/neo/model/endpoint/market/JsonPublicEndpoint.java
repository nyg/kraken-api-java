package dev.andstuff.kraken.api.neo.model.endpoint.market;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import dev.andstuff.kraken.api.neo.model.endpoint.QueryParams;
import dev.andstuff.kraken.api.neo.model.endpoint.market.params.GenericQueryParams;

public class JsonPublicEndpoint extends PublicEndpoint<JsonNode> {

    public JsonPublicEndpoint(String path) {
        super(path, QueryParams.EMPTY, new TypeReference<>() {});
    }

    public JsonPublicEndpoint(String path, Map<String, String> queryParams) {
        super(path, new GenericQueryParams(queryParams), new TypeReference<>() {});
    }
}
