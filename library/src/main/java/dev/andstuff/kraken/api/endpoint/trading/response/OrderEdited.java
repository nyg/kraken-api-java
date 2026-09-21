package dev.andstuff.kraken.api.endpoint.trading.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The replacement order returned by {@code EditOrder}.
 *
 * @param description the description of the new order
 * @param transactionId the Kraken identifier of the new order
 * @param newUserReference the user reference of the edit request
 * @param oldUserReference the user reference of the original order
 * @param ordersCancelled the number of orders cancelled, either 0 or 1
 * @param originalTransactionId the Kraken identifier of the original order
 * @param status whether the edit succeeded
 * @param volume the updated volume
 * @param price the updated price
 * @param price2 the updated secondary price
 * @param errorMessage the error message when the edit failed
 */
public record OrderEdited(@JsonProperty("descr") OrderDescription description,
                          @JsonProperty("txid") String transactionId,
                          @JsonProperty("newuserref") Long newUserReference,
                          @JsonProperty("olduserref") Long oldUserReference,
                          @JsonProperty("orders_cancelled") Integer ordersCancelled,
                          @JsonProperty("originaltxid") String originalTransactionId,
                          Status status,
                          BigDecimal volume,
                          BigDecimal price,
                          BigDecimal price2,
                          @JsonProperty("error_message") String errorMessage) {

    /**
     * The status values used by the {@code EditOrder} endpoint.
     */
    public enum Status {
        @JsonProperty("ok") OK,
        @JsonProperty("err") ERR,
        @JsonEnumDefaultValue UNKNOWN
    }
}
