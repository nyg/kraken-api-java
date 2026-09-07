package dev.andstuff.kraken.api.endpoint.market.params;

import static dev.andstuff.kraken.api.endpoint.pub.QueryParams.putIfNonNull;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.pub.QueryParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder(toBuilder = true)
public class OhlcParams implements QueryParams {

    @NonNull
    private final String pair;

    private final Integer interval;

    private final Long since;

    private final Integer assetVersion;

    private final String assetClass;

    @Override
    public Map<String, String> toMap() {
        Map<String, String> params = new HashMap<>();
        params.put("pair", pair);
        putIfNonNull(params, "interval", interval, String::valueOf);
        putIfNonNull(params, "since", since, String::valueOf);
        putIfNonNull(params, "assetVersion", assetVersion, String::valueOf);
        putIfNonNull(params, "asset_class", assetClass, v -> v);
        return params;
    }
}
