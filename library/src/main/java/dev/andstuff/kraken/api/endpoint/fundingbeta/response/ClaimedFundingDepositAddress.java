package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The deposit address returned by Claim Funding Deposit Address.
 *
 * @param addressDetails the details of the claimed address
 */
public record ClaimedFundingDepositAddress(@JsonProperty("address_details") AddressDetails addressDetails) {}
