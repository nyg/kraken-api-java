package dev.andstuff.kraken.api.endpoint.algotrading.params;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.JsonPostParams;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code GetOpenStrategies}, sent as JSON.
 */
@Getter
@Builder(toBuilder = true)
public class OpenStrategiesParams extends JsonPostParams {

    /**
     * The strategy type to restrict the results to, all types when omitted.
     */
    private final StrategyType strategyType;

    @Override
    protected Map<String, Object> params() {
        Map<String, Object> params = new LinkedHashMap<>();
        if (strategyType != null) {
            params.put("strategy_type", strategyType.getValue());
        }
        return params;
    }
}
