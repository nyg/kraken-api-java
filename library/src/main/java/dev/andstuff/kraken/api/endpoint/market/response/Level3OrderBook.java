package dev.andstuff.kraken.api.endpoint.market.response;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
     * @param timestamp order instant, decoded from epoch nanoseconds
     */
    public record Order(BigDecimal price, @JsonProperty("qty") BigDecimal quantity,
                        @JsonProperty("order_id") String orderId, @JsonDeserialize(using = NanosecondsDeserializer.class) Instant timestamp) {}

    static class NanosecondsDeserializer extends JsonDeserializer<Instant> {

        @Override
        public Instant deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            return Instant.ofEpochSecond(0, parser.getLongValue());
        }
    }
}
