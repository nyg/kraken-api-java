package dev.andstuff.kraken.api.endpoint.market.response;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = RecentSpreads.ResponseBuilder.class)
public record RecentSpreads(Map<String, List<RecentSpreads.Spread>> spreads, Long last) {

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({"time", "bid", "ask"})
    public record Spread(long time, BigDecimal bid, BigDecimal ask) {}

    @JsonPOJOBuilder(withPrefix = "")
    static class ResponseBuilder {

        private final Map<String, List<Spread>> entries = new LinkedHashMap<>();
        private Long last;

        @JsonProperty("last")
        void last(Long value) {
            last = value;
        }

        @JsonAnySetter
        void pair(String name, List<Spread> values) {
            entries.put(name, values);
        }

        RecentSpreads build() {
            return new RecentSpreads(entries, last);
        }
    }
}
