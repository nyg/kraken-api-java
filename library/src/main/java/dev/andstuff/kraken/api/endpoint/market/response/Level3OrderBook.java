package dev.andstuff.kraken.api.endpoint.market.response;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Level3OrderBook(String pair, List<Order> bids, List<Order> asks) {

    public record Order(BigDecimal price, @JsonProperty("qty") BigDecimal quantity,
                        @JsonProperty("order_id") String orderId, long timestamp) {}
}
