package dev.andstuff.kraken.api.endpoint.market;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.params.AssetPairParams;
import dev.andstuff.kraken.api.endpoint.market.response.AssetPair;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

public class AssetPairEndpoint extends PublicEndpoint<Map<String, AssetPair>> {

    public AssetPairEndpoint(List<String> pairs) {
        this(pairs, AssetPairParams.Info.ALL);
    }

    public AssetPairEndpoint(List<String> pairs, AssetPairParams.Info info) {
        super("AssetPairs", new AssetPairParams(pairs, info), new TypeReference<>() {});
    }
}
