package dev.andstuff.kraken.api.neo.model.endpoint.market;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.neo.model.endpoint.market.params.AssetPairParams;
import dev.andstuff.kraken.api.neo.model.endpoint.market.response.AssetPair;

public class AssetPairEndpoint extends PublicEndpoint<Map<String, AssetPair>> {

    public AssetPairEndpoint(List<String> pairs) {
        this(pairs, AssetPair.Info.ALL);
    }

    public AssetPairEndpoint(List<String> pairs, AssetPair.Info info) {
        super("AssetPairs", new AssetPairParams(pairs, info), new TypeReference<>() {});
    }
}
