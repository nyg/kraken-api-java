package dev.andstuff.kraken.api.endpoint.fundingbeta;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingNetworksParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingNetworks;

/**
 * The Funding (Beta) List Funding Networks endpoint ({@code GET /funding/v1/networks}), listing the networks and the network groups sharing an address format.
 */
public class FundingNetworksEndpoint extends FundingBetaEndpoint<FundingNetworks> {

    /**
     * Creates the List Funding Networks endpoint using Kraken's default options.
     */
    public FundingNetworksEndpoint() {
        this(FundingNetworksParams.builder().build());
    }

    /**
     * Creates the List Funding Networks endpoint.
     *
     * @param params the request parameters
     */
    public FundingNetworksEndpoint(FundingNetworksParams params) {
        super("GET", "v1/networks", params, new TypeReference<>() {});
    }
}
