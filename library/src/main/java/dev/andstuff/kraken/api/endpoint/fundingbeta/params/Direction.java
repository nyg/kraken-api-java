package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The funding direction selecting deposit or withdrawal methods and assets.
 */
@Getter
@RequiredArgsConstructor
public enum Direction {
    DEPOSIT("deposit"),
    WITHDRAW("withdraw"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
