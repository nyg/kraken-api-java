package dev.andstuff.kraken.api.endpoint.account.response;

import java.util.Map;

/**
 * The trades history returned by {@code TradesHistory}.
 *
 * @param trades the trades
 * @param count the count; null when the count is omitted
 */
public record TradesHistory(Map<String, AccountTrade> trades,
                            Long count) {}
