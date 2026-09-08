package dev.andstuff.kraken.api.endpoint.market;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.params.OrderBookParams;
import dev.andstuff.kraken.api.endpoint.market.response.OrderBook;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

/**
 * The public {@code Depth} endpoint, returning aggregated L2 price levels by asset pair.
 */
public class OrderBookEndpoint extends PublicEndpoint<Map<String, OrderBook>> {

    /**
     * Creates the {@code Depth} endpoint using Kraken's default options.
     *
     * @param pair the asset pair to query
     */
    public OrderBookEndpoint(String pair) {
        this(OrderBookParams.builder().pair(pair).build());
    }

    /**
     * Creates the {@code Depth} endpoint.
     *
     * @param params the request parameters
     */
    public OrderBookEndpoint(OrderBookParams params) {
        super("Depth", params, new TypeReference<>() {});
    }
}
