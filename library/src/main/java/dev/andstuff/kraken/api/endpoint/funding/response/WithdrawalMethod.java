package dev.andstuff.kraken.api.endpoint.funding.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The withdrawal method returned by {@code WithdrawMethods}.
 *
 * @param asset the asset
 * @param method the method
 * @param methodId the method id
 * @param network the network
 * @param networkId the network id
 * @param minimum the minimum
 * @param fee the fee
 * @param limits the limits
 */
public record WithdrawalMethod(String asset,
        String method,
        @JsonProperty("method_id") String methodId,
        String network,
        @JsonProperty("network_id") String networkId,
        BigDecimal minimum,
        Fee fee,
        List<Limits> limits) {

    /**
     * The fee returned by {@code WithdrawMethods}.
     *
     * @param assetClass the asset class
     * @param asset the asset
     * @param fee the fee
     * @param feePercentage the fee percentage
     */
    public record Fee(@JsonProperty("aclass") String assetClass,
            String asset,
            BigDecimal fee,
            @JsonProperty("fee_percentage") BigDecimal feePercentage) {}

    /**
     * The limits returned by {@code WithdrawMethods}.
     *
     * @param description the description
     * @param limitType the limit type
     * @param limits the limits
     */
    public record Limits(String description,
            @JsonProperty("limit_type") String limitType,
            Map<String, LimitWindow> limits) {}

    /**
     * Amounts within one {@code WithdrawMethods} limit window.
     *
     * @param maximum the maximum amount during the window
     * @param remaining the remaining amount during the window
     * @param used the amount already used during the window
     */
    public record LimitWindow(BigDecimal maximum, BigDecimal remaining, BigDecimal used) {}
}
