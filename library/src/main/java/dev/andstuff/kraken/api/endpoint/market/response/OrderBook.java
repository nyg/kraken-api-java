package dev.andstuff.kraken.api.endpoint.market.response;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

public record OrderBook(List<Level> asks, List<Level> bids) {

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({"price", "volume", "time"})
    public record Level(BigDecimal price, BigDecimal volume, long time) {}
}
