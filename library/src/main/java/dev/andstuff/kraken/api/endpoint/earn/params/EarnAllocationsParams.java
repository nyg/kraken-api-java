package dev.andstuff.kraken.api.endpoint.earn.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of the {@code Earn/Allocations} endpoint. All of them are optional: Kraken sorts by allocated amount descending, converts amounts to USD and returns strategies with a zero allocation unless told otherwise.
 */
@Getter
@Builder(toBuilder = true)
public class EarnAllocationsParams extends PostParams {

    private final Boolean ascending;
    private final String convertedAsset;
    private final Boolean hideZeroAllocations;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "ascending", ascending);
        putIfNonNull(params, "converted_asset", convertedAsset);
        putIfNonNull(params, "hide_zero_allocations", hideZeroAllocations);
        return params;
    }
}
