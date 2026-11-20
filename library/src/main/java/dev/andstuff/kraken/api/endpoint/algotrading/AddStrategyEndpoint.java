package dev.andstuff.kraken.api.endpoint.algotrading;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.algotrading.params.AddStrategyParams;
import dev.andstuff.kraken.api.endpoint.algotrading.response.StrategyAdded;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code AddStrategy} endpoint, creating a TWAP strategy that Kraken executes as a series of child orders.
 */
public class AddStrategyEndpoint extends PrivateEndpoint<StrategyAdded> {

    /**
     * Creates the {@code AddStrategy} endpoint.
     *
     * @param params the request parameters
     */
    public AddStrategyEndpoint(AddStrategyParams params) {
        super("AddStrategy", params, new TypeReference<>() {});
    }

    /**
     * Returns the media type used by the {@code AddStrategy} request body.
     *
     * @return {@code application/json}
     */
    @Override
    public String getContentType() {
        return "application/json";
    }
}
