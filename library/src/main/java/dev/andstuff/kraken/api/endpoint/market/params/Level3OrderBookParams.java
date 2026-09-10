package dev.andstuff.kraken.api.endpoint.market.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of the {@code Level3} endpoint; unset options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class Level3OrderBookParams extends PostParams {

    /**
     * The asset pair to query, e.g. {@code BTC/USD}.
     */
    @NonNull
    private final String pair;

    /**
     * Price levels per side: 0 (full book), 10, 25, 100, 250 or 1000; defaults to 100.
     */
    private final Integer depth;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        params.put("pair", pair);
        putIfNonNull(params, "depth", depth);
        return params;
    }
}
