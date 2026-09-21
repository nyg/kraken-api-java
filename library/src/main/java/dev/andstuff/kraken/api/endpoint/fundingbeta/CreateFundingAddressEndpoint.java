package dev.andstuff.kraken.api.endpoint.fundingbeta;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.CreateFundingAddressParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingAddressCreated;

/**
 * The Funding (Beta) Create Funding Address endpoint ({@code POST /funding/v1/addresses}), saving a crypto withdrawal address.
 */
public class CreateFundingAddressEndpoint extends FundingBetaEndpoint<FundingAddressCreated> {

    /**
     * Creates the Create Funding Address endpoint.
     *
     * @param params the request parameters
     */
    public CreateFundingAddressEndpoint(CreateFundingAddressParams params) {
        super("POST", "v1/addresses", params, new TypeReference<>() {});
    }
}
