package dev.andstuff.kraken.api.endpoint.account.response;

import java.util.Map;

/**
 * The closed orders returned by {@code ClosedOrders}.
 *
 * @param closed the closed
 * @param count the count; null when the count is omitted, 0 with {@code withoutCount} or cursor pagination
 * @param cursor the pagination cursor; null without cursor pagination or on the last page
 */
public record ClosedOrders(Map<String, Order> closed,
                           Long count,
                           Cursor cursor) implements CursorPage {}
