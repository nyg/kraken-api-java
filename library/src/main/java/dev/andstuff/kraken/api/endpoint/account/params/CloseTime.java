package dev.andstuff.kraken.api.endpoint.account.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The close time values accepted by {@code ClosedOrders}.
 */
@Getter
@RequiredArgsConstructor
public enum CloseTime {
    OPEN("open"),
    CLOSE("close"),
    BOTH("both"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
