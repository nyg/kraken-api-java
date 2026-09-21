package dev.andstuff.kraken.api.endpoint.trading.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The time in force values accepted by {@code AddOrder} and {@code AddOrderBatch}; {@code AddOrderBatch} does not accept {@link #FOK}.
 */
@Getter
@RequiredArgsConstructor
public enum TimeInForce {
    GTC("GTC"),
    IOC("IOC"),
    GTD("GTD"),
    FOK("FOK"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
