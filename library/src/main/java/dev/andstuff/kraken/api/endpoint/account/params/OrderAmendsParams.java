package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code OrderAmends}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class OrderAmendsParams extends PostParams {

    /**
     * The order id for {@code OrderAmends}.
     */
    private final String orderId;

    /**
     * The rebase multiplier for {@code OrderAmends}. Kraken defaults to {@code rebased} when omitted.
     */
    private final RebaseMultiplier rebaseMultiplier;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "order_id", orderId);
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        return params;
    }
}
