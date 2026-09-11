package dev.andstuff.kraken.api.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.ClosedOrdersParams;
import dev.andstuff.kraken.api.endpoint.account.response.ClosedOrders;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code ClosedOrders} endpoint for closed orders.
 */
public class ClosedOrdersEndpoint extends PrivateEndpoint<ClosedOrders> {

    /**
     * Creates the {@code ClosedOrders} endpoint with default options.
     */
    public ClosedOrdersEndpoint() {
        this(ClosedOrdersParams.builder().build());
    }

    /**
     * Creates the {@code ClosedOrders} endpoint.
     *
     * @param params the request parameters
     */
    public ClosedOrdersEndpoint(ClosedOrdersParams params) {
        super("ClosedOrders", params, new TypeReference<>() {});
    }
}
