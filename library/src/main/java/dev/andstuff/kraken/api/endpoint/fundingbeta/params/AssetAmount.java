package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An amount of the Funding (Beta) API and the asset it is denominated in.
 *
 * @param asset the asset
 * @param amount the decimal amount
 */
public record AssetAmount(Asset asset,
                          BigDecimal amount) {

    Map<String, Object> json() {
        if (asset == null || amount == null) {
            throw new IllegalArgumentException("AssetAmount requires an asset and an amount");
        }
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("asset", asset.json());
        json.put("amount", amount.toPlainString());
        return json;
    }
}
