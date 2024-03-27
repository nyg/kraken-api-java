package dev.andstuff.kraken.example;

import static dev.andstuff.kraken.example.helper.CredentialsHelper.readFromFile;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.model.KrakenCredentials;
import dev.andstuff.kraken.api.model.endpoint.market.params.AssetPairParams;
import dev.andstuff.kraken.api.model.endpoint.market.response.AssetInfo;
import dev.andstuff.kraken.api.model.endpoint.market.response.AssetPair;
import dev.andstuff.kraken.api.model.endpoint.market.response.ServerTime;
import dev.andstuff.kraken.api.model.endpoint.market.response.SystemStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleExamples {

    public static void main(String[] args) {

        /* Public endpoint examples */

        KrakenAPI publicAPI = new KrakenAPI();

        ServerTime serverTime = publicAPI.serverTime();
        log.info("{}", serverTime);

        SystemStatus systemStatus = publicAPI.systemStatus();
        log.info("{}", systemStatus);

        Map<String, AssetInfo> assets1 = publicAPI.assetInfo(List.of("BTC", "ETH"));
        log.info("{}", assets1);

        Map<String, AssetInfo> assets2 = publicAPI.assetInfo(List.of("DOT", "ADA"), "currency");
        log.info("{}", assets2);

        Map<String, AssetPair> pairs1 = publicAPI.assetPairs(List.of("ETH/BTC", "ETH/USD"));
        log.info("{}", pairs1);

        Map<String, AssetPair> pairs2 = publicAPI.assetPairs(List.of("DOT/USD", "ADA/USD"), AssetPairParams.Info.MARGIN);
        log.info("{}", pairs2);

        JsonNode ticker = publicAPI.query(KrakenAPI.Public.TICKER, Map.of("pair", "XBTEUR"));
        log.info("{}", ticker);

        JsonNode trades = publicAPI.queryPublic("Trades", Map.of("pair", "XBTUSD", "count", "1"));
        log.info("{}", trades);

        /* Private endpoint example */

        KrakenCredentials credentials = readFromFile("/api-keys.properties");
        KrakenAPI api = new KrakenAPI(credentials);

        JsonNode balance = api.query(KrakenAPI.Private.BALANCE);
        log.info("{}", balance);

        JsonNode order = api.query(KrakenAPI.Private.ADD_ORDER, Map.of(
                "ordertype", "limit",
                "type", "sell",
                "volume", "1",
                "pair", "XLTCZUSD",
                "price", "1000",
                "oflags", "post,fciq",
                "validate", "true"));
        log.info("{}", order);
    }
}
