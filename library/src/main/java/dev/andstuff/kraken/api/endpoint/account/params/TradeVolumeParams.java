package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of {@code TradeVolume}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class TradeVolumeParams extends PostParams {

    private static final JsonMapper OBJECT_MAPPER = JsonMapper.builder().build();

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
    protected Map<String, String> params() {
        if (pairs != null && pairsWithClass != null) {
            throw new IllegalArgumentException("Specify pairs or pairsWithClass, not both");
        }
        Map<String, String> params = new HashMap<>();
        if (pairsWithClass != null) {
            try {
                params.put("pair", OBJECT_MAPPER.writeValueAsString(pairsWithClass));
            }
            catch (JsonProcessingException e) {
                throw new IllegalStateException("Cannot encode class-qualified pairs", e);
            }
        }
        putIfNonNull(params, "pair", pairs, v -> String.join(",", v));
        putIfNonNull(params, "fee-info", feeInfo);
        putIfNonNull(params, "fee_schedule", feeSchedule);
        putIfNonNull(params, "rebase_multiplier", rebaseMultiplier, RebaseMultiplier::getValue);
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
