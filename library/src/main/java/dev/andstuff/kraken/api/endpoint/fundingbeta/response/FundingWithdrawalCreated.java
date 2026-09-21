package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.AssetAmount;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;

/**
 * The withdrawal created by Create Funding Withdrawal.
 *
 * @param withdrawalId the identifier of the withdrawal
 * @param netAmount the amount sent after fees
 * @param grossAmount the total amount deducted, fees included
 * @param fee the fee charged
 * @param approvalRequestId set when the withdrawal waits for an approval, and is cancelled if the approval is not granted
 */
public record FundingWithdrawalCreated(@JsonProperty("withdrawal_id") String withdrawalId,
                                       @JsonProperty("net_amount") Amount netAmount,
                                       @JsonProperty("gross_amount") Amount grossAmount,
                                       Amount fee,
                                       @JsonProperty("approval_request_id") String approvalRequestId) {

    /**
     * An amount of a created withdrawal.
     *
     * @param assetAmount the asset and amount
     * @param rebaseMultiplier whether the amount of a tokenized asset uses rebased or base units, {@code null} if not provided
     */
    public record Amount(@JsonProperty("asset_amount") AssetAmount assetAmount,
                         @JsonProperty("rebase_multiplier") RebaseMultiplier rebaseMultiplier) {}
}
