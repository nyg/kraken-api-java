package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The asset classes of the Funding (Beta) API.
 */
@Getter
@RequiredArgsConstructor
public enum AssetClass {
    CURRENCY("currency"),
    TOKENIZED_ASSET("tokenized_asset"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
