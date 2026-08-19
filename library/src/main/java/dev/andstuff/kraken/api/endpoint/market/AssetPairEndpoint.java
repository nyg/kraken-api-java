package dev.andstuff.kraken.api.endpoint.market;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.params.AssetPairParams;
import dev.andstuff.kraken.api.endpoint.market.response.AssetPairs;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

/**
 * The public {@code AssetPairs} endpoint, returning the asset pairs tradable on Kraken.
 */
public class AssetPairEndpoint extends PublicEndpoint<AssetPairs> {

    /**
     * Creates the endpoint for all asset pairs.
     */
    public AssetPairEndpoint() {
        this(null, null);
    }

    /**
     * Creates the endpoint.
     *
     * @param pairs the asset pairs to retrieve, e.g. {@code ["ETH/BTC", "ETH/USD"]}
     */
    public AssetPairEndpoint(List<String> pairs) {
        this(pairs, null);
    }

    /**
     * Creates the endpoint, restricting the information returned for each pair.
     *
     * @param pairs the asset pairs to retrieve, {@code null} for all of them
     * @param info the subset of information to return, {@code null} for all of it
     */
    public AssetPairEndpoint(List<String> pairs, AssetPairParams.Info info) {
        super("AssetPairs", new AssetPairParams(pairs, info), new TypeReference<>() {});
    }
}
