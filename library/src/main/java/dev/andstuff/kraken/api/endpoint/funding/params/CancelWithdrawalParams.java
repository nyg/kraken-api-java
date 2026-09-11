package dev.andstuff.kraken.api.endpoint.funding.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of {@code WithdrawCancel}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class CancelWithdrawalParams extends PostParams {

    /**
     * Asset being withdrawn.
     */
    @NonNull
    private final String asset;

    /**
     * Withdrawal reference ID.
     */
    @NonNull
    private final String referenceId;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        params.put("asset", asset);
        params.put("refid", referenceId);
        return params;
    }
}
