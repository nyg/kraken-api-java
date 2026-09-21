package dev.andstuff.kraken.api.endpoint.trading.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The self trade prevention modes accepted by {@code AddOrder} and {@code AddOrderBatch}, telling which order expires when an order would match another order of the same account.
 */
@Getter
@RequiredArgsConstructor
public enum SelfTradePrevention {
    CANCEL_NEWEST("cancel-newest"),
    CANCEL_OLDEST("cancel-oldest"),
    CANCEL_BOTH("cancel-both"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
