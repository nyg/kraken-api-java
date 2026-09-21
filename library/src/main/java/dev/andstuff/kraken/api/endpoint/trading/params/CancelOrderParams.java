package dev.andstuff.kraken.api.endpoint.trading.params;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code CancelOrder}, identifying the orders by exactly one of {@code transactionId}, {@code userReference} or {@code clientOrderId}.
 */
@Getter
@Builder(toBuilder = true)
public class CancelOrderParams extends PostParams {

    /**
     * The Kraken identifier of the order to cancel.
     */
    private final String transactionId;

    /**
     * The user reference of the orders to cancel; every open order sharing it is cancelled.
     */
    private final Integer userReference;

    /**
     * The client identifier of the order to cancel.
     */
    private final String clientOrderId;

    /**
     * Returns the parameters sent to Kraken.
     *
     * @return the POST parameters
     * @throws IllegalArgumentException if not exactly one identifier is set
     */
    @Override
    protected Map<String, String> params() {
        if (Stream.of(transactionId, userReference, clientOrderId).filter(Objects::nonNull).count() != 1) {
            throw new IllegalArgumentException("Specify exactly one of transactionId, userReference or clientOrderId");
        }
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "txid", transactionId);
        putIfNonNull(params, "txid", userReference);
        putIfNonNull(params, "cl_ord_id", clientOrderId);
        return params;
    }
}
