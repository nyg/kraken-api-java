package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;

import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code BalanceEx}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class ExtendedBalanceParams extends PostParams {

    /**
     * The rebase multiplier for {@code BalanceEx}. Kraken defaults to {@code rebased} when omitted.
     */
    private final RebaseMultiplier rebaseMultiplier;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        return params;
    }
}
