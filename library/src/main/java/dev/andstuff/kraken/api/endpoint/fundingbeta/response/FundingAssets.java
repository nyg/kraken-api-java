package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The assets available for deposit or withdrawal returned by List Funding Assets, by asset class.
 *
 * @param currency the assets of the {@code currency} class, {@code null} when filtered out
 * @param tokenizedAsset the assets of the {@code tokenized_asset} class, {@code null} when filtered out
 */
public record FundingAssets(List<AssetName> currency,
                            @JsonProperty("tokenized_asset") List<AssetName> tokenizedAsset) {

    /**
     * An asset available for funding.
     *
     * @param name the asset name, e.g. {@code USDC}
     */
    public record AssetName(String name) {}
}
