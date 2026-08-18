package dev.andstuff.kraken.api.endpoint.pub;

import java.util.Map;

import lombok.RequiredArgsConstructor;

/**
 * Query parameters given as raw name/value pairs, used by {@link JsonPublicEndpoint} for endpoints the library doesn't implement.
 */
@RequiredArgsConstructor
public class GenericQueryParams implements QueryParams {

    private final Map<String, String> params;

    @Override
    public Map<String, String> toMap() {
        return params;
    }
}
