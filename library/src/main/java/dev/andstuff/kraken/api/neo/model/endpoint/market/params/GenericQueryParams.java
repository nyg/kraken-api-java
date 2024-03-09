package dev.andstuff.kraken.api.neo.model.endpoint.market.params;

import java.util.Map;

import dev.andstuff.kraken.api.neo.model.endpoint.QueryParams;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GenericQueryParams implements QueryParams {

    private final Map<String, String> params;

    @Override
    public Map<String, String> toMap() {
        return params;
    }
}
