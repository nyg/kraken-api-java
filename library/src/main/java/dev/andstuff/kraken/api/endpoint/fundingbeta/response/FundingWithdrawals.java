package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.AssetAmount;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingWithdrawalStatus;

/**
 * A page of withdrawals returned by List Funding Withdrawals, newest first.
 *
 * @param withdrawals the withdrawals of the page
 * @param nextCursor the cursor of the next page, {@code null} on the last one
 */
public record FundingWithdrawals(List<Withdrawal> withdrawals,
                                 @JsonProperty("next_cursor") String nextCursor) {

    /**
     * A withdrawal.
     *
     * @param withdrawalId the identifier of the withdrawal
     * @param amount the withdrawn amount
     * @param fee the fee charged
     * @param methodId the funding method of the withdrawal
     * @param status the withdrawal status
     * @param createTime when the withdrawal was created
     * @param addressId the saved address withdrawn to, {@code null} for withdrawals created without one
     * @param onchainTransaction the on-chain transaction identifier, {@code null} until available
     * @param utxoVout the Bitcoin transaction output index, {@code null} if not applicable
     */
    public record Withdrawal(@JsonProperty("withdrawal_id") String withdrawalId,
                             AssetAmount amount,
                             AssetAmount fee,
                             @JsonProperty("method_id") String methodId,
                             FundingWithdrawalStatus status,
                             @JsonProperty("create_time") Instant createTime,
                             @JsonProperty("address_id") String addressId,
                             @JsonProperty("onchain_transaction") String onchainTransaction,
                             @JsonProperty("utxo_vout") Long utxoVout) {}
}
