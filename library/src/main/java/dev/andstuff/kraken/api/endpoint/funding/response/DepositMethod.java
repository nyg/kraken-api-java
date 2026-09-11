package dev.andstuff.kraken.api.endpoint.funding.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The deposit method returned by {@code DepositMethods}.
 *
 * @param method the method
 * @param limit the current maximum deposit, an unlimited marker, or null if omitted
 * @param fee the fee
 * @param feePercentage the fee percentage
 * @param addressSetupFee the address setup fee
 * @param genAddress the gen address
 * @param minimum the minimum
 */
public record DepositMethod(String method,
                            DepositLimit limit,
                            BigDecimal fee,
                            @JsonProperty("fee-percentage") BigDecimal feePercentage,
                            @JsonProperty("address-setup-fee") String addressSetupFee,
                            @JsonProperty("gen-address") Boolean genAddress,
                            BigDecimal minimum) {}
