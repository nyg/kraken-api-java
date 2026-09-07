package dev.andstuff.kraken.api.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.params.RecentTradesParams;
import dev.andstuff.kraken.api.endpoint.market.response.RecentTrades;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

public class RecentTradesEndpoint extends PublicEndpoint<RecentTrades> {

    public RecentTradesEndpoint(String pair) {
        this(RecentTradesParams.builder().pair(pair).build());
    }

    public RecentTradesEndpoint(RecentTradesParams params) {
        super("Trades", params, new TypeReference<>() {});
    }
}
