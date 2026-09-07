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

@JsonDeserialize(builder = OhlcData.ResponseBuilder.class)
public record OhlcData(Map<String, List<OhlcData.Candle>> candles, Long last) {

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({"time", "open", "high", "low", "close", "vwap", "volume", "count"})
    public record Candle(long time, BigDecimal open, BigDecimal high, BigDecimal low,
                         BigDecimal close, BigDecimal vwap, BigDecimal volume, long count) {}

    @JsonPOJOBuilder(withPrefix = "")
    static class ResponseBuilder {

        private final Map<String, List<Candle>> entries = new LinkedHashMap<>();
        private Long last;

        @JsonProperty("last")
        void last(Long value) {
            last = value;
        }

        @JsonAnySetter
        void pair(String name, List<Candle> values) {
            entries.put(name, values);
        }

        OhlcData build() {
            return new OhlcData(entries, last);
        }
    }
}
