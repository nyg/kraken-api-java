package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An asset of the Funding (Beta) API, identified by its class and name. As a filter, an asset may leave out its name to match a whole class, or its class where Kraken documents it as optional.
 *
 * @param assetClass the asset class
 * @param name the asset name, e.g. {@code USDC}
 */
public record Asset(@JsonProperty("class") AssetClass assetClass,
                    String name) {

    void putQuery(Map<String, String> query, String key) {
        if (assetClass != null) {
            query.put(key + "[class]", assetClass.getValue());
        }
        if (name != null) {
            query.put(key + "[name]", name);
        }
    }

    Map<String, Object> json() {
        if (assetClass == null || name == null) {
            throw new IllegalArgumentException("Asset requires a class and a name");
        }
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("class", assetClass.getValue());
        json.put("name", name);
        return json;
    }
}
