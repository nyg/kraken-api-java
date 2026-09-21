package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingBetaParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of List Funding Deposit Limits and List Funding Withdrawal Limits ({@code GET /funding/v1/limits/{direction}/{asset_class}/{asset}}); omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class FundingLimitsParams extends FundingBetaParams {

    /**
     * The class of the asset whose limits are returned, sent in the path.
     */
    @NonNull
    private final AssetClass assetClass;

    /**
     * The asset whose limits are returned, e.g. {@code BTC}, sent in the path.
     */
    @NonNull
    private final String asset;

    /**
     * The asset limit amounts are expressed in; its name is required, its class is optional. Kraken uses the asset of each funding method when omitted.
     */
    private final Asset preferredAsset;

    /**
     * The account ID; omit to use the account of the API key.
     */
    private final String accountId;

    @Override
    public Map<String, String> toMap() {
        Map<String, String> query = new LinkedHashMap<>();
        if (preferredAsset != null) {
            preferredAsset.putQuery(query, "preferred_asset");
        }
        putIfNonNull(query, "account_id", accountId, v -> v);
        return query;
    }
}
