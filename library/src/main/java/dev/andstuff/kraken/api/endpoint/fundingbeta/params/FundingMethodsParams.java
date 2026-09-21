package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingBetaParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of List Funding Methods ({@code GET /funding/v1/methods/{direction}}); omitted options use Kraken's defaults. A cursor must be sent without the other filters.
 */
@Getter
@Builder(toBuilder = true)
public class FundingMethodsParams extends FundingBetaParams {

    /**
     * Whether deposit or withdrawal methods are listed, sent in the path.
     */
    @NonNull
    private final Direction direction;

    /**
     * The asset to filter on; its class is required, its name is optional.
     */
    private final Asset asset;

    /**
     * Whether amounts of tokenized assets use rebased or base units.
     */
    private final RebaseMultiplier rebaseMultiplier;

    /**
     * The maximum number of methods returned, up to 10000.
     */
    private final Integer limit;

    /**
     * The {@code nextCursor} of the previous page.
     */
    private final String cursor;

    /**
     * The account ID; omit to use the account of the API key.
     */
    private final String accountId;

    @Override
    public Map<String, String> toMap() {
        Map<String, String> query = new LinkedHashMap<>();
        if (asset != null) {
            asset.putQuery(query, "asset");
        }
        putIfNonNull(query, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        putIfNonNull(query, "limit", limit, String::valueOf);
        putIfNonNull(query, "cursor", cursor, v -> v);
        putIfNonNull(query, "account_id", accountId, v -> v);
        return query;
    }
}
