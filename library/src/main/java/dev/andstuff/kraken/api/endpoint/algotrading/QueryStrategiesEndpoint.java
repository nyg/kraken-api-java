package dev.andstuff.kraken.api.endpoint.algotrading;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.algotrading.params.QueryStrategiesParams;
import dev.andstuff.kraken.api.endpoint.algotrading.response.StrategiesInfo;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code QueryStrategies} endpoint, returning strategies by identifier, whatever their status.
 */
public class QueryStrategiesEndpoint extends PrivateEndpoint<StrategiesInfo> {

    /**
     * Creates the {@code QueryStrategies} endpoint for the given strategies.
     *
     * @param strategyIds the identifiers of the strategies to query, 20 at most
     */
    public QueryStrategiesEndpoint(List<String> strategyIds) {
        this(QueryStrategiesParams.builder().strategyIds(strategyIds).build());
    }

    /**
     * Creates the {@code QueryStrategies} endpoint.
     *
     * @param params the request parameters
     */
    public QueryStrategiesEndpoint(QueryStrategiesParams params) {
        super("QueryStrategies", params, new TypeReference<>() {});
    }

    /**
     * Returns the media type used by the {@code QueryStrategies} request body.
     *
     * @return {@code application/json}
     */
    @Override
    public String getContentType() {
        return "application/json";
    }
}
