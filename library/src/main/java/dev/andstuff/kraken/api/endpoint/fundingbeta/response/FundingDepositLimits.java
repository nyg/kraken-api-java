package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The deposit limits of an asset returned by List Funding Deposit Limits.
 *
 * @param depositLimits the limits of each deposit method of the asset
 */
public record FundingDepositLimits(@JsonProperty("deposit_limits") List<MethodLimits> depositLimits) {}
