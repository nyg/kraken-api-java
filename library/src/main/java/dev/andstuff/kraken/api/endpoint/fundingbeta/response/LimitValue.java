package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.AssetAmount;

/**
 * A value of a funding limit, which Kraken returns as a count for attempt and success limits, or as amounts for amount based limits. Exactly one of the components is set.
 *
 * @param count the number of transactions, for attempt and success limits
 * @param amounts the amounts, for amount based limits
 */
@JsonDeserialize(using = LimitValueDeserializer.class)
public record LimitValue(Long count, Amounts amounts) {

    /**
     * Creates either a count or an amount value.
     *
     * @param count the number of transactions
     * @param amounts the amounts
     * @throws IllegalArgumentException if not exactly one of count and amounts is set
     */
    public LimitValue {
        if ((count == null) == (amounts == null)) {
            throw new IllegalArgumentException("A limit value is either a count or amounts");
        }
    }

    /**
     * The amounts of an amount based limit.
     *
     * @param policyAssetAmount the amount in the asset used by the limit policy
     * @param usdAmount the equivalent amount in USD
     * @param requestedAssetAmount the equivalent amount in the preferred asset, or in the asset of the funding method when no preferred asset was requested
     */
    public record Amounts(@JsonProperty("policy_asset_amount") AssetAmount policyAssetAmount,
                          @JsonProperty("usd_amount") AssetAmount usdAmount,
                          @JsonProperty("requested_asset_amount") AssetAmount requestedAssetAmount) {}
}
