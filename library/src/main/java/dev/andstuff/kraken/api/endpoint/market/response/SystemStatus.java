package dev.andstuff.kraken.api.endpoint.market.response;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

/**
 * The response of the {@code SystemStatus} endpoint.
 *
 * @param status the current status of the Kraken trading system
 * @param timestamp the time the status was last updated
 */
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
