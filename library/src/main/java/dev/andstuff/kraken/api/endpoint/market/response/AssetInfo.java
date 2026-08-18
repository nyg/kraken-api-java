package dev.andstuff.kraken.api.endpoint.market.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Information about a single asset, as returned by the {@code Assets} endpoint.
 *
 * @param assetClass the class of the asset, e.g. {@code currency}
 * @param alternateName the alternate name of the asset, e.g. {@code XBT} for {@code XXBT}
 * @param maxDecimals the number of decimal places Kraken uses for record keeping
 * @param displayedDecimals the number of decimal places Kraken uses for display
 * @param collateralValue the value of the asset when used as collateral, {@code null} if it cannot be
 * @param status the funding and trading status of the asset
 */
public record AssetInfo(@JsonProperty("aclass") String assetClass,
                        @JsonProperty("altname") String alternateName,
                        @JsonProperty("decimals") int maxDecimals,
                        @JsonProperty("display_decimals") int displayedDecimals,
                        @JsonProperty("collateral_value") BigDecimal collateralValue,
                        AssetStatus status) {

    /**
     * The funding and trading status of an asset.
     */
    public enum AssetStatus {
        ENABLED,
        DEPOSIT_ONLY,
        WITHDRAWAL_ONLY,
        FUNDING_TEMPORARILY_DISABLED,

        @JsonEnumDefaultValue
        UNKNOWN
    }
}
