package dev.andstuff.kraken.api.endpoint.account.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The trade balance returned by {@code TradeBalance}.
 *
 * @param equivalentBalance the equivalent balance
 * @param tradeBalance the trade balance
 * @param margin the margin
 * @param unrealizedNet the unrealized net
 * @param costBasis the cost basis
 * @param floatingValue the floating value
 * @param equity the equity
 * @param freeMargin the free margin
 * @param freeMarginForOrders the free margin for orders
 * @param marginLevel the margin level
 * @param unexecutedValue the unexecuted value
 */
public record TradeBalance(@JsonProperty("eb") BigDecimal equivalentBalance,
                           @JsonProperty("tb") BigDecimal tradeBalance,
                           @JsonProperty("m") BigDecimal margin,
                           @JsonProperty("n") BigDecimal unrealizedNet,
                           @JsonProperty("c") BigDecimal costBasis,
                           @JsonProperty("v") BigDecimal floatingValue,
                           @JsonProperty("e") BigDecimal equity,
                           @JsonProperty("mf") BigDecimal freeMargin,
                           @JsonProperty("mfo") BigDecimal freeMarginForOrders,
                           @JsonProperty("ml") BigDecimal marginLevel,
                           @JsonProperty("uv") BigDecimal unexecutedValue) {}
