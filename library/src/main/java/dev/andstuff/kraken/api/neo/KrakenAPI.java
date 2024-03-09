package dev.andstuff.kraken.api.neo;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import dev.andstuff.kraken.api.neo.model.endpoint.JsonPrivateEndpoint;
import dev.andstuff.kraken.api.neo.model.endpoint.market.AssetInfoEndpoint;
import dev.andstuff.kraken.api.neo.model.endpoint.market.AssetPairEndpoint;
import dev.andstuff.kraken.api.neo.model.endpoint.market.JsonPublicEndpoint;
import dev.andstuff.kraken.api.neo.model.endpoint.market.ServerTimeEndpoint;
import dev.andstuff.kraken.api.neo.model.endpoint.market.SystemStatusEndpoint;
import dev.andstuff.kraken.api.neo.model.endpoint.market.response.AssetInfo;
import dev.andstuff.kraken.api.neo.model.endpoint.market.response.AssetPair;
import dev.andstuff.kraken.api.neo.model.endpoint.market.response.ServerTime;
import dev.andstuff.kraken.api.neo.model.endpoint.market.response.SystemStatus;
import dev.andstuff.kraken.api.neo.rest.JDKKrakenRestRequester;
import dev.andstuff.kraken.api.neo.rest.KrakenRestRequester;

public class KrakenAPI {

    private final KrakenRestRequester restRequester;

    public KrakenAPI() {
        this(new JDKKrakenRestRequester());
    }

    public KrakenAPI(String key, String secret) {
        this(new JDKKrakenRestRequester(key, secret));
    }

    public KrakenAPI(KrakenRestRequester restRequester) {
        this.restRequester = restRequester;
    }

    /* Public endpoints */

    public ServerTime serverTime() {
        return restRequester.execute(new ServerTimeEndpoint());
    }

    public SystemStatus systemStatus() {
        return restRequester.execute(new SystemStatusEndpoint());
    }

    public Map<String, AssetInfo> assetInfo(List<String> assets) {
        return restRequester.execute(new AssetInfoEndpoint(assets));
    }

    public Map<String, AssetInfo> assetInfo(List<String> assets, String assetClass) {
        return restRequester.execute(new AssetInfoEndpoint(assets, assetClass));
    }

    public Map<String, AssetPair> assetPairs(List<String> pair) {
        return restRequester.execute(new AssetPairEndpoint(pair));
    }

    public Map<String, AssetPair> assetPairs(List<String> pair, AssetPair.Info info) {
        return restRequester.execute(new AssetPairEndpoint(pair, info));
    }

    /* Unimplemented endpoints */

    public JsonNode fetchPublic(String path) {
        return restRequester.execute(new JsonPublicEndpoint(path));
    }

    public JsonNode fetchPublic(String path, Map<String, String> queryParams) {
        return restRequester.execute(new JsonPublicEndpoint(path, queryParams));
    }

    public JsonNode fetchPrivate(String path) {
        return restRequester.execute(new JsonPrivateEndpoint(path));
    }

    public JsonNode fetchPrivate(String path, Map<String, String> params) {
        return restRequester.execute(new JsonPrivateEndpoint(path, params));
    }
}
