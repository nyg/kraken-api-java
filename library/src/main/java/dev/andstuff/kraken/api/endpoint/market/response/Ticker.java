package dev.andstuff.kraken.api.endpoint.market.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The ticker of a single asset pair, as returned by the {@code Ticker} endpoint.
 *
 * @param ask the best ask
 * @param bid the best bid
 * @param lastTrade the last trade closed
 * @param volume the traded volume
 * @param volumeWeightedAveragePrice the volume weighted average price
 * @param tradeCount the number of trades
 * @param low the lowest price
 * @param high the highest price
 * @param openingPrice the opening price of the day
 */
public record Ticker(@JsonProperty("a") Ask ask,
                     @JsonProperty("b") Bid bid,
                     @JsonProperty("c") LastTrade lastTrade,
                     @JsonProperty("v") Volume volume,
                     @JsonProperty("p") VWAP volumeWeightedAveragePrice,
                     @JsonProperty("t") TradeCount tradeCount,
                     @JsonProperty("l") Low low,
                     @JsonProperty("h") High high,
                     @JsonProperty("o") BigDecimal openingPrice) {

    /**
     * The best ask.
     *
     * @param price the ask price
     * @param wholeLotVolume the whole lot volume
     * @param lotVolume the lot volume
     */
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record Ask(BigDecimal price, BigDecimal wholeLotVolume, BigDecimal lotVolume) {}

    /**
     * The best bid.
     *
     * @param price the bid price
     * @param wholeLotVolume the whole lot volume
     * @param lotVolume the lot volume
     */
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record Bid(BigDecimal price, BigDecimal wholeLotVolume, BigDecimal lotVolume) {}

    /**
     * The last trade closed.
     *
     * @param price the price of the trade
     * @param volume the volume of the trade
     */
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record LastTrade(BigDecimal price, BigDecimal volume) {}

    /**
     * The traded volume.
     *
     * @param today the volume traded today
     * @param last24Hours the volume traded during the last 24 hours
     */
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record Volume(BigDecimal today, BigDecimal last24Hours) {}

    /**
     * The volume weighted average price.
     *
     * @param today the price of today
     * @param last24Hours the price of the last 24 hours
     */
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record VWAP(BigDecimal today, BigDecimal last24Hours) {}

    /**
     * The number of trades.
     *
     * @param today the number of trades today
     * @param last24Hours the number of trades during the last 24 hours
     */
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record TradeCount(int today, int last24Hours) {}

    /**
     * The lowest price.
     *
     * @param today the lowest price of today
     * @param last24Hours the lowest price of the last 24 hours
     */
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record Low(BigDecimal today, BigDecimal last24Hours) {}

    /**
     * The highest price.
     *
     * @param today the highest price of today
     * @param last24Hours the highest price of the last 24 hours
     */
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record High(BigDecimal today, BigDecimal last24Hours) {}
}
