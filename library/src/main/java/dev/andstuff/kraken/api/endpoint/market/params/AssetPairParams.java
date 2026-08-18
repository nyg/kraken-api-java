package dev.andstuff.kraken.api.endpoint.market.params;

import static dev.andstuff.kraken.api.endpoint.pub.QueryParams.putIfNonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.pub.QueryParams;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The parameters of the {@code AssetPairs} endpoint.
 */
@RequiredArgsConstructor
public class AssetPairParams implements QueryParams {

    private final List<String> pairs;
    private final Info info;

    public Map<String, String> toMap() {
        HashMap<String, String> params = new HashMap<>();
        putIfNonNull(params, "pair", pairs, v -> String.join(",", v));
        putIfNonNull(params, "info", info, Info::getValue);
        return params;
    }

    /**
     * The subset of information to return for each asset pair.
     */
    @Getter
    @RequiredArgsConstructor
    public enum Info {
        ALL("info"),
        LEVERAGE("leverage"),
        FEES("fees"),
        MARGIN("margin");

        private final String value;
    }
}
