package dev.andstuff.kraken.api.endpoint.trading.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The order direction values accepted by {@code AddOrder} and {@code AddOrderBatch}.
 */
@Getter
@RequiredArgsConstructor
public enum OrderSide {
    BUY("buy"),
    SELL("sell"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
