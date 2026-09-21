package dev.andstuff.kraken.api.endpoint.fundingbeta;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingLimitsParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingWithdrawalLimits;

/**
 * The Funding (Beta) List Funding Withdrawal Limits endpoint ({@code GET /funding/v1/limits/withdrawal/{asset_class}/{asset}}), returning the available balance and the withdrawal limits of an asset for each withdrawal method.
 */
public class FundingWithdrawalLimitsEndpoint extends FundingBetaEndpoint<FundingWithdrawalLimits> {

    /**
     * Creates the List Funding Withdrawal Limits endpoint.
     *
     * @param params the request parameters
     */
    public FundingWithdrawalLimitsEndpoint(FundingLimitsParams params) {
        super("GET", "v1/limits/withdrawal/%s/%s".formatted(pathSegment(params.getAssetClass().getValue()), pathSegment(params.getAsset())), params, new TypeReference<>() {});
    }
}
