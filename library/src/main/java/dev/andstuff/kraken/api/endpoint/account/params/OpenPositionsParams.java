package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code OpenPositions}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class OpenPositionsParams extends PostParams {

    /**
     * The transaction ids for {@code OpenPositions}.
     */
    private final List<String> transactionIds;

    /**
     * The calculate values for {@code OpenPositions}. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean calculateValues;

    /**
     * The consolidation for {@code OpenPositions}.
     */
    private final Consolidation consolidation;

    /**
     * The rebase multiplier for {@code OpenPositions}. Kraken defaults to {@code rebased} when omitted.
     */
    private final RebaseMultiplier rebaseMultiplier;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "txid", transactionIds, v -> String.join(",", v));
        putIfNonNull(params, "docalcs", calculateValues);
        putIfNonNull(params, "consolidation", consolidation, Consolidation::getValue);
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        return params;
    }
}
