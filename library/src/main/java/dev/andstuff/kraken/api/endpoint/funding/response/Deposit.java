package dev.andstuff.kraken.api.endpoint.funding.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The deposit returned by {@code DepositStatus}.
 *
 * @param method the method
 * @param assetClass the asset class
 * @param asset the asset
 * @param referenceId the reference id
 * @param transactionId the transaction id
 * @param info the info
 * @param amount the amount
 * @param fee the fee
 * @param time the time as an instant
 * @param status the status
 * @param statusProp the status prop
 * @param originators the sending transaction IDs for swept deposits
 */
public record Deposit(String method,
                      @JsonProperty("aclass") String assetClass,
                      String asset,
                      @JsonProperty("refid") String referenceId,
                      @JsonProperty("txid") String transactionId,
                      String info,
                      BigDecimal amount,
                      BigDecimal fee,
                      Instant time,
                      Status status,
                      @JsonProperty("status-prop") StatusProp statusProp,
                      List<String> originators) {

    /**
     * The status values used by the {@code DepositStatus} endpoint.
     */
    public enum Status {
        @JsonProperty("Initial") INITIAL,
        @JsonProperty("Pending") PENDING,
        @JsonProperty("EarlyConfirmed") EARLY_CONFIRMED,
        @JsonProperty("Settled") SETTLED,
        @JsonProperty("Success") SUCCESS,
        @JsonProperty("Failure") FAILURE,
        @JsonEnumDefaultValue UNKNOWN
    }

    /**
     * The status prop values used by the {@code DepositStatus} endpoint.
     */
    public enum StatusProp {
        @JsonProperty("return") RETURN,
        @JsonProperty("onhold") ONHOLD,
        @JsonEnumDefaultValue UNKNOWN
    }
}
