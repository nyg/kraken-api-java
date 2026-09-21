package dev.andstuff.kraken.api.endpoint.fundingbeta;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.DeleteFundingAddressParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingAddressDeleted;

/**
 * The Funding (Beta) Delete Funding Address endpoint ({@code DELETE /funding/v1/addresses/{id}}), deleting a saved withdrawal address.
 */
public class DeleteFundingAddressEndpoint extends FundingBetaEndpoint<FundingAddressDeleted> {

    /**
     * Creates the Delete Funding Address endpoint using the required parameters.
     *
     * @param addressId the identifier of the address to delete
     */
    public DeleteFundingAddressEndpoint(String addressId) {
        this(DeleteFundingAddressParams.builder().addressId(addressId).build());
    }

    /**
     * Creates the Delete Funding Address endpoint.
     *
     * @param params the request parameters
     */
    public DeleteFundingAddressEndpoint(DeleteFundingAddressParams params) {
        super("DELETE", "v1/addresses/" + pathSegment(params.getAddressId()), params, new TypeReference<>() {});
    }
}
