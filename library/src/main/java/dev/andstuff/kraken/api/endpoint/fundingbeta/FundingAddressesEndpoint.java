package dev.andstuff.kraken.api.endpoint.fundingbeta;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingAddressesParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingAddresses;

/**
 * The Funding (Beta) List Funding Addresses endpoint ({@code GET /funding/v1/addresses}), listing the saved withdrawal addresses.
 */
public class FundingAddressesEndpoint extends FundingBetaEndpoint<FundingAddresses> {

    /**
     * Creates the List Funding Addresses endpoint using Kraken's default options.
     */
    public FundingAddressesEndpoint() {
        this(FundingAddressesParams.builder().build());
    }

    /**
     * Creates the List Funding Addresses endpoint.
     *
     * @param params the request parameters
     */
    public FundingAddressesEndpoint(FundingAddressesParams params) {
        super("GET", "v1/addresses", params, new TypeReference<>() {});
    }
}
