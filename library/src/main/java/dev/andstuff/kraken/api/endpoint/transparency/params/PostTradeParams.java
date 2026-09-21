package dev.andstuff.kraken.api.endpoint.transparency.params;

import static dev.andstuff.kraken.api.endpoint.pub.QueryParams.putIfNonNull;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.pub.QueryParams;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of the {@code PostTrade} endpoint. All of them are optional: the trades can be restricted to a symbol, in the {@code BASE/QUOTE} display format, to a period and to a maximum count. Without any of them, Kraken returns the last 1000 trades of all pairs.
 */
@Getter
@Builder(toBuilder = true)
public class PostTradeParams implements QueryParams {

    private final String symbol;
    private final Instant fromTimestamp;
    private final Instant toTimestamp;
    private final Integer count;

    @Override
    public Map<String, String> toMap() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "symbol", symbol, v -> v);
        putIfNonNull(params, "from_ts", fromTimestamp, Instant::toString);
        putIfNonNull(params, "to_ts", toTimestamp, Instant::toString);
        putIfNonNull(params, "count", count, String::valueOf);
        return params;
    }
}
