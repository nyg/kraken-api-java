package dev.andstuff.kraken.api.endpoint.account.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The rebase multiplier values accepted by {@code Balance}.
 */
public enum RebaseMultiplier {
    @JsonProperty("rebased") REBASED("rebased"),
    @JsonProperty("base") BASE("base"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    private final String value;

    RebaseMultiplier(String value) {
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
