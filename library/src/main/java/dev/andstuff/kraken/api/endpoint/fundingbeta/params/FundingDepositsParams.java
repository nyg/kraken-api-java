package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingBetaParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of List Funding Deposits ({@code GET /funding/v1/deposits}); omitted options use Kraken's defaults. A cursor must be sent without the other filters.
 */
@Getter
@Builder(toBuilder = true)
public class FundingDepositsParams extends FundingBetaParams {

    /**
     * The asset to filter on; its class is required, its name is optional.
     */
    private final Asset asset;

    /**
     * The method, network or network group to filter on.
     */
    private final Scope scope;

    /**
     * The {@code nextCursor} of the previous page.
     */
    private final String cursor;

    /**
     * The maximum number of deposits returned, up to 500.
     */
    private final Integer limit;

    /**
     * The earliest creation time of the deposits returned.
     */
    private final Instant startTime;

    /**
     * The latest creation time of the deposits returned.
     */
    private final Instant endTime;

    /**
     * Whether amounts of tokenized assets use rebased or base units.
     */
    private final RebaseMultiplier rebaseMultiplier;

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
        if (scope != null) {
            scope.putQuery(query, "scope");
        }
        putIfNonNull(query, "cursor", cursor, v -> v);
        putIfNonNull(query, "limit", limit, String::valueOf);
        putIfNonNull(query, "start_time", startTime, Instant::toString);
        putIfNonNull(query, "end_time", endTime, Instant::toString);
        putIfNonNull(query, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        putIfNonNull(query, "account_id", accountId, v -> v);
        return query;
    }
}
