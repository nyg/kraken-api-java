package dev.andstuff.kraken.api.endpoint.fundingbeta;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.CreateFundingWithdrawalParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingWithdrawalCreated;

/**
 * The Funding (Beta) Create Funding Withdrawal endpoint ({@code POST /funding/v1/withdrawals}), withdrawing to a saved address.
 */
public class CreateFundingWithdrawalEndpoint extends FundingBetaEndpoint<FundingWithdrawalCreated> {

    /**
     * Creates the Create Funding Withdrawal endpoint.
     *
     * @param params the request parameters
     */
    public CreateFundingWithdrawalEndpoint(CreateFundingWithdrawalParams params) {
        super("POST", "v1/withdrawals", params, new TypeReference<>() {});
    }
}
