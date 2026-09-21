package dev.andstuff.kraken.api.endpoint.trading.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The amend transaction returned by {@code AmendOrder}.
 *
 * @param amendId the unique Kraken identifier of the amend transaction
 */
public record OrderAmended(@JsonProperty("amend_id") String amendId) {}
