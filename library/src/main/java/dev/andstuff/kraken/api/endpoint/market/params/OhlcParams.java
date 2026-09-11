package dev.andstuff.kraken.api.endpoint.market.params;

import static dev.andstuff.kraken.api.endpoint.pub.QueryParams.putIfNonNull;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.pub.QueryParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of the {@code OHLC} endpoint; unset options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class OhlcParams implements QueryParams {

    /**
     * The asset pair to query, e.g. {@code BTC/USD}.
     */
    @NonNull
    private final String pair;

    /**
     * Candle interval in minutes: 1, 5, 15, 30, 60, 240, 1440, 10080 or 21600; defaults to 1.
     */
    private final Integer interval;

    /**
     * Unix timestamp in seconds, or the last cursor from the previous OHLC response.
     */
    private final Long since;

    /**
     * Use 1 for display pair names in the response; omitted for internal names.
     */
    private final Integer assetVersion;

    /**
     * Use tokenized_asset for non-crypto pairs such as xStocks.
     */
    private final String assetClass;

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> toMap() {
        Map<String, String> params = new HashMap<>();
        params.put("pair", pair);
        putIfNonNull(params, "interval", interval, String::valueOf);
        putIfNonNull(params, "since", since, String::valueOf);
        putIfNonNull(params, "assetVersion", assetVersion, String::valueOf);
        putIfNonNull(params, "asset_class", assetClass, v -> v);
        return params;
    }
}
