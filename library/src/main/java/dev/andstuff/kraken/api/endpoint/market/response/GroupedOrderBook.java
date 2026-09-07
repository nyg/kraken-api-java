package dev.andstuff.kraken.api.endpoint.market.response;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GroupedOrderBook(String pair, int grouping, List<Level> bids, List<Level> asks) {

    public record Level(BigDecimal price, @JsonProperty("qty") BigDecimal quantity) {}
}
