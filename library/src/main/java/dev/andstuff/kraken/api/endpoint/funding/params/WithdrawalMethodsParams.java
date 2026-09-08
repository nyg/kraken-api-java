package dev.andstuff.kraken.api.endpoint.funding.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;

import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code WithdrawMethods}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class WithdrawalMethodsParams extends PostParams {

    /**
     * Filter methods for specific asset.
     */
    private final String asset;

    /**
     * Filter methods for specific asset class.
     */
    private final AssetClass assetClass;

    /**
     * Filter methods for specific network.
     */
    private final String network;

    /**
     * The rebase multiplier for {@code WithdrawMethods}. Kraken defaults to {@code rebased} when omitted.
     */
    private final RebaseMultiplier rebaseMultiplier;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "asset", asset);
        putIfNonNull(params, "aclass", assetClass, AssetClass::getValue);
        putIfNonNull(params, "network", network);
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        return params;
    }
}
