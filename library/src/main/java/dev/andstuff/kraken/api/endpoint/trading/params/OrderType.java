package dev.andstuff.kraken.api.endpoint.trading.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The order type values accepted by {@code AddOrder} and {@code AddOrderBatch}, also used for conditional close orders.
 */
@Getter
@RequiredArgsConstructor
public enum OrderType {
    MARKET("market"),
    LIMIT("limit"),
    ICEBERG("iceberg"),
    STOP_LOSS("stop-loss"),
    TAKE_PROFIT("take-profit"),
    STOP_LOSS_LIMIT("stop-loss-limit"),
    TAKE_PROFIT_LIMIT("take-profit-limit"),
    TRAILING_STOP("trailing-stop"),
    TRAILING_STOP_LIMIT("trailing-stop-limit"),
    SETTLE_POSITION("settle-position"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
