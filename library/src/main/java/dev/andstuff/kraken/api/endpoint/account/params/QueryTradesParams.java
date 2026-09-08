package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of {@code QueryTrades}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class QueryTradesParams extends PostParams {

    /**
     * The transaction ids for {@code QueryTrades}.
     */
    @NonNull
    private final List<String> transactionIds;

    /**
     * The trades for {@code QueryTrades}. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean trades;

    /**
     * The rebase multiplier for {@code QueryTrades}. Kraken defaults to {@code rebased} when omitted.
     */
    private final RebaseMultiplier rebaseMultiplier;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "txid", transactionIds, v -> String.join(",", v));
        putIfNonNull(params, "trades", trades);
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        return params;
    }
}
