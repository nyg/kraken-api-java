package dev.andstuff.kraken.api.endpoint.transparency.params;

import static dev.andstuff.kraken.api.endpoint.pub.QueryParams.putIfNonNull;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.pub.QueryParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of the {@code PostTrade} endpoint. The symbol is required, in the {@code BASE/QUOTE} display format, and the trades can be further restricted to a period and to a maximum count.
 */
@Getter
@Builder(toBuilder = true)
public class PostTradeParams implements QueryParams {

    @NonNull
    private final String symbol;

    private final Instant fromTimestamp;
    private final Instant toTimestamp;
    private final Integer count;

    @Override
    public Map<String, String> toMap() {
        Map<String, String> params = new HashMap<>();
        params.put("symbol", symbol);
        putIfNonNull(params, "from_ts", fromTimestamp, Instant::toString);
        putIfNonNull(params, "to_ts", toTimestamp, Instant::toString);
        putIfNonNull(params, "count", count, String::valueOf);
        return params;
    }
}
