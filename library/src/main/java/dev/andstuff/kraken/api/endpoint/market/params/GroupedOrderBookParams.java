package dev.andstuff.kraken.api.endpoint.market.params;

import static dev.andstuff.kraken.api.endpoint.pub.QueryParams.putIfNonNull;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.pub.QueryParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of the {@code GroupedBook} endpoint; unset options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class GroupedOrderBookParams implements QueryParams {

    /**
     * The asset pair to query, e.g. {@code BTC/USD}.
     */
    @NonNull
    private final String pair;

    /**
     * Price levels per side: 10, 25, 100, 250 or 1000; defaults to 10.
     */
    private final Integer depth;

    /**
     * Ticks per price level: 1, 5, 10, 25, 50, 100, 250, 500 or 1000; defaults to 1.
     */
    private final Integer grouping;

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> toMap() {
        Map<String, String> params = new HashMap<>();
        params.put("pair", pair);
        putIfNonNull(params, "depth", depth, String::valueOf);
        putIfNonNull(params, "grouping", grouping, String::valueOf);
        return params;
    }
}
