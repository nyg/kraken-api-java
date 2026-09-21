package dev.andstuff.kraken.api.endpoint.fundingbeta;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingFeesParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingFees;

/**
 * The Funding (Beta) Calculate Funding Fees endpoint ({@code GET /funding/v1/fees/{method_id}}), quoting the fee of a deposit or withdrawal amount and, for withdrawals, a token pinning the fee rate.
 */
public class FundingFeesEndpoint extends FundingBetaEndpoint<FundingFees> {

    /**
     * Creates the Calculate Funding Fees endpoint.
     *
     * @param params the request parameters
     */
    public FundingFeesEndpoint(FundingFeesParams params) {
        super("GET", "v1/fees/" + pathSegment(params.getMethodId()), params, new TypeReference<>() {});
    }
}
