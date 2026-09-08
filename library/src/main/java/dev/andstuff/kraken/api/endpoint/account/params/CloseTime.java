package dev.andstuff.kraken.api.endpoint.account.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The close time values accepted by {@code ClosedOrders}.
 */
public enum CloseTime {
    @JsonProperty("open") OPEN("open"),
    @JsonProperty("close") CLOSE("close"),
    @JsonProperty("both") BOTH("both"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    private final String value;

    CloseTime(String value) {
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
