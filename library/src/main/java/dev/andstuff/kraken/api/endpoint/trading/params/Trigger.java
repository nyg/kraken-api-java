package dev.andstuff.kraken.api.endpoint.trading.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The price signal values accepted by {@code AddOrder} and {@code AddOrderBatch} for triggered orders.
 */
@Getter
@RequiredArgsConstructor
public enum Trigger {
    INDEX("index"),
    LAST("last"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
