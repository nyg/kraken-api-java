package dev.andstuff.kraken.api.endpoint.account.response;

/**
 * The pagination cursor returned by {@code OpenOrders}, {@code ClosedOrders}, {@code TradesHistory} and {@code Ledgers} with cursor pagination, only when more results are available.
 *
 * @param next the token to pass as {@code cursor} to fetch the next page
 */
public record Cursor(String next) {}
