package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.AssetAmount;

/**
 * The deposit or withdrawal limits of a funding method, returned by List Funding Deposit Limits and List Funding Withdrawal Limits.
 *
 * @param methodId the funding method the limits apply to
 * @param maximumAmount the maximum amount allowed by the amount based limits and, for withdrawals, the available balance; attempt and transaction count limits are not included
 * @param maximumReason whether the available balance or the limits determine the maximum withdrawal amount, {@code null} for deposits
 * @param limits the limits of each time window
 */
public record MethodLimits(@JsonProperty("method_id") String methodId,
                           @JsonProperty("maximum_amount") AssetAmount maximumAmount,
                           @JsonProperty("maximum_reason") MaximumReason maximumReason,
                           List<TimeWindowLimit> limits) {

    /**
     * What determines the maximum withdrawal amount.
     */
    public enum MaximumReason {
        BALANCE,
        LIMITS,

        @JsonEnumDefaultValue
        UNKNOWN
    }

    /**
     * A limit applying over a rolling time window.
     *
     * @param timeWindow the length of the time window
     * @param limit the limited metric and its remaining, maximum and used values
     */
    public record TimeWindowLimit(Duration timeWindow,
                                  Limit limit) {

        @JsonCreator
        TimeWindowLimit(@JsonProperty("time_window") long timeWindowSeconds,
                        @JsonProperty("limit") Limit limit) {
            this(Duration.ofSeconds(timeWindowSeconds), limit);
        }
    }

    /**
     * The values of a limit. Amount based limits have {@link LimitValue#amounts()} values, attempt and success limits have {@link LimitValue#count()} values.
     *
     * @param limitType the limited metric
     * @param remaining what remains in the time window
     * @param maximum the maximum for the time window
     * @param used what was used in the time window, {@code null} if not provided
     */
    public record Limit(@JsonProperty("limit_type") LimitType limitType,
                        LimitValue remaining,
                        LimitValue maximum,
                        LimitValue used) {}

    /**
     * The metric a limit applies to.
     */
    public enum LimitType {
        EQUIV_AMOUNT_USD,
        EQUIV_AMOUNT_EUR,
        EQUIV_AMOUNT_CAD,
        EQUIV_AMOUNT_GBP,
        AMOUNT,
        ATTEMPT,
        SUCCESS,

        @JsonEnumDefaultValue
        UNKNOWN
    }
}
