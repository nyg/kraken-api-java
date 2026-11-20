package dev.andstuff.kraken.api.endpoint.algotrading.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The asset classes of the pair of a strategy. {@code AddStrategy} only accepts {@code forex}, the standard spot pairs.
 */
@Getter
@RequiredArgsConstructor
public enum StrategyAssetClass {
    FOREX("forex"),
    TOKENIZED_ASSET("tokenized_asset"),
    SYNTHETIC_PAIR("synthetic_pair"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
