package dev.andstuff.kraken.api.endpoint.trading;

import java.time.Duration;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.params.CancelAllOrdersAfterParams;
import dev.andstuff.kraken.api.endpoint.trading.response.DeadMansSwitch;

/**
 * The private {@code CancelAllOrdersAfter} endpoint, a dead man's switch cancelling all orders when its timer expires.
 */
public class CancelAllOrdersAfterEndpoint extends PrivateEndpoint<DeadMansSwitch> {

    /**
     * Creates the {@code CancelAllOrdersAfter} endpoint using the required parameters.
     *
     * @param timeout the delay after which all orders are cancelled, {@link Duration#ZERO} to disable the timer
     */
    public CancelAllOrdersAfterEndpoint(Duration timeout) {
        this(CancelAllOrdersAfterParams.builder().timeout(timeout).build());
    }

    /**
     * Creates the {@code CancelAllOrdersAfter} endpoint.
     *
     * @param params the request parameters
     */
    public CancelAllOrdersAfterEndpoint(CancelAllOrdersAfterParams params) {
        super("CancelAllOrdersAfter", params, new TypeReference<>() {});
    }
}
