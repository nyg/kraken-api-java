package dev.andstuff.kraken.api.endpoint.algotrading.params;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.JsonPostParams;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code GetClosedStrategies}, sent as JSON. The cursor only stores a position, so the filters must be repeated on every page, e.g. with {@code params.toBuilder().cursor(page.nextCursor()).build()}.
 */
@Getter
@Builder(toBuilder = true)
public class ClosedStrategiesParams extends JsonPostParams {

    /**
     * The asset class to restrict the results to, all classes when omitted.
     */
    private final StrategyAssetClass assetClass;

    /**
     * The strategy type to restrict the results to, all types when omitted.
     */
    private final StrategyType strategyType;

    /**
     * The terminal status to restrict the results to: {@code closed}, {@code expired} or {@code cancelled}, all three when omitted.
     */
    private final StrategyStatus status;

    /**
     * The inclusive lower bound, sent in Unix seconds and applied according to {@code queryTimeMode}.
     */
    private final Instant start;

    /**
     * The inclusive upper bound, sent in Unix seconds and applied according to {@code queryTimeMode}.
     */
    private final Instant end;

    /**
     * The timestamps the bounds apply to. Kraken defaults to {@code both} when omitted.
     */
    private final QueryTimeMode queryTimeMode;

    /**
     * The maximum number of strategies per page, from 1 to 100. Kraken defaults to 100 when omitted.
     */
    private final Integer limit;

    /**
     * The {@code next_cursor} of the previous page, omitted for the first page.
     */
    private final String cursor;

    @Override
    protected Map<String, Object> params() {
        Map<String, Object> params = new LinkedHashMap<>();
        if (assetClass != null) {
            params.put("asset_class", assetClass.getValue());
        }
        if (strategyType != null) {
            params.put("strategy_type", strategyType.getValue());
        }
        if (status != null) {
            params.put("status", status.getValue());
        }
        if (start != null) {
            params.put("start", start.getEpochSecond());
        }
        if (end != null) {
            params.put("end", end.getEpochSecond());
        }
        if (queryTimeMode != null) {
            params.put("query_time_mode", queryTimeMode.getValue());
        }
        if (limit != null) {
            params.put("limit", limit);
        }
        if (cursor != null) {
            params.put("cursor", cursor);
        }
        return params;
    }
}
