package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code ClosedOrders}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class ClosedOrdersParams extends PostParams {

    /**
     * The trades for {@code ClosedOrders}. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean trades;

    /**
     * The user reference for {@code ClosedOrders}.
     */
    private final Long userReference;

    /**
     * The client order id for {@code ClosedOrders}.
     */
    private final String clientOrderId;

    /**
     * The start boundary as Unix seconds or a transaction ID.
     */
    private final String start;

    /**
     * The end boundary as Unix seconds or a transaction ID.
     */
    private final String end;

    /**
     * The offset for {@code ClosedOrders}.
     */
    private final Integer offset;

    /**
     * The close time for {@code ClosedOrders}. Kraken defaults to {@code both} when omitted.
     */
    private final CloseTime closeTime;

    /**
     * The consolidate taker for {@code ClosedOrders}. Kraken defaults to {@code true} when omitted.
     */
    private final Boolean consolidateTaker;

    /**
     * The without count for {@code ClosedOrders}. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean withoutCount;

    /**
     * The rebase multiplier for {@code ClosedOrders}. Kraken defaults to {@code rebased} when omitted.
     */
    private final RebaseMultiplier rebaseMultiplier;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "trades", trades);
        putIfNonNull(params, "userref", userReference);
        putIfNonNull(params, "cl_ord_id", clientOrderId);
        putIfNonNull(params, "start", start);
        putIfNonNull(params, "end", end);
        putIfNonNull(params, "ofs", offset);
        putIfNonNull(params, "closetime", closeTime, CloseTime::getValue);
        putIfNonNull(params, "consolidate_taker", consolidateTaker);
        putIfNonNull(params, "without_count", withoutCount);
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        return params;
    }
}
