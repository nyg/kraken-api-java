package dev.andstuff.kraken.api.endpoint.earn.response;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

/**
 * The class of an asset an earn strategy accepts, or an allocation is made in.
 */
public enum AssetClass {
    CURRENCY,
    TOKENIZED_ASSET,

    @JsonEnumDefaultValue
    UNKNOWN
}
