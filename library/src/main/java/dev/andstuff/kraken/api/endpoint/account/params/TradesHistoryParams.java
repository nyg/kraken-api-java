package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code TradesHistory}; omitted options use Kraken's defaults. With cursor pagination, the filters must be repeated on every page, e.g. with {@code params.toBuilder().cursor(page.nextCursor()).build()}.
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
     * The result offset for {@code TradesHistory}.
     *
     * @deprecated Kraken deprecated {@code ofs}: use {@code withCursor} and {@code cursor} instead.
     */
    @Deprecated(since = "5.0.0")
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

    /**
     * Whether to use cursor pagination instead of {@code offset}. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean withCursor;

    /**
     * The {@code cursor.next} of the previous page, omitted for the first page. Requires {@code withCursor}.
     */
    private final String cursor;

    /**
     * The wallet ID, sent in the URL query; omit to use the default wallet.
     */
    private final String accountId;

    /**
     * Returns the parameters sent to Kraken.
     *
     * @return the POST parameters
     * @throws IllegalArgumentException if {@code cursor} is set without {@code withCursor}, or if {@code offset} is not 0 with {@code withCursor}
     */
    @Override
    protected Map<String, String> params() {
        CursorPagination.validate(withCursor, cursor, "offset", offset);
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
        putIfNonNull(params, "with_cursor", withCursor);
        putIfNonNull(params, "cursor", cursor);
        return params;
    }
}
