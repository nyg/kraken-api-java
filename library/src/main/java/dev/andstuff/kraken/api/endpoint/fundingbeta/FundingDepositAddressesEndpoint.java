package dev.andstuff.kraken.api.endpoint.fundingbeta;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingDepositAddressesParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingDepositAddresses;

/**
 * The Funding (Beta) List Funding Claimed Addresses endpoint ({@code GET /funding/v2/deposit/addresses}), listing the claimed deposit addresses.
 */
public class FundingDepositAddressesEndpoint extends FundingBetaEndpoint<FundingDepositAddresses> {

    /**
     * Creates the List Funding Claimed Addresses endpoint using Kraken's default options.
     */
    public FundingDepositAddressesEndpoint() {
        this(FundingDepositAddressesParams.builder().build());
    }

    /**
     * Creates the List Funding Claimed Addresses endpoint.
     *
     * @param params the request parameters
     */
    public FundingDepositAddressesEndpoint(FundingDepositAddressesParams params) {
        super("GET", "v2/deposit/addresses", params, new TypeReference<>() {});
    }
}
