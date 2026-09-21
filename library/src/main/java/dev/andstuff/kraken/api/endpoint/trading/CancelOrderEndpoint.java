package dev.andstuff.kraken.api.endpoint.trading;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.params.CancelOrderParams;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderCancellation;

/**
 * The private {@code CancelOrder} endpoint, cancelling an open order or every open order sharing a user reference.
 */
public class CancelOrderEndpoint extends PrivateEndpoint<OrderCancellation> {

    /**
     * Creates the {@code CancelOrder} endpoint for a single order.
     *
     * @param transactionId the Kraken identifier of the order
     */
    public CancelOrderEndpoint(String transactionId) {
        this(CancelOrderParams.builder().transactionId(transactionId).build());
    }

    /**
     * Creates the {@code CancelOrder} endpoint.
     *
     * @param params the request parameters
     */
    public CancelOrderEndpoint(CancelOrderParams params) {
        super("CancelOrder", params, new TypeReference<>() {});
    }
}
