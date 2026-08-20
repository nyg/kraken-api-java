package dev.andstuff.kraken.api.endpoint.earn.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The response of the {@code Earn/Allocations} endpoint, i.e. the earn allocations of the account.
 *
 * @param convertedAsset the asset the converted amounts are expressed in
 * @param totalAllocated the total amount allocated, in the converted asset
 * @param totalRewarded the total amount earned since the creation of the account, in the converted asset
 * @param nextCursor the cursor of the next page, {@code null} on the last one
 * @param items the allocations, one per strategy
 */
public record EarnAllocations(@JsonProperty("converted_asset") String convertedAsset,
                              @JsonProperty("total_allocated") BigDecimal totalAllocated,
                              @JsonProperty("total_rewarded") BigDecimal totalRewarded,
                              @JsonProperty("next_cursor") String nextCursor,
                              List<Allocation> items) {

    /**
     * The allocation of the account to a single strategy.
     *
     * @param strategyId the identifier of the strategy
     * @param nativeAsset the asset allocated to the strategy
     * @param amountAllocated the allocated amount, split by state
     * @param totalRewarded the amount earned on the strategy since the creation of the account
     * @param payout the reward period in progress, {@code null} for strategies that have none
     */
    public record Allocation(@JsonProperty("strategy_id") String strategyId,
                             @JsonProperty("native_asset") String nativeAsset,
                             @JsonProperty("amount_allocated") AmountAllocated amountAllocated,
                             @JsonProperty("total_rewarded") Amount totalRewarded,
                             Payout payout) {}

    /**
     * The amount allocated to a strategy, split by the state the funds are in. Kraken omits the states that don't apply to the strategy.
     *
     * @param bonding the funds bonding, not earning rewards yet
     * @param allocated the funds earning rewards
     * @param exitQueue the funds queued for unbonding, Ethereum only
     * @param unbonding the funds unbonding, on their way back to the spot balance
     * @param pending the funds of an allocation or deallocation still in progress
     * @param total the sum of all states
     */
    public record AmountAllocated(State bonding,
                                  Amount allocated,
                                  @JsonProperty("exit_queue") State exitQueue,
                                  State unbonding,
                                  Amount pending,
                                  Amount total) {}

    /**
     * The funds of a state that holds them for a period, i.e. bonding, unbonding or exit queue.
     *
     * @param nativeAmount the amount, in the asset of the strategy
     * @param converted the amount, in the converted asset
     * @param allocationCount the number of allocations in this state
     * @param allocations the allocations in this state, with the time they leave it
     */
    public record State(@JsonProperty("native") BigDecimal nativeAmount,
                        BigDecimal converted,
                        @JsonProperty("allocation_count") Integer allocationCount,
                        List<Entry> allocations) {}

    /**
     * A single allocation of a bonding, unbonding or exit queue state.
     *
     * @param createdAt the time the allocation entered the state
     * @param expires the time the allocation leaves the state
     * @param nativeAmount the amount, in the asset of the strategy
     * @param converted the amount, in the converted asset
     */
    public record Entry(@JsonProperty("created_at") Instant createdAt,
                        Instant expires,
                        @JsonProperty("native") BigDecimal nativeAmount,
                        BigDecimal converted) {}

    /**
     * The reward period in progress on a strategy.
     *
     * @param periodStart the start of the period
     * @param periodEnd the end of the period, when the reward is paid out
     * @param accumulatedReward the reward accumulated so far in the period
     * @param estimatedReward the reward expected at the end of the period
     */
    public record Payout(@JsonProperty("period_start") Instant periodStart,
                         @JsonProperty("period_end") Instant periodEnd,
                         @JsonProperty("accumulated_reward") Amount accumulatedReward,
                         @JsonProperty("estimated_reward") Amount estimatedReward) {}

    /**
     * An amount, given both in the asset of the strategy and in the converted asset.
     *
     * @param nativeAmount the amount, in the asset of the strategy
     * @param converted the amount, in the converted asset
     */
    public record Amount(@JsonProperty("native") BigDecimal nativeAmount,
                         BigDecimal converted) {}
}
