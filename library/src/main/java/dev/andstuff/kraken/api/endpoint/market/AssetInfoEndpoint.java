package dev.andstuff.kraken.api.endpoint.market;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.params.AssetInfoParams;
import dev.andstuff.kraken.api.endpoint.market.response.AssetInfo;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

/**
 * The public {@code Assets} endpoint, returning information about the assets available on Kraken.
 */
public class AssetInfoEndpoint extends PublicEndpoint<Map<String, AssetInfo>> {

    /**
     * Creates the endpoint for the {@code currency} asset class.
     *
     * @param assets the assets to retrieve information for, e.g. {@code ["BTC", "ETH"]}
     */
    public AssetInfoEndpoint(List<String> assets) {
        this(assets, "currency");
    }

    /**
     * Creates the endpoint.
     *
     * @param assets the assets to retrieve information for, e.g. {@code ["BTC", "ETH"]}
     * @param assetClass the asset class to filter on, e.g. {@code currency}
     */
    public AssetInfoEndpoint(List<String> assets, String assetClass) {
        super("Assets", new AssetInfoParams(assets, assetClass), new TypeReference<>() {});
    }
}
