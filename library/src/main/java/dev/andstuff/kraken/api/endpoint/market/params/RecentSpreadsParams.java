package dev.andstuff.kraken.api.endpoint.market.params;

import static dev.andstuff.kraken.api.endpoint.pub.QueryParams.putIfNonNull;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.pub.QueryParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of the {@code Spread} endpoint; unset options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class RecentSpreadsParams implements QueryParams {

    /**
     * The asset pair to query, e.g. {@code BTC/USD}.
     */
    @NonNull
    private final String pair;

    /**
     * Unix timestamp in seconds, or the last cursor from the previous Spread response.
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
        putIfNonNull(params, "since", since, String::valueOf);
        putIfNonNull(params, "assetVersion", assetVersion, String::valueOf);
        putIfNonNull(params, "asset_class", assetClass, v -> v);
        return params;
    }
}
