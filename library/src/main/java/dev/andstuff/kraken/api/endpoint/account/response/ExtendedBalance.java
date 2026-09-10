package dev.andstuff.kraken.api.endpoint.account.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The extended balance returned by {@code BalanceEx}.
 *
 * @param balance the balance
 * @param credit the credit
 * @param creditUsed the credit used
 * @param holdTrade the hold trade
 */
public record ExtendedBalance(BigDecimal balance,
                              BigDecimal credit,
                              @JsonProperty("credit_used") BigDecimal creditUsed,
                              @JsonProperty("hold_trade") BigDecimal holdTrade) {}
