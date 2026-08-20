package dev.andstuff.kraken.api.endpoint.earn.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The parameters of the {@code Earn/AllocateStatus} and {@code Earn/DeallocateStatus} endpoints.
 */
@Getter
@RequiredArgsConstructor(staticName = "of")
public class EarnStatusParams extends PostParams {

    @NonNull
    private final String strategyId;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        params.put("strategy_id", strategyId);
        return params;
    }
}
