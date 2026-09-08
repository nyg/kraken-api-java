package dev.andstuff.kraken.api.endpoint.market.response;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * The {@code Trades} response containing recent trades; by default Kraken returns the last 1000 trades.
 *
 * @param trades entries by Kraken's returned pair name (internal or display name)
 * @param last cursor to pass unchanged as {@code since} when polling for new data
 */
@JsonDeserialize(builder = RecentTrades.ResponseBuilder.class)
public record RecentTrades(Map<String, List<RecentTrades.Trade>> trades, String last) {

    /**
     * A positional trade returned by the {@code Trades} endpoint.
     *
     * @param price trade price
     * @param volume traded volume
     * @param time Unix timestamp in seconds, retaining fractional precision
     * @param side buy or sell
     * @param orderType market or limit
     * @param miscellaneous additional trade information
     * @param tradeId trade identifier
     */
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({"price", "volume", "time", "side", "orderType", "miscellaneous", "tradeId"})
    public record Trade(BigDecimal price, BigDecimal volume, BigDecimal time, Side side,
                        OrderType orderType, String miscellaneous, long tradeId) {}

    /**
     * The buy/sell code in a {@code Trades} row.
     */
    public enum Side {
        @JsonProperty("b") BUY,
        @JsonProperty("s") SELL,
        @JsonEnumDefaultValue UNKNOWN
    }

    /**
     * The market/limit code in a {@code Trades} row.
     */
    public enum OrderType {
        @JsonProperty("m") MARKET,
        @JsonProperty("l") LIMIT,
        @JsonEnumDefaultValue UNKNOWN
    }

    @JsonPOJOBuilder(withPrefix = "")
    static class ResponseBuilder {

        private final Map<String, List<Trade>> entries = new LinkedHashMap<>();
        private String last;

        @JsonProperty("last")
        void last(String value) {
            last = value;
        }

        @JsonAnySetter
        void pair(String name, List<Trade> values) {
            entries.put(name, values);
        }

        RecentTrades build() {
            return new RecentTrades(entries, last);
        }
    }
}
