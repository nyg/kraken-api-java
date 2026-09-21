package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Scope;

/**
 * A page of saved withdrawal addresses returned by List Funding Addresses.
 *
 * @param addresses the withdrawal addresses of the page
 * @param nextCursor the cursor of the next page, {@code null} on the last one
 */
public record FundingAddresses(List<Address> addresses,
                               @JsonProperty("next_cursor") String nextCursor) {

    /**
     * A saved withdrawal address.
     *
     * @param addressId the identifier of the address, used to withdraw to it
     * @param scope the method, network or network group the address was saved for
     * @param addressDetails the details of the address
     * @param name the unique name of the address
     * @param description the description of the address
     * @param modifier {@link Modifier#THIRD_PARTY} if the address belongs to a third party, {@code null} otherwise
     * @param verified whether the address is verified and ready for use
     */
    public record Address(@JsonProperty("address_id") String addressId,
                          Scope scope,
                          @JsonProperty("address_details") AddressDetails addressDetails,
                          String name,
                          String description,
                          Modifier modifier,
                          boolean verified) {}

    /**
     * A qualifier of a withdrawal address.
     */
    public enum Modifier {
        THIRD_PARTY,

        @JsonEnumDefaultValue
        UNKNOWN
    }
}
