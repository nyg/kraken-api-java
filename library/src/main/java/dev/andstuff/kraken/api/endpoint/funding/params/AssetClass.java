package dev.andstuff.kraken.api.endpoint.funding.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The asset class values accepted by {@code DepositMethods}.
 */
public enum AssetClass {
    @JsonProperty("currency") CURRENCY("currency"),
    @JsonProperty("tokenized_asset") TOKENIZED_ASSET("tokenized_asset"),
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
