package dev.andstuff.kraken.api.endpoint.account.response;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The credit lines returned by {@code CreditLines}.
 *
 * @param assetDetails the asset details
 * @param limitsMonitor the limits monitor
 */
public record CreditLines(@JsonProperty("asset_details") Map<String, AssetDetails> assetDetails,
                          @JsonProperty("limits_monitor") LimitsMonitor limitsMonitor) {

    /**
     * The asset details returned by {@code CreditLines}.
     *
     * @param balance the balance
     * @param holdTrade the hold trade
     * @param collateralValue the collateral value factor, from 0 to 1
     * @param creditLimit the credit limit, returned by Kraken as {@code credit}
     * @param creditUsed the credit used
     * @param rolloverFees the rollover rate charged on drawn credit, in percent per rollover period
     * @param reserveFees the reserve rate charged on undrawn credit, in percent per rollover period
     */
    public record AssetDetails(BigDecimal balance,
                               @JsonProperty("hold_trade") BigDecimal holdTrade,
                               @JsonProperty("collateral_value") BigDecimal collateralValue,
                               @JsonProperty("credit") BigDecimal creditLimit,
                               @JsonProperty("credit_used") BigDecimal creditUsed,
                               @JsonProperty("rollover_fees") BigDecimal rolloverFees,
                               @JsonProperty("reserve_fees") BigDecimal reserveFees) {}

    /**
     * The limits monitor returned by {@code CreditLines}.
     *
     * @param totalCreditUsd the total credit usd
     * @param totalCreditUsedUsd the total credit used usd
     * @param totalCollateralValueUsd the total collateral value usd
     * @param equityUsd the equity usd
     * @param ongoingBalance the ongoing balance
     * @param debtToEquity the debt to equity
     */
    public record LimitsMonitor(@JsonProperty("total_credit_usd") BigDecimal totalCreditUsd,
                                @JsonProperty("total_credit_used_usd") BigDecimal totalCreditUsedUsd,
                                @JsonProperty("total_collateral_value_usd") BigDecimal totalCollateralValueUsd,
                                @JsonProperty("equity_usd") BigDecimal equityUsd,
                                @JsonProperty("ongoing_balance") BigDecimal ongoingBalance,
                                @JsonProperty("debt_to_equity") BigDecimal debtToEquity) {}
}
