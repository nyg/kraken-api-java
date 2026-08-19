package dev.andstuff.kraken.api.endpoint.market.response;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single tradable asset pair, as returned by the {@code AssetPairs} endpoint.
 *
 * @param alternateName the alternate name of the pair, e.g. {@code ETHXBT}
 * @param webSocketName the name of the pair used by Kraken's WebSocket API, e.g. {@code ETH/XBT}
 * @param baseAssetClass the class of the base asset
 * @param baseAsset the base asset of the pair
 * @param quoteAssetClass the class of the quote asset
 * @param quoteAsset the quote asset of the pair
 * @param pairDecimals the number of decimal places of the pair price
 * @param costDecimals the number of decimal places of the order cost
 * @param lotDecimals the number of decimal places of the order volume
 * @param lotMultiplier the amount to multiply the volume by to get the currency volume
 * @param leverageBuy the leverage amounts available when buying
 * @param leverageSell the leverage amounts available when selling
 * @param takerFees the taker fee schedule, by volume tier
 * @param makerFees the maker fee schedule, by volume tier, {@code null} if the pair has no maker/taker distinction
 * @param volumeCurrencyFee the asset the fee volume tiers are expressed in
 * @param marginCallLevel the margin level at which a margin call occurs
 * @param marginStopLevel the margin level at which positions are liquidated
 * @param minimumOrderSize the minimum volume of an order
 * @param minimumOrderCost the minimum cost of an order
 * @param tickSize the minimum price increment
 * @param status the trading status of the pair
 * @param maxLongPositionSize the maximum size of a long position, {@code 0} if unlimited
 * @param maxShortPositionSize the maximum size of a short position, {@code 0} if unlimited
 */
public record AssetPair(@JsonProperty("altname") String alternateName,
                        @JsonProperty("wsname") String webSocketName,
                        @JsonProperty("aclass_base") String baseAssetClass,
                        @JsonProperty("base") String baseAsset,
                        @JsonProperty("aclass_quote") String quoteAssetClass,
                        @JsonProperty("quote") String quoteAsset,
                        @JsonProperty("pair_decimals") int pairDecimals,
                        @JsonProperty("cost_decimals") int costDecimals,
                        @JsonProperty("lot_decimals") int lotDecimals,
                        @JsonProperty("lot_multiplier") int lotMultiplier,
                        @JsonProperty("leverage_buy") List<Integer> leverageBuy,
                        @JsonProperty("leverage_sell") List<Integer> leverageSell,
                        @JsonProperty("fees") List<FeeSchedule> takerFees,
                        @JsonProperty("fees_maker") List<FeeSchedule> makerFees,
                        @JsonProperty("fee_volume_currency") String volumeCurrencyFee,
                        @JsonProperty("margin_call") int marginCallLevel,
                        @JsonProperty("margin_stop") int marginStopLevel,
                        @JsonProperty("ordermin") BigDecimal minimumOrderSize,
                        @JsonProperty("costmin") BigDecimal minimumOrderCost,
                        @JsonProperty("tick_size") BigDecimal tickSize,
                        @JsonProperty("status") Status status,
                        @JsonProperty("long_position_limit") long maxLongPositionSize,
                        @JsonProperty("short_position_limit") long maxShortPositionSize) {

    /**
     * A single tier of a fee schedule.
     *
     * @param volume the 30 day volume from which the tier applies
     * @param percentage the fee of the tier, in percent
     */
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record FeeSchedule(BigDecimal volume, BigDecimal percentage) {}

    /**
     * The trading status of an asset pair.
     */
    public enum Status {
        ONLINE,
        CANCEL_ONLY,
        POST_ONLY,
        LIMIT_ONLY,
        REDUCE_ONLY,

        @JsonEnumDefaultValue
        UNKNOWN
    }
}
