package dev.andstuff.kraken.api.endpoint.priv;

import java.util.HashMap;
import java.util.Map;

class GenericPostParams extends PostParams {

    private final Map<String, String> params = new HashMap<>();

    GenericPostParams(Map<String, String> params) {
        this.params.putAll(params);
    }

    @Override
    protected Map<String, String> params() {
        return params;
    }
}
