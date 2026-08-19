package dev.andstuff.kraken.api.endpoint.transparency.response;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

/**
 * How a quantity or a price is expressed in the transparency data.
 */
public enum Notation {
    UNIT,
    NOML,
    MONE,

    @JsonEnumDefaultValue
    UNKNOWN
}
