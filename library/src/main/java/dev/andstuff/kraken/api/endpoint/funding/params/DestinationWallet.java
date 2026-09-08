package dev.andstuff.kraken.api.endpoint.funding.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The destination wallet values accepted by {@code WalletTransfer}.
 */
public enum DestinationWallet {
    @JsonProperty("Futures Wallet") FUTURES_WALLET("Futures Wallet"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    private final String value;

    DestinationWallet(String value) {
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
