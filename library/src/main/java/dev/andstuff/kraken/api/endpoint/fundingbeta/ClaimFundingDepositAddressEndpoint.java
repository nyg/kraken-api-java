package dev.andstuff.kraken.api.endpoint.fundingbeta;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.ClaimFundingDepositAddressParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.ClaimedFundingDepositAddress;

/**
 * The Funding (Beta) Claim Funding Deposit Address endpoint ({@code PUT /funding/v1/deposit/address}), generating a deposit address for a deposit method.
 */
public class ClaimFundingDepositAddressEndpoint extends FundingBetaEndpoint<ClaimedFundingDepositAddress> {

    /**
     * Creates the Claim Funding Deposit Address endpoint.
     *
     * @param params the request parameters
     */
    public ClaimFundingDepositAddressEndpoint(ClaimFundingDepositAddressParams params) {
        super("PUT", "v1/deposit/address", params, new TypeReference<>() {});
    }
}
