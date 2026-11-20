package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of the {@code QueryLedgers} endpoint.
 */
@Getter
@Builder(toBuilder = true)
public class LedgerEntriesParams extends PostParams {

    @Builder.Default
    private final List<String> entryIds = List.of();

    /**
     * Whether trades related to the position are included, sent only when {@code true}.
     *
     * @deprecated Kraken deprecated {@code trades} on {@code QueryLedgers} and advises not to rely on it.
     */
    @Deprecated
    private final boolean includeTrades;

    private final RebaseMultiplier rebaseMultiplier;

    /**
     * The wallet ID, sent in the URL query; omit to use the default wallet.
     */
    private final String accountId;

    @Override
    public Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "id", entryIds, v -> String.join(",", v));
        if (includeTrades) {
            params.put("trades", "true");
        }
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        return params;
    }
}
