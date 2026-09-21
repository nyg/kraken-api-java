package dev.andstuff.kraken.api.endpoint.trading.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The asset class values accepted by {@code AddOrder}, {@code AddOrderBatch} and {@code EditOrder}, required for non-crypto pairs such as xStocks.
 */
@Getter
@RequiredArgsConstructor
public enum AssetClass {
    TOKENIZED_ASSET("tokenized_asset"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
