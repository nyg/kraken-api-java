package dev.andstuff.kraken.api.endpoint.algotrading.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The strategy types of the Algo Trading endpoints, {@code twap} being the only one Kraken supports.
 */
@Getter
@RequiredArgsConstructor
public enum StrategyType {
    TWAP("twap"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
