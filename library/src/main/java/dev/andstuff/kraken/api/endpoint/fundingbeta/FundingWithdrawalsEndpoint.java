package dev.andstuff.kraken.api.endpoint.fundingbeta;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingWithdrawalsParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingWithdrawals;

/**
 * The Funding (Beta) List Funding Withdrawals endpoint ({@code GET /funding/v1/withdrawals}), listing withdrawals, newest first.
 */
public class FundingWithdrawalsEndpoint extends FundingBetaEndpoint<FundingWithdrawals> {

    /**
     * Creates the List Funding Withdrawals endpoint using Kraken's default options.
     */
    public FundingWithdrawalsEndpoint() {
        this(FundingWithdrawalsParams.builder().build());
    }

    /**
     * Creates the List Funding Withdrawals endpoint.
     *
     * @param params the request parameters
     */
    public FundingWithdrawalsEndpoint(FundingWithdrawalsParams params) {
        super("GET", "v1/withdrawals", params, new TypeReference<>() {});
    }
}
