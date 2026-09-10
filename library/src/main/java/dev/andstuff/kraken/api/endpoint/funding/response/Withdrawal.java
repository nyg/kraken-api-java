package dev.andstuff.kraken.api.endpoint.funding.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The withdrawal returned by {@code WithdrawStatus}.
 *
 * @param method the method
 * @param network the network
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
 * @param key the key
 */
public record Withdrawal(String method,
                         String network,
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
                         String key) {

    /**
     * The status values used by the {@code WithdrawStatus} endpoint.
     */
    public enum Status {
        @JsonProperty("Initial") INITIAL,
        @JsonProperty("Pending") PENDING,
        @JsonProperty("Settled") SETTLED,
        @JsonProperty("Success") SUCCESS,
        @JsonProperty("Failure") FAILURE,
        @JsonEnumDefaultValue UNKNOWN
    }

    /**
     * The status prop values used by the {@code WithdrawStatus} endpoint.
     */
    public enum StatusProp {
        @JsonProperty("cancel-pending") CANCEL_PENDING,
        @JsonProperty("canceled") CANCELED,
        @JsonProperty("cancel-denied") CANCEL_DENIED,
        @JsonProperty("return") RETURN,
        @JsonProperty("onhold") ONHOLD,
        @JsonEnumDefaultValue UNKNOWN
    }
}
