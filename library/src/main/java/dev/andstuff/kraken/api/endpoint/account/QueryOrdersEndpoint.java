package dev.andstuff.kraken.api.endpoint.account;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.QueryOrdersParams;
import dev.andstuff.kraken.api.endpoint.account.response.Order;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code QueryOrders} endpoint for query orders.
 */
public class QueryOrdersEndpoint extends PrivateEndpoint<Map<String, Order>> {

    /**
     * Creates the {@code QueryOrders} endpoint for the selected transaction IDs.
     *
     * @param transactionIds the transaction IDs to query
     */
    public QueryOrdersEndpoint(List<String> transactionIds) {
        this(QueryOrdersParams.builder().transactionIds(transactionIds).build());
    }

    /**
     * Creates the {@code QueryOrders} endpoint.
     *
     * @param params the request parameters
     */
    public QueryOrdersEndpoint(QueryOrdersParams params) {
        super("QueryOrders", params, new TypeReference<>() {});
    }
}
