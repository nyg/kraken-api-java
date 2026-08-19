package dev.andstuff.kraken.api.endpoint.transparency.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The response of the {@code PreTrade} endpoint, i.e. the aggregated order book of a single currency pair, with at most ten price levels on each side.
 *
 * @param symbol the symbol of the currency pair, e.g. {@code BTC/USD}
 * @param description the full description of the currency pair, e.g. {@code Bitcoin / US Dollar}
 * @param baseAsset the currency code of the base asset
 * @param baseNotation how the quantity is expressed
 * @param baseDtiCode the Digital Token Identifier code of the base asset, empty if it has none
 * @param baseDtiShortName the Digital Token Identifier short name of the base asset, empty if it has none
 * @param quoteAsset the currency the price is expressed in
 * @param quoteNotation how the price is expressed
 * @param quoteDtiCode the Digital Token Identifier code of the quote asset, empty if it has none
 * @param quoteDtiShortName the Digital Token Identifier short name of the quote asset, empty if it has none
 * @param venue the Market Identifier Code of the trading platform the orders were submitted on
 * @param system the type of order system the price levels come from
 * @param bids the bid price levels, in decreasing price order
 * @param asks the ask price levels, in increasing price order
 */
public record PreTrade(String symbol,
                       String description,
                       @JsonProperty("base_asset") String baseAsset,
                       @JsonProperty("base_notation") Notation baseNotation,
                       @JsonProperty("base_dti_code") String baseDtiCode,
                       @JsonProperty("base_dti_short_name") String baseDtiShortName,
                       @JsonProperty("quote_asset") String quoteAsset,
                       @JsonProperty("quote_notation") Notation quoteNotation,
                       @JsonProperty("quote_dti_code") String quoteDtiCode,
                       @JsonProperty("quote_dti_short_name") String quoteDtiShortName,
                       String venue,
                       OrderSystem system,
                       List<PriceLevel> bids,
                       List<PriceLevel> asks) {

    /**
     * A single price level of the aggregated order book.
     *
     * @param side whether the price level is a bid or an ask
     * @param price the price of the level
     * @param quantity the aggregated quantity at the price level
     * @param count the number of orders in the price level
     * @param submissionTimestamp the time the order at this price level was submitted
     * @param publicationTimestamp the time the price level was last updated and published
     */
    public record PriceLevel(Side side,
                             BigDecimal price,
                             @JsonProperty("qty") BigDecimal quantity,
                             int count,
                             @JsonProperty("submission_ts") Instant submissionTimestamp,
                             @JsonProperty("publication_ts") Instant publicationTimestamp) {}

    /**
     * The side of a price level.
     */
    public enum Side {
        BUY,
        SELL,

        @JsonEnumDefaultValue
        UNKNOWN
    }

    /**
     * The type of order system the price levels come from.
     */
    public enum OrderSystem {
        CLOB,

        @JsonEnumDefaultValue
        UNKNOWN
    }
}
