package dev.andstuff.kraken.api.endpoint.trading.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The order flags accepted by {@code AddOrder}, {@code AddOrderBatch} and {@code EditOrder}.
 */
@Getter
@RequiredArgsConstructor
public enum OrderFlag {
    /**
     * Post-only order, available for limit orders.
     */
    POST("post"),
    /**
     * Prefer the fee in the base currency, the default when selling.
     */
    FCIB("fcib"),
    /**
     * Prefer the fee in the quote currency, the default when buying.
     */
    FCIQ("fciq"),
    /**
     * Order volume expressed in the quote currency, available for buy market orders without margin.
     */
    VIQC("viqc"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
