package dev.andstuff.kraken.api.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.ApiKeyInfoParams;
import dev.andstuff.kraken.api.endpoint.account.response.ApiKeyInfo;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code GetApiKeyInfo} endpoint for api key info.
 */
public class ApiKeyInfoEndpoint extends PrivateEndpoint<ApiKeyInfo> {

    /**
     * Creates the {@code GetApiKeyInfo} endpoint with default options.
     */
    public ApiKeyInfoEndpoint() {
        this(ApiKeyInfoParams.builder().build());
    }

    /**
     * Creates the {@code GetApiKeyInfo} endpoint.
     *
     * @param params the request parameters
     */
    public ApiKeyInfoEndpoint(ApiKeyInfoParams params) {
        super("GetApiKeyInfo", params, new TypeReference<>() {});
    }
}
