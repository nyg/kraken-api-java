package dev.andstuff.kraken.api.endpoint.earn.params;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of the {@code Earn/Strategies} endpoint. All of them are optional: strategies can be restricted to an asset and to lock types, and paged through with the cursor returned by the previous call.
 */
@Getter
@Builder(toBuilder = true)
public class EarnStrategiesParams extends PostParams {

    private final Boolean ascending;
    private final String asset;
    private final String cursor;
    private final Integer limit;
    private final List<LockType> lockTypes;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "ascending", ascending);
        putIfNonNull(params, "asset", asset);
        putIfNonNull(params, "cursor", cursor);
        putIfNonNull(params, "limit", limit);
        putIfNonNull(params, "lock_type", lockTypes, types -> String.join(",", types.stream().map(type -> type.toString().toLowerCase()).toList()));
        return params;
    }
}
