package dev.andstuff.kraken.api.endpoint.account.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The asset class values accepted by {@code TradesHistory}.
 */
@Getter
@RequiredArgsConstructor
public enum AssetClass {
    FOREX("forex"),
    EQUITY_PAIR("equity_pair"),
    FUTURES_CONTRACT("futures_contract"),
    SYNTHETIC_PAIR("synthetic_pair"),
    EXTERNAL_PAIR("external_pair"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
