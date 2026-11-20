package dev.andstuff.kraken.api.endpoint.account.response;

import java.util.Map;

/**
 * The open orders returned by {@code OpenOrders}.
 *
 * @param open the open
 * @param cursor the pagination cursor; null without cursor pagination or on the last page
 */
public record OpenOrders(Map<String, Order> open,
                         Cursor cursor) {

    /**
     * Returns the cursor of the next page.
     *
     * @return the token to pass as {@code cursor} for the next page, or null without cursor pagination or on the last page
     */
    public String nextCursor() {
        return cursor == null ? null : cursor.next();
    }
}
