package dev.andstuff.kraken.api.endpoint.algotrading.response;

import java.util.Map;

/**
 * The strategies returned by {@code QueryStrategies}.
 *
 * @param strategies the strategies keyed by identifier, without the identifiers Kraken did not find
 */
public record StrategiesInfo(Map<String, AlgoStrategy> strategies) {}
