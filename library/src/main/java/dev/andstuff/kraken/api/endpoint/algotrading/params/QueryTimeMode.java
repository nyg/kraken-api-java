package dev.andstuff.kraken.api.endpoint.algotrading.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The timestamps the {@code start} and {@code end} bounds of {@code GetClosedStrategies} apply to: the opening time, the closing time, or the opening time for {@code start} and the closing time for {@code end}.
 */
@Getter
@RequiredArgsConstructor
public enum QueryTimeMode {
    OPEN("open"),
    CLOSE("close"),
    BOTH("both"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
