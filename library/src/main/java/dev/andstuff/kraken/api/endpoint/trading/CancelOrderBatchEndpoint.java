package dev.andstuff.kraken.api.endpoint.trading;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.params.CancelOrderBatchParams;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderCancellation;

/**
 * The private {@code CancelOrderBatch} endpoint, cancelling up to 50 open orders at once.
 */
public class CancelOrderBatchEndpoint extends PrivateEndpoint<OrderCancellation> {

    /**
     * Creates the {@code CancelOrderBatch} endpoint.
     *
     * @param params the request parameters
     */
    public CancelOrderBatchEndpoint(CancelOrderBatchParams params) {
        super("CancelOrderBatch", params, new TypeReference<>() {});
    }

    /**
     * Returns the media type used by the {@code CancelOrderBatch} request body.
     *
     * @return {@code application/json}
     */
    @Override
    public String getContentType() {
        return "application/json";
    }
}
