package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The withdrawal address saved by Create Funding Address.
 *
 * @param addressId the identifier of the created address, used to withdraw to it
 * @param verified whether the address is verified and ready for use
 */
public record FundingAddressCreated(@JsonProperty("address_id") String addressId,
                                    boolean verified) {}
