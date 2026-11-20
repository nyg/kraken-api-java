package dev.andstuff.kraken.api.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.OrderAmendsParams;
import dev.andstuff.kraken.api.endpoint.account.response.OrderAmends;

/**
 * The private {@code OrderAmends} endpoint for order amends.
 */
public class OrderAmendsEndpoint extends WalletScopedEndpoint<OrderAmends> {

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
        super("OrderAmends", params, params.getAccountId(), new TypeReference<>() {});
    }
}
