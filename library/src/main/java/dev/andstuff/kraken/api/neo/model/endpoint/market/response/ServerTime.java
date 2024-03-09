package dev.andstuff.kraken.api.neo.model.endpoint.market.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ServerTime(@JsonProperty("unixtime") long unixTime,
                         String rfc1123) {
}
