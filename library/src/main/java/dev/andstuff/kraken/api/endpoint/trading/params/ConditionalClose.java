package dev.andstuff.kraken.api.endpoint.trading.params;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.NonNull;

/**
 * A conditional close order for {@code AddOrder} and {@code AddOrderBatch}. It is triggered by the execution of the primary order, in the same quantity and the opposite direction, and then becomes an independent order that may reduce or increase the net position.
 *
 * @param orderType the close order type; {@link OrderType#MARKET} and {@link OrderType#SETTLE_POSITION} are not accepted
 * @param price the close order price, absolute or relative as for the primary order
 * @param price2 the close order secondary price, for limit variants of triggered orders
 */
public record ConditionalClose(@NonNull OrderType orderType,
                               String price,
                               String price2) {

    /**
     * Creates a conditional close order with a single price.
     *
     * @param orderType the close order type
     * @param price the close order price
     */
    public ConditionalClose(OrderType orderType, String price) {
        this(orderType, price, null);
    }

    Map<String, String> params() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("ordertype", orderType.getValue());
        if (price != null) {
            params.put("price", price);
        }
        if (price2 != null) {
            params.put("price2", price2);
        }
        return params;
    }
}
