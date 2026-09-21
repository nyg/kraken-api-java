package dev.andstuff.kraken.api.endpoint.fundingbeta;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.UpdateFundingAddressParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingAddressUpdated;

/**
 * The Funding (Beta) Update Funding Address endpoint ({@code PUT /funding/v1/addresses/{id}}), renaming or describing a saved withdrawal address.
 */
public class UpdateFundingAddressEndpoint extends FundingBetaEndpoint<FundingAddressUpdated> {

    /**
     * Creates the Update Funding Address endpoint.
     *
     * @param params the request parameters
     */
    public UpdateFundingAddressEndpoint(UpdateFundingAddressParams params) {
        super("PUT", "v1/addresses/" + pathSegment(params.getAddressId()), params, new TypeReference<>() {});
    }
}
