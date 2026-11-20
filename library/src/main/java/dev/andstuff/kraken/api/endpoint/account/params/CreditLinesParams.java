package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code CreditLines}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class CreditLinesParams extends PostParams {

    /**
     * The rebase multiplier for {@code CreditLines}. Kraken defaults to {@code rebased} when omitted.
     */
    private final RebaseMultiplier rebaseMultiplier;

    /**
     * The wallet ID, sent in the URL query; omit to use the default wallet.
     */
    private final String accountId;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        return params;
    }
}
