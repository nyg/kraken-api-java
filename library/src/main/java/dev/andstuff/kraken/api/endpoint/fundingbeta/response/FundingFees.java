package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.AssetAmount;

/**
 * The fee quote returned by Calculate Funding Fees.
 *
 * @param fee the calculated fee
 * @param grossAmount the amount including the fee
 * @param netAmount the amount excluding the fee
 * @param feeDetails the fixed and percentage components of the fee
 * @param withdrawalFeeToken the token pinning the quoted fee rate for withdrawals, valid for 5 minutes and reusable; null for deposit methods
 */
public record FundingFees(AssetAmount fee,
                          @JsonProperty("gross_amount") AssetAmount grossAmount,
                          @JsonProperty("net_amount") AssetAmount netAmount,
                          @JsonProperty("fee_details") FeeDetails feeDetails,
                          @JsonProperty("withdrawal_fee_token") String withdrawalFeeToken) {

    /**
     * The components of a calculated fee.
     *
     * @param baseFee the fixed fee component
     * @param feePercentage the percentage fee component
     */
    public record FeeDetails(@JsonProperty("base_fee") AssetAmount baseFee,
                             @JsonProperty("fee_percentage") BigDecimal feePercentage) {}
}
