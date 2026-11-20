package dev.andstuff.kraken.api.endpoint.algotrading;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.algotrading.params.CancelStrategyParams;
import dev.andstuff.kraken.api.endpoint.algotrading.response.StrategyCancelled;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code CancelStrategy} endpoint, cancelling a pending or open strategy.
 */
public class CancelStrategyEndpoint extends PrivateEndpoint<StrategyCancelled> {

    /**
     * Creates the {@code CancelStrategy} endpoint for the given strategy.
     *
     * @param strategyId the identifier of the strategy to cancel
     */
    public CancelStrategyEndpoint(String strategyId) {
        this(CancelStrategyParams.builder().strategyId(strategyId).build());
    }

    /**
     * Creates the {@code CancelStrategy} endpoint.
     *
     * @param params the request parameters
     */
    public CancelStrategyEndpoint(CancelStrategyParams params) {
        super("CancelStrategy", params, new TypeReference<>() {});
    }

    /**
     * Returns the media type used by the {@code CancelStrategy} request body.
     *
     * @return {@code application/json}
     */
    @Override
    public String getContentType() {
        return "application/json";
    }
}
