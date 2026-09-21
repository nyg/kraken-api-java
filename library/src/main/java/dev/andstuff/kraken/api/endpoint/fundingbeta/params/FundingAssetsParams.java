package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingBetaParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of List Funding Assets ({@code GET /funding/v1/assets/{direction}}); omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class FundingAssetsParams extends FundingBetaParams {

    /**
     * Whether assets available for deposit or for withdrawal are listed, sent in the path.
     */
    @NonNull
    private final Direction direction;

    /**
     * The asset class to filter on.
     */
    private final AssetClass assetClass;

    /**
     * The account ID; omit to use the account of the API key.
     */
    private final String accountId;

    @Override
    public Map<String, String> toMap() {
        Map<String, String> query = new LinkedHashMap<>();
        putIfNonNull(query, "asset_class", assetClass, AssetClass::getValue);
        putIfNonNull(query, "account_id", accountId, v -> v);
        return query;
    }
}
