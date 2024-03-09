package dev.andstuff.kraken.example;

import static dev.andstuff.kraken.example.ExampleHelper.readPropertiesFromFile;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;

import dev.andstuff.kraken.api.KrakenApi;
import dev.andstuff.kraken.api.neo.KrakenAPI;
import dev.andstuff.kraken.api.neo.model.endpoint.market.response.AssetInfo;
import dev.andstuff.kraken.api.neo.model.endpoint.market.response.AssetPair;
import dev.andstuff.kraken.api.neo.model.endpoint.market.response.ServerTime;
import dev.andstuff.kraken.api.neo.model.endpoint.market.response.SystemStatus;

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

        JsonNode ticker = publicAPI.fetchPublic(KrakenApi.Method.TICKER.name, Map.of("pair", "XBTEUR"));
        System.out.println(ticker);

        /* Private endpoint example */

        Properties apiKeys = readPropertiesFromFile("/api-keys.properties");

        KrakenAPI api = new KrakenAPI(apiKeys.getProperty("key"), apiKeys.getProperty("secret"));

        //
        //        JsonNode response;
        //        Map<String, String> input = new HashMap<>();
        //
        //        input.put("pair", "XBTEUR");
        //        response = api.queryPublic(KrakenApi.Method.TICKER, input);
        //        System.out.println(response);
        //
        //        input.clear();
        //        input.put("pair", "XBTUSD,XLTCZUSD");
        //        response = api.queryPublic(KrakenApi.Method.ASSET_PAIRS, input);
        //        System.out.println(response);
        //
        //        input.clear();
        //        input.put("asset", "ZEUR");
        //        response = api.queryPrivate(KrakenApi.Method.BALANCE, input);
        //        System.out.println(response);
        //
        //        input.clear();
        //        input.put("ordertype", "limit");
        //        input.put("type", "sell");
        //        input.put("volume", "1");
        //        input.put("pair", "XLTCZUSD");
        //        input.put("price", "1000");
        //        input.put("oflags", "post,fciq");
        //        input.put("validate", "true");
        //        response = api.queryPrivate(KrakenApi.Method.ADD_ORDER, input);
        //        System.out.println(response);
    }
}
