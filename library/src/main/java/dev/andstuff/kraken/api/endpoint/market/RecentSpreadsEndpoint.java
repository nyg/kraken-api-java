package dev.andstuff.kraken.api.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.params.RecentSpreadsParams;
import dev.andstuff.kraken.api.endpoint.market.response.RecentSpreads;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

public class RecentSpreadsEndpoint extends PublicEndpoint<RecentSpreads> {

    public RecentSpreadsEndpoint(String pair) {
        this(RecentSpreadsParams.builder().pair(pair).build());
    }

    public RecentSpreadsEndpoint(RecentSpreadsParams params) {
        super("Spread", params, new TypeReference<>() {});
    }
}
