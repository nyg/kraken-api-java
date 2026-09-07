package dev.andstuff.kraken.api.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.params.RecentSpreadsParams;
import dev.andstuff.kraken.api.endpoint.market.response.RecentSpreads;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

/**
 * The public {@code Spread} endpoint, returning typed market data.
 */
public class RecentSpreadsEndpoint extends PublicEndpoint<RecentSpreads> {

    /**
     * Creates the {@code Spread} endpoint using Kraken's default options.
     *
     * @param pair the asset pair to query
     */
    public RecentSpreadsEndpoint(String pair) {
        this(RecentSpreadsParams.builder().pair(pair).build());
    }

    /**
     * Creates the {@code Spread} endpoint.
     *
     * @param params the request parameters
     */
    public RecentSpreadsEndpoint(RecentSpreadsParams params) {
        super("Spread", params, new TypeReference<>() {});
    }
}
