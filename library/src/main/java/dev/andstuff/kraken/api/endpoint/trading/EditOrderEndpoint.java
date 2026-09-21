package dev.andstuff.kraken.api.endpoint.trading;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.params.EditOrderParams;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderEdited;

/**
 * The private {@code EditOrder} endpoint, cancelling an open order and replacing it with a new order.
 */
public class EditOrderEndpoint extends PrivateEndpoint<OrderEdited> {

    /**
     * Creates the {@code EditOrder} endpoint.
     *
     * @param params the request parameters
     */
    public EditOrderEndpoint(EditOrderParams params) {
        super("EditOrder", params, new TypeReference<>() {});
    }
}
