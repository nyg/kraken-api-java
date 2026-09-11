package dev.andstuff.kraken.api.endpoint.priv;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The rebase multiplier values accepted by private endpoints.
 */
@Getter
@RequiredArgsConstructor
public enum RebaseMultiplier {
    REBASED("rebased"),
    BASE("base"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
