package dev.andstuff.kraken.api.endpoint.market;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.params.AssetPairParams;
import dev.andstuff.kraken.api.endpoint.market.response.AssetPairs;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

public class AssetPairEndpoint extends PublicEndpoint<AssetPairs> {

    public AssetPairEndpoint() {
        this(null, null);
    }

    public AssetPairEndpoint(List<String> pairs) {
        this(pairs, null);
    }

    public AssetPairEndpoint(List<String> pairs, AssetPairParams.Info info) {
        super("AssetPairs", new AssetPairParams(pairs, info), new TypeReference<>() {});
    }
}
