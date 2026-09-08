package dev.andstuff.kraken.api.endpoint.account.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The consolidation values accepted by {@code OpenPositions}.
 */
public enum Consolidation {
    @JsonProperty("market") MARKET("market"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    private final String value;

    Consolidation(String value) {
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
