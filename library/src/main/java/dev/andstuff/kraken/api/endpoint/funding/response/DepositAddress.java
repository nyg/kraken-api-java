package dev.andstuff.kraken.api.endpoint.funding.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The deposit address returned by {@code DepositAddresses}.
 *
 * @param address the address
 * @param expireTime the expiration time as Unix seconds, or "0" for a non-expiring address
 * @param unused whether the address has never been used; null when omitted
 * @param tag the destination tag
 * @param memo the destination memo
 */
public record DepositAddress(String address,
        @JsonProperty("expiretm") String expireTime,
        @JsonProperty("new") Boolean unused,
        String tag,
        String memo) {}
