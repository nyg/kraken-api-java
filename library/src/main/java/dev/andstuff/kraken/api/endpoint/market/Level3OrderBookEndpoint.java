package dev.andstuff.kraken.api.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.params.Level3OrderBookParams;
import dev.andstuff.kraken.api.endpoint.market.response.Level3OrderBook;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

public class Level3OrderBookEndpoint extends PrivateEndpoint<Level3OrderBook> {

    public Level3OrderBookEndpoint(String pair) {
        this(Level3OrderBookParams.builder().pair(pair).build());
    }

    public Level3OrderBookEndpoint(Level3OrderBookParams params) {
        super("Level3", params, new TypeReference<>() {});
    }
}
