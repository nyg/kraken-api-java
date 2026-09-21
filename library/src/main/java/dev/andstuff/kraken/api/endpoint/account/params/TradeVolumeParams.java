package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.andstuff.kraken.api.endpoint.priv.JsonPostParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of {@code TradeVolume}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class TradeVolumeParams extends JsonPostParams {

    /**
     * The pairs for {@code TradeVolume}.
     */
    private final List<String> pairs;

    /**
     * Class-qualified pairs, used instead of {@code pairs} when selecting non-forex pairs.
     */
    private final List<Pair> pairsWithClass;

    /**
     * The fee info for {@code TradeVolume}. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean feeInfo;

    /**
     * The fee schedule for {@code TradeVolume}.
     */
    private final Boolean feeSchedule;

    /**
     * The rebase multiplier for {@code TradeVolume}. Kraken defaults to {@code rebased} when omitted.
     */
    private final RebaseMultiplier rebaseMultiplier;

    @Override
    protected Map<String, Object> params() {
        if (pairs != null && pairsWithClass != null) {
            throw new IllegalArgumentException("Specify pairs or pairsWithClass, not both");
        }
        Map<String, Object> params = new LinkedHashMap<>();
        if (pairs != null) {
            params.put("pair", String.join(",", pairs));
        }
        else if (pairsWithClass != null) {
            params.put("pair", pairsWithClass);
        }
        if (feeInfo != null) {
            params.put("fee-info", feeInfo);
        }
        if (feeSchedule != null) {
            params.put("fee_schedule", feeSchedule);
        }
        if (rebaseMultiplier != null) {
            params.put("rebase_multiplier", rebaseMultiplier.getValue());
        }
        return params;
    }

    /**
     * A class-qualified trading pair for {@code TradeVolume}.
     *
     * @param asset the pair name
     * @param assetClass the Kraken asset class, such as {@code forex} or {@code derivatives}
     */
    public record Pair(@NonNull String asset, @NonNull @JsonProperty("aclass") String assetClass) {}
}
