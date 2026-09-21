package dev.andstuff.kraken.api.endpoint.fundingbeta;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Direction;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingMethodsParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingMethods;

/**
 * The Funding (Beta) List Funding Methods endpoint ({@code GET /funding/v1/methods/{direction}}), listing the deposit or withdrawal methods available to the account.
 */
public class FundingMethodsEndpoint extends FundingBetaEndpoint<FundingMethods> {

    /**
     * Creates the List Funding Methods endpoint using the required parameters.
     *
     * @param direction whether deposit or withdrawal methods are listed
     */
    public FundingMethodsEndpoint(Direction direction) {
        this(FundingMethodsParams.builder().direction(direction).build());
    }

    /**
     * Creates the List Funding Methods endpoint.
     *
     * @param params the request parameters
     */
    public FundingMethodsEndpoint(FundingMethodsParams params) {
        super("GET", "v1/methods/" + pathSegment(params.getDirection().getValue()), params, new TypeReference<>() {});
    }
}
