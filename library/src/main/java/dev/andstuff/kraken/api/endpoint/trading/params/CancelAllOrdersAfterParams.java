package dev.andstuff.kraken.api.endpoint.trading.params;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of {@code CancelAllOrdersAfter}.
 */
@Getter
@Builder(toBuilder = true)
public class CancelAllOrdersAfterParams extends PostParams {

    /**
     * The delay, in whole seconds below one day, after which all orders are cancelled unless the timer is extended; {@link Duration#ZERO} disables the timer.
     */
    @NonNull
    private final Duration timeout;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        params.put("timeout", Long.toString(timeout.toSeconds()));
        return params;
    }
}
