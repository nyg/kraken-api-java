package dev.andstuff.kraken.api.endpoint.algotrading.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The strategy returned by {@code AddStrategy}.
 *
 * @param strategyId the identifier of the new strategy, empty when the strategy was only validated
 */
public record StrategyAdded(@JsonProperty("strategy_id") String strategyId) {}
