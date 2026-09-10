package dev.andstuff.kraken.api.endpoint.account.response;

import java.util.Map;

/**
 * The closed orders returned by {@code ClosedOrders}.
 *
 * @param closed the closed
 * @param count the count; null when the count is omitted
 */
public record ClosedOrders(Map<String, Order> closed,
                           Long count) {}
