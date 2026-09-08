package dev.andstuff.kraken.api.endpoint.funding.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The funding reference returned by {@code Withdraw and WalletTransfer}.
 *
 * @param referenceId the reference id
 */
public record FundingReference(@JsonProperty("refid") String referenceId) {}
