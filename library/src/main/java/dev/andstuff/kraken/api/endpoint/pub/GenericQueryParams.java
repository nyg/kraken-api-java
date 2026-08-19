package dev.andstuff.kraken.api.endpoint.pub;

import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class GenericQueryParams implements QueryParams {

    private final Map<String, String> params;

    @Override
    public Map<String, String> toMap() {
        return params;
    }
}
