package dev.andstuff.kraken.api.model.endpoint.market.response;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public record SystemStatus(Description status,
                           Instant timestamp) {

    enum Description {
        ONLINE,
        MAINTENANCE,
        CANCEL_ONLY,
        POST_ONLY,

        @JsonEnumDefaultValue
        UNKNOWN
    }
}
