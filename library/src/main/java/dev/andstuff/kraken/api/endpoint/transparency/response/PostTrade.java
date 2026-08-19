package dev.andstuff.kraken.api.endpoint.transparency.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The response of the {@code PostTrade} endpoint, i.e. a batch of executed trades.
 *
 * @param lastTimestamp the time of the latest trade of the batch, to be used as the next {@code fromTimestamp} to page through trades
 * @param count the number of trades returned
 * @param trades the trades, in ascending time order
 */
public record PostTrade(@JsonProperty("last_ts") Instant lastTimestamp,
                        int count,
                        List<Trade> trades) {

    /**
     * A single executed trade.
     *
     * @param tradeId the Kraken identifier of the trade
     * @param price the trade price, excluding fees and commissions
     * @param quantity the unconsolidated trade quantity from execution
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
     * @param tradeVenue the Market Identifier Code of the trading platform the trade was executed on
     * @param tradeTimestamp the time the trade was matched in the engine
     * @param publicationVenue the Market Identifier Code of the trading platform the trade was published on
     * @param publicationTimestamp the time the trade was published to the market data streams
     */
    public record Trade(@JsonProperty("trade_id") String tradeId,
                        BigDecimal price,
                        BigDecimal quantity,
                        String symbol,
                        String description,
                        @JsonProperty("base_asset") String baseAsset,
                        @JsonProperty("base_notation") Notation baseNotation,
                        @JsonProperty("base_dti_code") String baseDtiCode,
                        @JsonProperty("base_dti_short_name") String baseDtiShortName,
                        @JsonProperty("quote_asset") String quoteAsset,
                        @JsonProperty("quote_notation") Notation quoteNotation,
                        @JsonProperty("quote_dti_code") String quoteDtiCode,
                        @JsonProperty("quote_dti_short_name") String quoteDtiShortName,
                        @JsonProperty("trade_venue") String tradeVenue,
                        @JsonProperty("trade_ts") Instant tradeTimestamp,
                        @JsonProperty("publication_venue") String publicationVenue,
                        @JsonProperty("publication_ts") Instant publicationTimestamp) {}
}
