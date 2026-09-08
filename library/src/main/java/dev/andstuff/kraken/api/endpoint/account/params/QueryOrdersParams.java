package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of {@code QueryOrders}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class QueryOrdersParams extends PostParams {

    /**
     * The trades for {@code QueryOrders}. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean trades;

    /**
     * The user reference for {@code QueryOrders}.
     */
    private final Long userReference;

    /**
     * The transaction ids for {@code QueryOrders}.
     */
    @NonNull
    private final List<String> transactionIds;

    /**
     * The consolidate taker for {@code QueryOrders}. Kraken defaults to {@code true} when omitted.
     */
    private final Boolean consolidateTaker;

    /**
     * The rebase multiplier for {@code QueryOrders}. Kraken defaults to {@code rebased} when omitted.
     */
    private final RebaseMultiplier rebaseMultiplier;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "trades", trades);
        putIfNonNull(params, "userref", userReference);
        putIfNonNull(params, "txid", transactionIds, v -> String.join(",", v));
        putIfNonNull(params, "consolidate_taker", consolidateTaker);
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        return params;
    }
}
