package dev.andstuff.kraken.api.endpoint.algotrading.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The order types of the child orders of a strategy.
 */
@Getter
@RequiredArgsConstructor
public enum StrategyOrderType {
    MARKET("market"),
    LIMIT("limit"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
