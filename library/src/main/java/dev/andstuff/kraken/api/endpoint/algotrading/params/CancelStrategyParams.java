package dev.andstuff.kraken.api.endpoint.algotrading.params;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.JsonPostParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of {@code CancelStrategy}, sent as JSON.
 */
@Getter
@Builder(toBuilder = true)
public class CancelStrategyParams extends JsonPostParams {

    /**
     * The identifier of the pending or open strategy to cancel.
     */
    @NonNull
    private final String strategyId;

    @Override
    protected Map<String, Object> params() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("strategy_id", strategyId);
        return params;
    }
}
