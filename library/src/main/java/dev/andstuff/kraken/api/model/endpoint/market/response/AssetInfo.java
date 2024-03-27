package dev.andstuff.kraken.api.model.endpoint.market.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AssetInfo(@JsonProperty("aclass") String assetClass,
                        @JsonProperty("altname") String alternateName,
                        @JsonProperty("decimals") int maxDecimals,
                        @JsonProperty("display_decimals") int displayedDecimals,
                        @JsonProperty("collateral_value") BigDecimal collateralValue,
                        AssetStatus status) {

    public enum AssetStatus {
        ENABLED,
        DEPOSIT_ONLY,
        WITHDRAWAL_ONLY,
        FUNDING_TEMPORARILY_DISABLED,

        @JsonEnumDefaultValue
        UNKNOWN
    }
}
