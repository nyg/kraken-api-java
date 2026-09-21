package dev.andstuff.kraken.api.endpoint.trading;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.params.AmendOrderParams;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderAmended;

/**
 * The private {@code AmendOrder} endpoint, modifying an open order in place while keeping its identifiers and, where possible, its queue priority.
 */
public class AmendOrderEndpoint extends PrivateEndpoint<OrderAmended> {

    /**
     * Creates the {@code AmendOrder} endpoint.
     *
     * @param params the request parameters
     */
    public AmendOrderEndpoint(AmendOrderParams params) {
        super("AmendOrder", params, new TypeReference<>() {});
    }
}
