package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code TradesHistory}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class TradesHistoryParams extends PostParams {

    /**
     * The type for {@code TradesHistory}. Kraken defaults to {@code all} when omitted.
     */
    private final TradeType type;

    /**
     * The trades for {@code TradesHistory}. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean trades;

    /**
     * The start boundary as Unix seconds or a transaction ID.
     */
    private final String start;

    /**
     * The end boundary as Unix seconds or a transaction ID.
     */
    private final String end;

    /**
     * The offset for {@code TradesHistory}.
     */
    private final Integer offset;

    /**
     * The without count for {@code TradesHistory}. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean withoutCount;

    /**
     * The consolidate taker for {@code TradesHistory}. Kraken defaults to {@code true} when omitted.
     */
    private final Boolean consolidateTaker;

    /**
     * The ledgers for {@code TradesHistory}. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean ledgers;

    /**
     * The rebase multiplier for {@code TradesHistory}. Kraken defaults to {@code rebased} when omitted.
     */
    private final RebaseMultiplier rebaseMultiplier;

    /**
     * The asset class for {@code TradesHistory}. Kraken defaults to {@code forex} when omitted.
     */
    private final AssetClass assetClass;

    /**
     * The pair for {@code TradesHistory}.
     */
    private final String pair;

    /**
     * The limit for {@code TradesHistory}. Kraken defaults to {@code 50} when omitted.
     */
    private final Integer limit;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "type", type, TradeType::getValue);
        putIfNonNull(params, "trades", trades);
        putIfNonNull(params, "start", start);
        putIfNonNull(params, "end", end);
        putIfNonNull(params, "ofs", offset);
        putIfNonNull(params, "without_count", withoutCount);
        putIfNonNull(params, "consolidate_taker", consolidateTaker);
        putIfNonNull(params, "ledgers", ledgers);
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        putIfNonNull(params, "aclass", assetClass, AssetClass::getValue);
        putIfNonNull(params, "pair", pair);
        putIfNonNull(params, "limit", limit);
        return params;
    }
}
