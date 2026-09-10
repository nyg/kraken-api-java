package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code OpenOrders}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class OpenOrdersParams extends PostParams {

    /**
     * The trades for {@code OpenOrders}. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean trades;

    /**
     * The user reference for {@code OpenOrders}.
     */
    private final Long userReference;

    /**
     * The client order id for {@code OpenOrders}.
     */
    private final String clientOrderId;

    /**
     * The rebase multiplier for {@code OpenOrders}. Kraken defaults to {@code rebased} when omitted.
     */
    private final RebaseMultiplier rebaseMultiplier;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "trades", trades);
        putIfNonNull(params, "userref", userReference);
        putIfNonNull(params, "cl_ord_id", clientOrderId);
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        return params;
    }
}
