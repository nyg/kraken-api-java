package dev.andstuff.kraken.api.endpoint.funding.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The destination wallet values accepted by {@code WalletTransfer}.
 */
@Getter
@RequiredArgsConstructor
public enum DestinationWallet {
    FUTURES_WALLET("Futures Wallet"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
