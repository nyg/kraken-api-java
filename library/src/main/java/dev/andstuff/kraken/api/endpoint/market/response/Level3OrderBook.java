package dev.andstuff.kraken.api.endpoint.market.response;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The {@code Level3} response, containing individual orders in the book.
 *
 * @param pair asset pair
 * @param bids bid orders in the order returned by Kraken
 * @param asks ask orders in the order returned by Kraken
 */
public record Level3OrderBook(String pair, List<Order> bids, List<Order> asks) {

    /**
     * An individual order returned by the {@code Level3} endpoint.
     *
     * @param price order price
     * @param quantity order quantity
     * @param orderId order identifier
     * @param timestamp Unix timestamp in nanoseconds
     */
    public record Order(BigDecimal price, @JsonProperty("qty") BigDecimal quantity,
                        @JsonProperty("order_id") String orderId, long timestamp) {}
}
