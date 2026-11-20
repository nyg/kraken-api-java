package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code OpenOrders}; omitted options use Kraken's defaults. With cursor pagination, the filters must be repeated on every page, e.g. with {@code params.toBuilder().cursor(page.nextCursor()).build()}.
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
     * Whether trades are consolidated by individual taker trades. Kraken defaults to {@code true} when omitted.
     */
    private final Boolean consolidateTaker;

    /**
     * Whether to use cursor pagination, with pages of at most {@code limit} orders. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean withCursor;

    /**
     * The maximum number of orders to return. With cursor pagination, Kraken defaults to {@code 50} and rejects values above {@code 100}; otherwise all open orders are returned unless set, up to {@code 1000}.
     */
    private final Integer limit;

    /**
     * The {@code cursor.next} of the previous page, omitted for the first page. Requires {@code withCursor}.
     */
    private final String cursor;

    /**
     * The rebase multiplier for {@code OpenOrders}. Kraken defaults to {@code rebased} when omitted.
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
     * @throws IllegalArgumentException if {@code cursor} is set without {@code withCursor}
     */
    @Override
    protected Map<String, String> params() {
        CursorPagination.validate(withCursor, cursor);
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "trades", trades);
        putIfNonNull(params, "userref", userReference);
        putIfNonNull(params, "cl_ord_id", clientOrderId);
        putIfNonNull(params, "consolidate_taker", consolidateTaker);
        putIfNonNull(params, "with_cursor", withCursor);
        putIfNonNull(params, "limit", limit);
        putIfNonNull(params, "cursor", cursor);
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        return params;
    }
}
