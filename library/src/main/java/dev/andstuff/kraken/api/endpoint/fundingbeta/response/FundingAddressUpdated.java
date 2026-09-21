package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

/**
 * The withdrawal address updated by Update Funding Address.
 *
 * @param verified whether the address is verified and ready for use
 */
public record FundingAddressUpdated(boolean verified) {}
