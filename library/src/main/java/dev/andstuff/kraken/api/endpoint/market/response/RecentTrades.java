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

@JsonDeserialize(builder = RecentTrades.ResponseBuilder.class)
public record RecentTrades(Map<String, List<RecentTrades.Trade>> trades, String last) {

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({"price", "volume", "time", "side", "orderType", "miscellaneous", "tradeId"})
    public record Trade(BigDecimal price, BigDecimal volume, BigDecimal time, Side side,
                        OrderType orderType, String miscellaneous, long tradeId) {}

    public enum Side {
        @JsonProperty("b") BUY,
        @JsonProperty("s") SELL,
        @JsonEnumDefaultValue UNKNOWN
    }

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
