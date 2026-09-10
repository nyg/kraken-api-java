package dev.andstuff.kraken.api.endpoint.funding.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code DepositStatus}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class DepositStatusParams extends PostParams {

    /**
     * Filter for specific asset being deposited.
     */
    private final String asset;

    /**
     * Filter for specific asset class being deposited.
     */
    private final AssetClass assetClass;

    /**
     * Filter for specific name of deposit method.
     */
    private final String method;

    /**
     * The earliest request timestamp in Unix seconds, inclusive.
     */
    private final String start;

    /**
     * The latest request timestamp in Unix seconds, inclusive.
     */
    private final String end;

    /**
     * The next-page cursor, or {@code true}/{@code false} to enable or disable pagination. Kraken defaults to {@code false} when omitted.
     */
    private final String cursor;

    /**
     * Number of results to include per page.
     */
    private final Integer limit;

    /**
     * The rebase multiplier for {@code DepositStatus}. Kraken defaults to {@code rebased} when omitted.
     */
    private final RebaseMultiplier rebaseMultiplier;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "asset", asset);
        putIfNonNull(params, "aclass", assetClass, AssetClass::getValue);
        putIfNonNull(params, "method", method);
        putIfNonNull(params, "start", start);
        putIfNonNull(params, "end", end);
        putIfNonNull(params, "cursor", cursor);
        putIfNonNull(params, "limit", limit);
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        return params;
    }

    /**
     * Builds status filters, accepting either a cursor token or a pagination flag.
     */
    public static class DepositStatusParamsBuilder {

        /**
         * Selects a page using Kraken's opaque cursor token.
         *
         * @param cursor the token, or null to omit pagination
         * @return this builder
         */
        public DepositStatusParamsBuilder cursor(String cursor) {
            this.cursor = cursor;
            return this;
        }

        /**
         * Enables or disables cursor pagination for the first request.
         *
         * @param enabled whether Kraken should return a paginated response
         * @return this builder
         */
        public DepositStatusParamsBuilder cursor(boolean enabled) {
            this.cursor = Boolean.toString(enabled);
            return this;
        }
    }
}
