package dev.andstuff.kraken.api.endpoint.trading;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.params.AddOrderBatchParams;
import dev.andstuff.kraken.api.endpoint.trading.params.BatchOrder;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderBatchAdded;

/**
 * The private {@code AddOrderBatch} endpoint, placing between 2 and 15 orders on the same pair.
 */
public class AddOrderBatchEndpoint extends PrivateEndpoint<OrderBatchAdded> {

    /**
     * Creates the {@code AddOrderBatch} endpoint using the required parameters.
     *
     * @param pair the asset pair of every order, e.g. {@code BTC/USD}
     * @param orders the orders to place
     */
    public AddOrderBatchEndpoint(String pair, List<BatchOrder> orders) {
        this(AddOrderBatchParams.builder().pair(pair).orders(orders).build());
    }

    /**
     * Creates the {@code AddOrderBatch} endpoint.
     *
     * @param params the request parameters
     */
    public AddOrderBatchEndpoint(AddOrderBatchParams params) {
        super("AddOrderBatch", params, new TypeReference<>() {});
    }

    /**
     * Returns the media type used by the {@code AddOrderBatch} request body.
     *
     * @return {@code application/json}
     */
    @Override
    public String getContentType() {
        return "application/json";
    }
}
