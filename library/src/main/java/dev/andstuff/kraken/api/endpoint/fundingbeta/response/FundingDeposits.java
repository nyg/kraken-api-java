package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.AssetAmount;

/**
 * A page of deposits returned by List Funding Deposits, newest first.
 *
 * @param deposits the deposits of the page
 * @param nextCursor the cursor of the next page, {@code null} on the last one
 */
public record FundingDeposits(List<Deposit> deposits,
                              @JsonProperty("next_cursor") String nextCursor) {

    /**
     * A deposit.
     *
     * @param depositId the identifier of the deposit
     * @param methodId the funding method of the deposit
     * @param networkId the network of the deposit
     * @param status the settlement status
     * @param amount the deposited amount, {@code null} while unknown
     * @param fee the fee charged, {@code null} until the deposit completes
     * @param createTime when the deposit was created
     */
    public record Deposit(@JsonProperty("deposit_id") String depositId,
                          @JsonProperty("method_id") String methodId,
                          @JsonProperty("network_id") String networkId,
                          Status status,
                          AssetAmount amount,
                          AssetAmount fee,
                          @JsonProperty("create_time") Instant createTime) {}

    /**
     * The settlement status of a deposit.
     */
    public enum Status {
        INITIAL,
        PENDING,
        SETTLED,
        SUCCESS,
        FAILURE,

        @JsonEnumDefaultValue
        UNKNOWN
    }
}
