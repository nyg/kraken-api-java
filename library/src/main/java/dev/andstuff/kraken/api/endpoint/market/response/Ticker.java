package dev.andstuff.kraken.api.endpoint.market.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Ticker(@JsonProperty("a") Ask ask,
                     @JsonProperty("b") Bid bid,
                     @JsonProperty("c") LastTrade lastTrade,
                     @JsonProperty("v") Volume volume,
                     @JsonProperty("p") VWAP volumeWeightedAveragePrice,
                     @JsonProperty("t") TradeCount tradeCount,
                     @JsonProperty("l") Low low,
                     @JsonProperty("h") High high,
                     @JsonProperty("o") BigDecimal openingPrice) {

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record Ask(BigDecimal price, BigDecimal wholeLotVolume, BigDecimal lotVolume) {}

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record Bid(BigDecimal price, BigDecimal wholeLotVolume, BigDecimal lotVolume) {}

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record LastTrade(BigDecimal price, BigDecimal volume) {}

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record Volume(BigDecimal today, BigDecimal last24Hours) {}

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record VWAP(BigDecimal today, BigDecimal last24Hours) {}

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record TradeCount(int today, int last24Hours) {}

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record Low(BigDecimal today, BigDecimal last24Hours) {}

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record High(BigDecimal today, BigDecimal last24Hours) {}
}
