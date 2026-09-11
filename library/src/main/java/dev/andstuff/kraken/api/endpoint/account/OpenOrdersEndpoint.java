package dev.andstuff.kraken.api.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.OpenOrdersParams;
import dev.andstuff.kraken.api.endpoint.account.response.OpenOrders;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code OpenOrders} endpoint for open orders.
 */
public class OpenOrdersEndpoint extends PrivateEndpoint<OpenOrders> {

    /**
     * Creates the {@code OpenOrders} endpoint with default options.
     */
    public OpenOrdersEndpoint() {
        this(OpenOrdersParams.builder().build());
    }

    /**
     * Creates the {@code OpenOrders} endpoint.
     *
     * @param params the request parameters
     */
    public OpenOrdersEndpoint(OpenOrdersParams params) {
        super("OpenOrders", params, new TypeReference<>() {});
    }
}
