package dev.andstuff.kraken.api.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.params.RecentTradesParams;
import dev.andstuff.kraken.api.endpoint.market.response.RecentTrades;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

/**
 * The public {@code Trades} endpoint, returning typed market data.
 */
public class RecentTradesEndpoint extends PublicEndpoint<RecentTrades> {

    /**
     * Creates the {@code Trades} endpoint using Kraken's default options.
     *
     * @param pair the asset pair to query
     */
    public RecentTradesEndpoint(String pair) {
        this(RecentTradesParams.builder().pair(pair).build());
    }

    /**
     * Creates the {@code Trades} endpoint.
     *
     * @param params the request parameters
     */
    public RecentTradesEndpoint(RecentTradesParams params) {
        super("Trades", params, new TypeReference<>() {});
    }
}
