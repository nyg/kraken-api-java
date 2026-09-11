package dev.andstuff.kraken.api.endpoint.funding.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * The {@code DepositMethods} limit, which Kraken returns as an amount or {@code false} for no limit.
 *
 * @param amount the maximum net amount, or null when unlimited
 * @param unlimited whether Kraken returned the unlimited marker
 */
@JsonDeserialize(using = DepositLimitDeserializer.class)
public record DepositLimit(BigDecimal amount, boolean unlimited) {

    /**
     * Creates either a finite limit or an unlimited marker.
     *
     * @param amount the maximum net amount, present only for a finite limit
     * @param unlimited whether the limit is unlimited
     * @throws IllegalArgumentException if amount and unlimited contradict each other
     */
    public DepositLimit {
        if (unlimited == (amount != null)) {
            throw new IllegalArgumentException("Finite limits require an amount; unlimited limits must not have one");
        }
    }
}
