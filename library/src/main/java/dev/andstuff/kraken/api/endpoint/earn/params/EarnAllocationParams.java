package dev.andstuff.kraken.api.endpoint.earn.params;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The parameters of the {@code Earn/Allocate} and {@code Earn/Deallocate} endpoints, both taking a strategy and an amount expressed in the native asset of that strategy.
 */
@Getter
@RequiredArgsConstructor(staticName = "of")
public class EarnAllocationParams extends PostParams {

    @NonNull
    private final String strategyId;

    @NonNull
    private final BigDecimal amount;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        params.put("strategy_id", strategyId);
        params.put("amount", amount.toPlainString());
        return params;
    }
}
