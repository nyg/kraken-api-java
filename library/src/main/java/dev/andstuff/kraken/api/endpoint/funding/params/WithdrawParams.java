package dev.andstuff.kraken.api.endpoint.funding.params;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of {@code Withdraw}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class WithdrawParams extends PostParams {

    /**
     * Asset being withdrawn.
     */
    @NonNull
    private final String asset;

    /**
     * Specify the asset class of the asset being withdrawn.
     */
    private final AssetClass assetClass;

    /**
     * The withdrawal key name configured in the Kraken account.
     */
    @NonNull
    private final String key;

    /**
     * The optional address confirmation; Kraken rejects the withdrawal if it does not match the configured key.
     */
    private final String address;

    /**
     * Amount to be withdrawn.
     */
    @NonNull
    private final BigDecimal amount;

    /**
     * The maximum acceptable fee; Kraken rejects the withdrawal when the processed fee exceeds this amount.
     */
    private final BigDecimal maxFee;

    /**
     * The rebase multiplier for {@code Withdraw}. Kraken defaults to {@code rebased} when omitted.
     */
    private final RebaseMultiplier rebaseMultiplier;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "asset", asset);
        putIfNonNull(params, "aclass", assetClass, AssetClass::getValue);
        putIfNonNull(params, "key", key);
        putIfNonNull(params, "address", address);
        putIfNonNull(params, "amount", amount, BigDecimal::toPlainString);
        putIfNonNull(params, "max_fee", maxFee, BigDecimal::toPlainString);
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
        return params;
    }
}
