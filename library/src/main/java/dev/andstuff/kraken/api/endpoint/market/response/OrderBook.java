package dev.andstuff.kraken.api.endpoint.market.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * A pair's aggregated L2 order book from the {@code Depth} endpoint.
 *
 * @param asks ask levels in the order returned by Kraken
 * @param bids bid levels in the order returned by Kraken
 */
public record OrderBook(List<Level> asks, List<Level> bids) {

    /**
     * An aggregated price level returned by the {@code Depth} endpoint.
     *
     * @param price price of this level
     * @param volume aggregated volume at this price
     * @param time timestamp as an instant
     */
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({"price", "volume", "time"})
    public record Level(BigDecimal price, BigDecimal volume, Instant time) {}
}
