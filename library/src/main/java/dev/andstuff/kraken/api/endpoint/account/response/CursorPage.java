package dev.andstuff.kraken.api.endpoint.account.response;

/**
 * A page of {@code OpenOrders}, {@code ClosedOrders}, {@code TradesHistory} or {@code Ledgers} results, carrying the cursor of the next page with cursor pagination.
 */
public interface CursorPage {

    /**
     * Returns the pagination cursor.
     *
     * @return the cursor, or null without cursor pagination or on the last page
     */
    Cursor cursor();

    /**
     * Returns the cursor of the next page.
     *
     * @return the token to pass as {@code cursor} for the next page, or null without cursor pagination or on the last page
     */
    default String nextCursor() {
        return cursor() == null ? null : cursor().next();
    }
}
