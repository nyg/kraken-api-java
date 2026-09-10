package dev.andstuff.kraken.api.endpoint.account.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The consolidation values accepted by {@code OpenPositions}.
 */
@Getter
@RequiredArgsConstructor
public enum Consolidation {
    MARKET("market"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
