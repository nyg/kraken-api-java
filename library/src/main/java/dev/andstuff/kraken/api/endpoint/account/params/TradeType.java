package dev.andstuff.kraken.api.endpoint.account.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The trade type values accepted by {@code TradesHistory}.
 */
@Getter
@RequiredArgsConstructor
public enum TradeType {
    ALL("all"),
    ANY_POSITION("any position"),
    CLOSED_POSITION("closed position"),
    CLOSING_POSITION("closing position"),
    NO_POSITION("no position"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
