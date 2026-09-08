package dev.andstuff.kraken.api.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.params.Level3OrderBookParams;
import dev.andstuff.kraken.api.endpoint.market.response.Level3OrderBook;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code Level3} endpoint, requiring the Orders and trades - Query open orders &amp; trades API key permission.
 */
public class Level3OrderBookEndpoint extends PrivateEndpoint<Level3OrderBook> {

    /**
     * Creates the {@code Level3} endpoint using Kraken's default options.
     *
     * @param pair the asset pair to query
     */
    public Level3OrderBookEndpoint(String pair) {
        this(Level3OrderBookParams.builder().pair(pair).build());
    }

    /**
     * Creates the {@code Level3} endpoint.
     *
     * @param params the request parameters
     */
    public Level3OrderBookEndpoint(Level3OrderBookParams params) {
        super("Level3", params, new TypeReference<>() {});
    }
}
