package dev.andstuff.kraken.api.endpoint.earn.response;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The response of the {@code Earn/Strategies} endpoint, i.e. a page of earn strategies.
 *
 * @param nextCursor the cursor of the next page, {@code null} on the last one
 * @param items the strategies of the page
 */
public record EarnStrategies(@JsonProperty("next_cursor") String nextCursor,
                             List<Strategy> items) {

    /**
     * A single earn strategy.
     *
     * @param id the identifier of the strategy, to be passed to the allocation endpoints
     * @param asset the asset that can be allocated to the strategy
     * @param lockType how the allocated funds are locked
     * @param aprEstimate the estimated annual percentage rate, based on previous revenues, {@code null} if Kraken gives none
     * @param allocationFee the fee taken when allocating, as a percentage
     * @param deallocationFee the fee taken when deallocating, as a percentage
     * @param autoCompound whether rewards are automatically allocated back to the strategy
     * @param yieldSource where the yield comes from
     * @param canAllocate whether the account may currently allocate to the strategy
     * @param canDeallocate whether the account may currently deallocate from the strategy
     * @param allocationRestrictionInfo why allocation is not possible, empty when it is
     * @param userCap the maximum amount the account may allocate, {@code null} if uncapped
     * @param userMinAllocation the minimum amount of a single allocation, {@code null} if there is none
     */
    public record Strategy(String id,
                           String asset,
                           @JsonProperty("lock_type") LockType lockType,
                           @JsonProperty("apr_estimate") AprEstimate aprEstimate,
                           @JsonProperty("allocation_fee") BigDecimal allocationFee,
                           @JsonProperty("deallocation_fee") BigDecimal deallocationFee,
                           @JsonProperty("auto_compound") AutoCompound autoCompound,
                           @JsonProperty("yield_source") YieldSource yieldSource,
                           @JsonProperty("can_allocate") boolean canAllocate,
                           @JsonProperty("can_deallocate") boolean canDeallocate,
                           @JsonProperty("allocation_restriction_info") List<Restriction> allocationRestrictionInfo,
                           @JsonProperty("user_cap") BigDecimal userCap,
                           @JsonProperty("user_min_allocation") BigDecimal userMinAllocation) {}

    /**
     * How the funds allocated to a strategy are locked. Kraken only sends the fields that apply to the lock type, the others being {@code null}.
     *
     * @param type the lock type
     * @param payoutFrequency how often rewards are paid out
     * @param bondingPeriod how long funds stay bonded before earning rewards
     * @param bondingPeriodVariable whether the bonding period varies
     * @param bondingRewards whether rewards are earned during the bonding period
     * @param exitQueuePeriod how long funds stay in the exit queue before unbonding
     * @param unbondingPeriod how long funds stay unbonding before being available again
     * @param unbondingPeriodVariable whether the unbonding period varies
     * @param unbondingRewards whether rewards are earned during the unbonding period
     */
    public record LockType(Type type,
                           @JsonProperty("payout_frequency") Duration payoutFrequency,
                           @JsonProperty("bonding_period") Duration bondingPeriod,
                           @JsonProperty("bonding_period_variable") Boolean bondingPeriodVariable,
                           @JsonProperty("bonding_rewards") Boolean bondingRewards,
                           @JsonProperty("exit_queue_period") Duration exitQueuePeriod,
                           @JsonProperty("unbonding_period") Duration unbondingPeriod,
                           @JsonProperty("unbonding_period_variable") Boolean unbondingPeriodVariable,
                           @JsonProperty("unbonding_rewards") Boolean unbondingRewards) {

        /**
         * The kind of lock applied to allocated funds.
         */
        public enum Type {
            FLEX,
            BONDED,
            TIMED,
            INSTANT,

            @JsonEnumDefaultValue
            UNKNOWN
        }
    }

    /**
     * The estimated annual percentage rate of a strategy.
     *
     * @param low the lower bound of the estimate
     * @param high the upper bound of the estimate
     */
    public record AprEstimate(BigDecimal low,
                              BigDecimal high) {}

    /**
     * Whether rewards are allocated back to the strategy.
     *
     * @param type whether auto compounding is disabled, enforced or left to the account
     * @param enabledByDefault whether an optional auto compounding is on unless the account says otherwise, {@code null} when it is not optional
     */
    public record AutoCompound(Type type,
                               @JsonProperty("default") Boolean enabledByDefault) {

        /**
         * How auto compounding applies to a strategy.
         */
        public enum Type {
            DISABLED,
            ENABLED,
            OPTIONAL,

            @JsonEnumDefaultValue
            UNKNOWN
        }
    }

    /**
     * Where the yield of a strategy comes from.
     *
     * @param type the source of the yield
     */
    public record YieldSource(Type type) {

        /**
         * The kind of source generating the yield.
         */
        public enum Type {
            STAKING,
            OFF_CHAIN,

            @JsonEnumDefaultValue
            UNKNOWN
        }
    }

    /**
     * Why an account may not allocate to a strategy.
     */
    public enum Restriction {
        TIER,

        @JsonEnumDefaultValue
        UNKNOWN
    }
}
