package dev.andstuff.kraken.api.endpoint.fundingbeta;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingDepositsParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingDeposits;

/**
 * The Funding (Beta) List Funding Deposits endpoint ({@code GET /funding/v1/deposits}), listing deposits, newest first.
 */
public class FundingDepositsEndpoint extends FundingBetaEndpoint<FundingDeposits> {

    /**
     * Creates the List Funding Deposits endpoint using Kraken's default options.
     */
    public FundingDepositsEndpoint() {
        this(FundingDepositsParams.builder().build());
    }

    /**
     * Creates the List Funding Deposits endpoint.
     *
     * @param params the request parameters
     */
    public FundingDepositsEndpoint(FundingDepositsParams params) {
        super("GET", "v1/deposits", params, new TypeReference<>() {});
    }
}
