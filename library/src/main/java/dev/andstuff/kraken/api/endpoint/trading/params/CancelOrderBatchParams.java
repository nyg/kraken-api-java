package dev.andstuff.kraken.api.endpoint.trading.params;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.JsonPostParams;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code CancelOrderBatch}, sent as JSON. Up to 50 unique identifiers can be combined.
 */
@Getter
@Builder(toBuilder = true)
public class CancelOrderBatchParams extends JsonPostParams {

    /**
     * The Kraken identifiers of the orders to cancel.
     */
    private final List<String> transactionIds;

    /**
     * The user references of the orders to cancel; every open order sharing one of them is cancelled.
     */
    private final List<Integer> userReferences;

    /**
     * The client identifiers of the orders to cancel.
     */
    private final List<String> clientOrderIds;

    @Override
    protected Map<String, Object> params() {
        List<Object> orders = new ArrayList<>();
        if (transactionIds != null) {
            orders.addAll(transactionIds);
        }
        if (userReferences != null) {
            orders.addAll(userReferences);
        }
        Map<String, Object> params = new LinkedHashMap<>();
        if (!orders.isEmpty()) {
            params.put("orders", orders);
        }
        if (clientOrderIds != null && !clientOrderIds.isEmpty()) {
            params.put("cl_ord_ids", clientOrderIds);
        }
        return params;
    }
}
