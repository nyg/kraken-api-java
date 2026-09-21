package dev.andstuff.kraken.api.endpoint.trading;

import java.math.BigDecimal;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.params.AddOrderParams;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderSide;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderType;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderAdded;

/**
 * The private {@code AddOrder} endpoint, placing a new order.
 */
public class AddOrderEndpoint extends PrivateEndpoint<OrderAdded> {

    /**
     * Creates the {@code AddOrder} endpoint using the required parameters.
     *
     * @param pair the asset pair, e.g. {@code XBTUSD}
     * @param side the order direction
     * @param orderType the execution model of the order
     * @param volume the order quantity in terms of the base asset
     */
    public AddOrderEndpoint(String pair, OrderSide side, OrderType orderType, BigDecimal volume) {
        this(AddOrderParams.builder().pair(pair).side(side).orderType(orderType).volume(volume).build());
    }

    /**
     * Creates the {@code AddOrder} endpoint.
     *
     * @param params the request parameters
     */
    public AddOrderEndpoint(AddOrderParams params) {
        super("AddOrder", params, new TypeReference<>() {});
    }
}
