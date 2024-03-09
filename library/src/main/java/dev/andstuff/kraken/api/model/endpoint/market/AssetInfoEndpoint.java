package dev.andstuff.kraken.api.model.endpoint.market;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.model.endpoint.market.params.AssetInfoParams;
import dev.andstuff.kraken.api.model.endpoint.market.response.AssetInfo;
import dev.andstuff.kraken.api.model.endpoint.pub.PublicEndpoint;

public class AssetInfoEndpoint extends PublicEndpoint<Map<String, AssetInfo>> {

    public AssetInfoEndpoint(List<String> assets) {
        this(assets, "currency");
    }

    public AssetInfoEndpoint(List<String> assets, String assetClass) {
        super("Assets", new AssetInfoParams(assets, assetClass), new TypeReference<>() {});
    }
}
