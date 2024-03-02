package dev.andstuff.kraken.example;

import static dev.andstuff.kraken.example.ExampleHelper.readPropertiesFromFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;

import dev.andstuff.kraken.api.KrakenApi;

public class Examples {

    public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchAlgorithmException {

        Properties apiKeys = readPropertiesFromFile("/api-keys.properties");

        KrakenApi api = new KrakenApi();
        api.setKey(apiKeys.getProperty("key"));
        api.setSecret(apiKeys.getProperty("secret"));

        JsonNode response;
        Map<String, String> input = new HashMap<>();

        input.put("pair", "XBTEUR");
        response = api.queryPublic(KrakenApi.Method.TICKER, input);
        System.out.println(response);

        input.clear();
        input.put("pair", "XBTUSD,XLTCZUSD");
        response = api.queryPublic(KrakenApi.Method.ASSET_PAIRS, input);
        System.out.println(response);

        input.clear();
        input.put("asset", "ZEUR");
        response = api.queryPrivate(KrakenApi.Method.BALANCE, input);
        System.out.println(response);

        input.clear();
        input.put("ordertype", "limit");
        input.put("type", "sell");
        input.put("volume", "1");
        input.put("pair", "XLTCZUSD");
        input.put("price", "1000");
        input.put("oflags", "post,fciq");
        input.put("validate", "true");
        response = api.queryPrivate(KrakenApi.Method.ADD_ORDER, input);
        System.out.println(response);
    }
}
