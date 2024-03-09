package dev.andstuff.kraken.api.neo.model.endpoint.market.params;

import java.util.List;
import java.util.Map;

import dev.andstuff.kraken.api.neo.model.endpoint.QueryParams;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AssetInfoParams implements QueryParams {

    private final List<String> assets;
    private final String assetClass;

    public Map<String, String> toMap() {
        return Map.of(
                "asset", String.join(",", assets),
                "aclass", assetClass);
    }
}
