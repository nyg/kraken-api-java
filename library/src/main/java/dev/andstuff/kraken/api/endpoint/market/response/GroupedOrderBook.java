package dev.andstuff.kraken.api.endpoint.market.response;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The {@code GroupedBook} response, with asks rounded up and bids down to grouped price levels.
 *
 * @param pair asset pair
 * @param grouping ticks per price level used by Kraken
 * @param bids aggregated bid levels
 * @param asks aggregated ask levels
 */
public record GroupedOrderBook(String pair, int grouping, List<Level> bids, List<Level> asks) {

    /**
     * A grouped price level returned by the {@code GroupedBook} endpoint.
     *
     * @param price grouped price
     * @param quantity aggregated quantity at this price
     */
    public record Level(BigDecimal price, @JsonProperty("qty") BigDecimal quantity) {}
}
