package dev.andstuff.kraken.api.endpoint.algotrading.params;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.JsonPostParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of {@code QueryStrategies}, sent as JSON.
 */
@Getter
@Builder(toBuilder = true)
public class QueryStrategiesParams extends JsonPostParams {

    /**
     * The identifiers of the strategies to query, 20 at most.
     */
    @NonNull
    private final List<String> strategyIds;

    @Override
    protected Map<String, Object> params() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("strategy_ids", String.join(",", strategyIds));
        return params;
    }
}
