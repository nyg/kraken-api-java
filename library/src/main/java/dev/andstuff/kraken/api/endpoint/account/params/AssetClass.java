package dev.andstuff.kraken.api.endpoint.account.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The asset class values accepted by {@code TradesHistory}.
 */
public enum AssetClass {
    @JsonProperty("forex") FOREX("forex"),
    @JsonProperty("equity_pair") EQUITY_PAIR("equity_pair"),
    @JsonProperty("futures_contract") FUTURES_CONTRACT("futures_contract"),
    @JsonProperty("synthetic_pair") SYNTHETIC_PAIR("synthetic_pair"),
    @JsonProperty("external_pair") EXTERNAL_PAIR("external_pair"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    private final String value;

    AssetClass(String value) {
        this.value = value;
    }

    /**
     * Returns the value sent to Kraken.
 *
 * @return the API value
     */
    @JsonValue
    public String getValue() {
        return value;
    }
}
