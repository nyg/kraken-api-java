package dev.andstuff.kraken.api.endpoint.market;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.params.OrderBookParams;
import dev.andstuff.kraken.api.endpoint.market.response.OrderBook;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

public class OrderBookEndpoint extends PublicEndpoint<Map<String, OrderBook>> {

    public OrderBookEndpoint(String pair) {
        this(OrderBookParams.builder().pair(pair).build());
    }

    public OrderBookEndpoint(OrderBookParams params) {
        super("Depth", params, new TypeReference<>() {});
    }
}
