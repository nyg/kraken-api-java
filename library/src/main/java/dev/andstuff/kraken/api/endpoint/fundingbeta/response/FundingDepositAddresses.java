package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A page of claimed deposit addresses returned by List Funding Claimed Addresses.
 *
 * @param addresses the claimed deposit addresses of the page
 * @param nextCursor the cursor of the next page, {@code null} on the last one
 */
public record FundingDepositAddresses(List<Address> addresses,
                                      @JsonProperty("next_cursor") String nextCursor) {

    /**
     * A claimed deposit address.
     *
     * @param methodId the method of the address: the method filtered on when the filter is a method, the method the address is stored against otherwise
     * @param sharesAddressesWithMethodId the method the address is stored against, when it is shared with the method filtered on
     * @param id the identifier of the claimed address
     * @param lastUsed when the address last received a deposit, {@code null} if it never did
     * @param expireTime when the unused address expires, {@code null} if it doesn't
     * @param addressDetails the details of the address
     */
    public record Address(@JsonProperty("method_id") String methodId,
                          @JsonProperty("shares_addresses_with_method_id") String sharesAddressesWithMethodId,
                          String id,
                          @JsonProperty("last_used") Instant lastUsed,
                          @JsonProperty("expire_time") Instant expireTime,
                          @JsonProperty("address_details") AddressDetails addressDetails) {}
}
