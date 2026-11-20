package dev.andstuff.kraken.api.endpoint.algotrading.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The statuses of a strategy: {@code pending} until its start time, {@code open} while child orders are placed, then {@code closed} when fully filled, {@code expired} when its end time was reached first, or {@code cancelled}.
 */
@Getter
@RequiredArgsConstructor
public enum StrategyStatus {
    PENDING("pending"),
    OPEN("open"),
    CLOSED("closed"),
    EXPIRED("expired"),
    CANCELLED("cancelled"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
