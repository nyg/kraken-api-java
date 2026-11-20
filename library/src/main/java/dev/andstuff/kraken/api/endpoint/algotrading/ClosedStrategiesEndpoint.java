package dev.andstuff.kraken.api.endpoint.algotrading;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.algotrading.params.ClosedStrategiesParams;
import dev.andstuff.kraken.api.endpoint.algotrading.response.ClosedStrategies;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code GetClosedStrategies} endpoint, returning a page of closed, expired and cancelled strategies.
 */
public class ClosedStrategiesEndpoint extends PrivateEndpoint<ClosedStrategies> {

    /**
     * Creates the {@code GetClosedStrategies} endpoint for the first page of every terminal strategy.
     */
    public ClosedStrategiesEndpoint() {
        this(ClosedStrategiesParams.builder().build());
    }

    /**
     * Creates the {@code GetClosedStrategies} endpoint.
     *
     * @param params the request parameters
     */
    public ClosedStrategiesEndpoint(ClosedStrategiesParams params) {
        super("GetClosedStrategies", params, new TypeReference<>() {});
    }

    /**
     * Returns the media type used by the {@code GetClosedStrategies} request body.
     *
     * @return {@code application/json}
     */
    @Override
    public String getContentType() {
        return "application/json";
    }
}
