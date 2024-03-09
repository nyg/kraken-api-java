package dev.andstuff.kraken.api.neo.model.endpoint.market.params;

import java.util.List;
import java.util.Map;

import dev.andstuff.kraken.api.neo.model.endpoint.QueryParams;
import dev.andstuff.kraken.api.neo.model.endpoint.market.response.AssetPair;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AssetPairParams implements QueryParams {

    private final List<String> pairs;
    private final AssetPair.Info info;

    public Map<String, String> toMap() {
        return Map.of(
                "pair", String.join(",", pairs),
                "info", info.getValue());
    }
}
