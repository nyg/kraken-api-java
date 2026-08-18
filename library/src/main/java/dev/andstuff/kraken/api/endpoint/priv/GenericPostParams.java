package dev.andstuff.kraken.api.endpoint.priv;

import java.util.HashMap;
import java.util.Map;

/**
 * POST parameters given as raw name/value pairs, used by {@link JsonPrivateEndpoint} for endpoints the library doesn't implement.
 */
public class GenericPostParams extends PostParams {

    private final Map<String, String> params = new HashMap<>();

    /**
     * Creates parameters from the given name/value pairs.
     *
     * @param params the POST parameters, as expected by Kraken
     */
    public GenericPostParams(Map<String, String> params) {
        this.params.putAll(params);
    }

    @Override
    protected Map<String, String> params() {
        return params;
    }
}
