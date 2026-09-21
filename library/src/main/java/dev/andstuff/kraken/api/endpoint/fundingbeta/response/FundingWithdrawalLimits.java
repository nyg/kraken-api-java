package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.AssetAmount;

/**
 * The withdrawal limits of an asset returned by List Funding Withdrawal Limits.
 *
 * @param availableBalance the balance available for withdrawal after holds, open positions, margin requirements and other restrictions, which method limits may reduce further
 * @param withdrawalLimits the limits of each withdrawal method of the asset
 */
public record FundingWithdrawalLimits(@JsonProperty("available_balance") AssetAmount availableBalance,
                                      @JsonProperty("withdrawal_limits") List<MethodLimits> withdrawalLimits) {}
