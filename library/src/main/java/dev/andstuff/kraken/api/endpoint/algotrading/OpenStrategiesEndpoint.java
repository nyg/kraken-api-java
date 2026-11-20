package dev.andstuff.kraken.api.endpoint.algotrading;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.algotrading.params.OpenStrategiesParams;
import dev.andstuff.kraken.api.endpoint.algotrading.response.OpenStrategies;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code GetOpenStrategies} endpoint, returning the pending and open strategies of the account.
 */
public class OpenStrategiesEndpoint extends PrivateEndpoint<OpenStrategies> {

    /**
     * Creates the {@code GetOpenStrategies} endpoint for strategies of every type.
     */
    public OpenStrategiesEndpoint() {
        this(OpenStrategiesParams.builder().build());
    }

    /**
     * Creates the {@code GetOpenStrategies} endpoint.
     *
     * @param params the request parameters
     */
    public OpenStrategiesEndpoint(OpenStrategiesParams params) {
        super("GetOpenStrategies", params, new TypeReference<>() {});
    }

    /**
     * Returns the media type used by the {@code GetOpenStrategies} request body.
     *
     * @return {@code application/json}
     */
    @Override
    public String getContentType() {
        return "application/json";
    }
}
