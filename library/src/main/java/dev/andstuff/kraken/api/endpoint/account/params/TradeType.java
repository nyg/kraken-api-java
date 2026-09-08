package dev.andstuff.kraken.api.endpoint.account.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The trade type values accepted by {@code TradesHistory}.
 */
public enum TradeType {
    @JsonProperty("all") ALL("all"),
    @JsonProperty("any position") ANY_POSITION("any position"),
    @JsonProperty("closed position") CLOSED_POSITION("closed position"),
    @JsonProperty("closing position") CLOSING_POSITION("closing position"),
    @JsonProperty("no position") NO_POSITION("no position"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    private final String value;

    TradeType(String value) {
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
