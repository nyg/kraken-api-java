package dev.andstuff.kraken.api.endpoint.algotrading.response;

import java.util.Map;

/**
 * The pending and open strategies returned by {@code GetOpenStrategies}.
 *
 * @param open the strategies keyed by identifier, empty when there are none
 */
public record OpenStrategies(Map<String, AlgoStrategy> open) {}
