package dev.andstuff.kraken.api.model.endpoint.market.response;

import java.time.Instant;

public record SystemStatus(String status, // TODO could be enum
                           Instant timestamp) {
}
