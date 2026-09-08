package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;

import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code TradeBalance}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class TradeBalanceParams extends PostParams {

    /**
     * The asset for {@code TradeBalance}. Kraken defaults to {@code zusd} when omitted.
     */
    private final String asset;

    /**
     * The rebase multiplier for {@code TradeBalance}. Kraken defaults to {@code rebased} when omitted.
     */
    private final RebaseMultiplier rebaseMultiplier;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "asset", asset);
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        return params;
    }
}
