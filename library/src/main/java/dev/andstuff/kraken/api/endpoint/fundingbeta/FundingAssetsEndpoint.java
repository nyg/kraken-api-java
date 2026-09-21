package dev.andstuff.kraken.api.endpoint.fundingbeta;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Direction;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingAssetsParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingAssets;

/**
 * The Funding (Beta) List Funding Assets endpoint ({@code GET /funding/v1/assets/{direction}}), listing the assets available for deposit or withdrawal.
 */
public class FundingAssetsEndpoint extends FundingBetaEndpoint<FundingAssets> {

    /**
     * Creates the List Funding Assets endpoint using the required parameters.
     *
     * @param direction whether assets available for deposit or for withdrawal are listed
     */
    public FundingAssetsEndpoint(Direction direction) {
        this(FundingAssetsParams.builder().direction(direction).build());
    }

    /**
     * Creates the List Funding Assets endpoint.
     *
     * @param params the request parameters
     */
    public FundingAssetsEndpoint(FundingAssetsParams params) {
        super("GET", "v1/assets/" + pathSegment(params.getDirection().getValue()), params, new TypeReference<>() {});
    }
}
