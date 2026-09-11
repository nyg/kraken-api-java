package dev.andstuff.kraken.api.endpoint.funding.response;

import java.math.BigDecimal;

/**
 * The withdrawal info returned by {@code WithdrawInfo}.
 *
 * @param method the method
 * @param limit the limit
 * @param amount the amount
 * @param fee the fee
 */
public record WithdrawalInfo(String method,
                             BigDecimal limit,
                             BigDecimal amount,
                             BigDecimal fee) {}
