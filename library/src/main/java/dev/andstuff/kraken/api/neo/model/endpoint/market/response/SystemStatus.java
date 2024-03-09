package dev.andstuff.kraken.api.neo.model.endpoint.market.response;

import java.time.Instant;

public record SystemStatus(String status, // TODO enum?
                           Instant timestamp) {
}
