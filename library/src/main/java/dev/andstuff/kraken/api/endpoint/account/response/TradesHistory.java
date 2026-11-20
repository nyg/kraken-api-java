package dev.andstuff.kraken.api.endpoint.account.response;

import java.util.Map;

/**
 * The trades history returned by {@code TradesHistory}.
 *
 * @param trades the trades
 * @param count the count; null when the count is omitted, 0 with {@code withoutCount} or cursor pagination
 * @param cursor the pagination cursor; null without cursor pagination or on the last page
 */
public record TradesHistory(Map<String, AccountTrade> trades,
                            Long count,
                            Cursor cursor) implements CursorPage {}
