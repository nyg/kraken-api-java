package dev.andstuff.kraken.api.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.OrderAmendsParams;
import dev.andstuff.kraken.api.endpoint.account.response.OrderAmends;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code OrderAmends} endpoint for order amends.
 */
public class OrderAmendsEndpoint extends PrivateEndpoint<OrderAmends> {

    /**
     * Creates the {@code OrderAmends} endpoint with default options.
     */
    public OrderAmendsEndpoint() {
        this(OrderAmendsParams.builder().build());
    }

    /**
     * Creates the {@code OrderAmends} endpoint.
     *
     * @param params the request parameters
     */
    public OrderAmendsEndpoint(OrderAmendsParams params) {
        super("OrderAmends", params, new TypeReference<>() {});
    }
}
