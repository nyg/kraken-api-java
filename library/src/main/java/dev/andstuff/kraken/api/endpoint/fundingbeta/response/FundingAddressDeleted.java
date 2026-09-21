package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The response of Delete Funding Address.
 *
 * @param deleted whether the address was deleted
 */
public record FundingAddressDeleted(@JsonProperty("result") boolean deleted) {}
