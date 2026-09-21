package dev.andstuff.kraken.api.endpoint.trading.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The orders returned by {@code AddOrderBatch}.
 *
 * @param orders the orders, in the order they were sent
 */
public record OrderBatchAdded(List<AddedOrder> orders) {

    /**
     * An order of the batch; an order failing pre-match checks, e.g. funding, is rejected without rejecting the rest of the batch.
     *
     * @param description the order description
     * @param error the reason the order was rejected, null when it was added
     * @param transactionId the Kraken identifier of the order, null when it was rejected or only validated
     */
    public record AddedOrder(@JsonProperty("descr") OrderDescription description,
                             String error,
                             @JsonProperty("txid") String transactionId) {}
}
