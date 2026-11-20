package dev.andstuff.kraken.api.endpoint.algotrading.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyAssetClass;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyOrderType;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyStatus;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyType;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderSide;

/**
 * A strategy returned by {@code GetOpenStrategies}, {@code QueryStrategies} and {@code GetClosedStrategies}.
 *
 * @param strategyId the identifier of the strategy, starting with {@code AS}
 * @param status the status of the strategy
 * @param reason an explanation of the status, e.g. {@code insufficientFunds}, {@code remainingBelowMinimum} or {@code userRequested}, {@code null} when Kraken gives none
 * @param details the parameters and execution progress of the strategy
 */
public record AlgoStrategy(@JsonProperty("strategy_id") String strategyId,
                           StrategyStatus status,
                           String reason,
                           Details details) {

    /**
     * The parameters and execution progress of a TWAP strategy.
     *
     * @param strategyType the strategy type
     * @param side the direction of the strategy
     * @param volume the total quantity of the strategy, in the base asset
     * @param filledVolume the quantity executed so far, in the base asset
     * @param averagePrice the average execution price, {@code null} until the first fill
     * @param totalCost the total cost of the executed quantity, in the quote asset
     * @param totalFee the fees charged across fills, in the quote asset
     * @param startTime the start time of the strategy
     * @param expireTime the end time of the strategy
     * @param symbol the asset pair, e.g. {@code BTC/USD}
     * @param assetClass the asset class of the pair
     * @param orderType the order type of the child orders
     * @param price the limit price of the child orders, {@code null} for market child orders
     */
    public record Details(@JsonProperty("strategy_type") StrategyType strategyType,
                          OrderSide side,
                          BigDecimal volume,
                          @JsonProperty("filled_volume") BigDecimal filledVolume,
                          @JsonProperty("avg_price") BigDecimal averagePrice,
                          @JsonProperty("total_cost") BigDecimal totalCost,
                          @JsonProperty("total_fee") BigDecimal totalFee,
                          @JsonProperty("starttm") Instant startTime,
                          @JsonProperty("expiretm") Instant expireTime,
                          String symbol,
                          @JsonProperty("asset_class") StrategyAssetClass assetClass,
                          @JsonProperty("ordertype") StrategyOrderType orderType,
                          BigDecimal price) {}
}
