package dev.andstuff.kraken.api.endpoint.trading;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderCancellation;

/**
 * The private {@code CancelAll} endpoint, cancelling all open orders.
 */
public class CancelAllOrdersEndpoint extends PrivateEndpoint<OrderCancellation> {

    /**
     * Creates the {@code CancelAll} endpoint.
     */
    public CancelAllOrdersEndpoint() {
        super("CancelAll", new TypeReference<>() {});
    }
}
