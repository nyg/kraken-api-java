package dev.andstuff.kraken.example;

import static dev.andstuff.kraken.example.PropertiesHelper.readFromFile;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.model.endpoint.market.response.AssetInfo;
import dev.andstuff.kraken.api.model.endpoint.market.response.AssetPair;
import dev.andstuff.kraken.api.model.endpoint.market.response.ServerTime;
import dev.andstuff.kraken.api.model.endpoint.market.response.SystemStatus;

public class Examples {

    public static void main(String[] args) {


        /* Public endpoint examples */

        KrakenAPI publicAPI = new KrakenAPI();

        ServerTime serverTime = publicAPI.serverTime();
        System.out.println(serverTime);

        SystemStatus systemStatus = publicAPI.systemStatus();
        System.out.println(systemStatus);

        Map<String, AssetInfo> assets1 = publicAPI.assetInfo(List.of("BTC", "ETH"));
        System.out.println(assets1);

        Map<String, AssetInfo> assets2 = publicAPI.assetInfo(List.of("DOT", "ADA"), "currency");
        System.out.println(assets2);

        Map<String, AssetPair> pairs1 = publicAPI.assetPairs(List.of("ETH/BTC", "ETH/USD"));
        System.out.println(pairs1);

        Map<String, AssetPair> pairs2 = publicAPI.assetPairs(List.of("DOT/USD", "ADA/USD"), AssetPair.Info.MARGIN);
        System.out.println(pairs2);

        JsonNode ticker = publicAPI.query(KrakenAPI.Public.TICKER, Map.of("pair", "XBTEUR"));
        System.out.println(ticker);

        /* Private endpoint example */

        Properties apiKeys = readFromFile("/api-keys.properties");

        KrakenAPI api = new KrakenAPI(apiKeys.getProperty("key"), apiKeys.getProperty("secret"));

        JsonNode balance = api.query(KrakenAPI.Private.BALANCE);
        System.out.println(balance);

        JsonNode order = api.query(KrakenAPI.Private.ADD_ORDER, Map.of(
                "ordertype", "limit",
                "type", "sell",
                "volume", "1",
                "pair", "XLTCZUSD",
                "price", "1000",
                "oflags", "post,fciq",
                "validate", "true"));
        System.out.println(order);
    }
}
