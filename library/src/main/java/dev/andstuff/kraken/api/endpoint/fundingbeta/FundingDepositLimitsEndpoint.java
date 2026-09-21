package dev.andstuff.kraken.api.endpoint.fundingbeta;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingLimitsParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingDepositLimits;

/**
 * The Funding (Beta) List Funding Deposit Limits endpoint ({@code GET /funding/v1/limits/deposit/{asset_class}/{asset}}), returning the deposit limits of an asset for each deposit method.
 */
public class FundingDepositLimitsEndpoint extends FundingBetaEndpoint<FundingDepositLimits> {

    /**
     * Creates the List Funding Deposit Limits endpoint.
     *
     * @param params the request parameters
     */
    public FundingDepositLimitsEndpoint(FundingLimitsParams params) {
        super("GET", "v1/limits/deposit/%s/%s".formatted(pathSegment(params.getAssetClass().getValue()), pathSegment(params.getAsset())), params, new TypeReference<>() {});
    }
}
