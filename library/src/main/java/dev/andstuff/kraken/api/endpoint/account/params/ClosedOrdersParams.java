package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code ClosedOrders}; omitted options use Kraken's defaults. With cursor pagination, the filters must be repeated on every page, e.g. with {@code params.toBuilder().cursor(page.nextCursor()).build()}.
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
     * The result offset for {@code ClosedOrders}.
     *
     * @deprecated Kraken deprecated {@code ofs}: use {@code withCursor} and {@code cursor} instead.
     */
    @Deprecated(since = "5.0.0")
    private final Integer offset;

    /**
     * Whether to use cursor pagination instead of {@code offset}, with pages of up to 50 orders. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean withCursor;

    /**
     * The {@code cursor.next} of the previous page, omitted for the first page. Requires {@code withCursor}.
     */
    private final String cursor;

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
        CursorPagination.validate(withCursor, cursor, offset);
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "trades", trades);
        putIfNonNull(params, "userref", userReference);
        putIfNonNull(params, "cl_ord_id", clientOrderId);
        putIfNonNull(params, "start", start);
        putIfNonNull(params, "end", end);
        putIfNonNull(params, "ofs", offset);
        putIfNonNull(params, "with_cursor", withCursor);
        putIfNonNull(params, "cursor", cursor);
        putIfNonNull(params, "closetime", closeTime, CloseTime::getValue);
        putIfNonNull(params, "consolidate_taker", consolidateTaker);
        putIfNonNull(params, "without_count", withoutCount);
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        return params;
    }
}
