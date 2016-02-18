package edu.self.kraken;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import edu.self.kraken.api.KrakenApi;
import edu.self.kraken.api.KrakenApi.Method;

public class Examples {

    public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchAlgorithmException {

        KrakenApi api = new KrakenApi();
        api.setKey("Your API Key"); // FIXME
        api.setSecret("Your API Secret"); // FIXME

        String response;
        Map<String, String> input = new HashMap<>();

        input.put("pair", "XBTEUR");
        response = api.queryPublic(Method.TICKER, input);
        System.out.println(response);

        input.clear();
        input.put("pair", "XBTEUR,XBTLTC");
        response = api.queryPublic(Method.ASSET_PAIRS, input);
        System.out.println(response);

        input.clear();
        input.put("asset", "ZEUR");
        response = api.queryPrivate(Method.BALANCE, input);
        System.out.println(response);
    }
}
