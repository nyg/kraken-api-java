package dev.andstuff.kraken.api.endpoint.market.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The response of the {@code Time} endpoint.
 *
 * @param unixTime the server time, in seconds since the epoch
 * @param rfc1123 the server time, as an RFC 1123 formatted string
 */
public record ServerTime(@JsonProperty("unixtime") long unixTime,
                         String rfc1123) {
}
