package dev.andstuff.kraken.api.endpoint.funding.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of {@code DepositMethods}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class DepositMethodsParams extends PostParams {

    /**
     * Asset being deposited.
     */
    @NonNull
    private final String asset;

    /**
     * Asset class being deposited (optional).
     */
    private final AssetClass assetClass;

    /**
     * The rebase multiplier for {@code DepositMethods}. Kraken defaults to {@code rebased} when omitted.
     */
    private final RebaseMultiplier rebaseMultiplier;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        params.put("asset", asset);
        putIfNonNull(params, "aclass", assetClass, AssetClass::getValue);
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        return params;
    }
}
