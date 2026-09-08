package dev.andstuff.kraken.api.endpoint.funding.params;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of {@code WithdrawInfo}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class WithdrawalInfoParams extends PostParams {

    /**
     * Asset being withdrawn.
     */
    @NonNull
    private final String asset;

    /**
     * The withdrawal key name configured in the Kraken account.
     */
    @NonNull
    private final String key;

    /**
     * Amount to be withdrawn.
     */
    @NonNull
    private final BigDecimal amount;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "asset", asset);
        putIfNonNull(params, "key", key);
        putIfNonNull(params, "amount", amount, BigDecimal::toPlainString);
        return params;
    }
}
