package dev.andstuff.kraken.api.endpoint.funding.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The source wallet values accepted by {@code WalletTransfer}.
 */
@Getter
@RequiredArgsConstructor
public enum SourceWallet {
    SPOT_WALLET("Spot Wallet"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
