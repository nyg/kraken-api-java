package dev.andstuff.kraken.api.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.params.GroupedOrderBookParams;
import dev.andstuff.kraken.api.endpoint.market.response.GroupedOrderBook;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

public class GroupedOrderBookEndpoint extends PublicEndpoint<GroupedOrderBook> {

    public GroupedOrderBookEndpoint(String pair) {
        this(GroupedOrderBookParams.builder().pair(pair).build());
    }

    public GroupedOrderBookEndpoint(GroupedOrderBookParams params) {
        super("GroupedBook", params, new TypeReference<>() {});
    }
}
