package dev.andstuff.kraken.api.endpoint.algotrading.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The strategy returned by {@code CancelStrategy}.
 *
 * @param strategyId the identifier of the cancelled strategy
 */
public record StrategyCancelled(@JsonProperty("strategy_id") String strategyId) {}
