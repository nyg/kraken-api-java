package dev.andstuff.kraken.api.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.params.GroupedOrderBookParams;
import dev.andstuff.kraken.api.endpoint.market.response.GroupedOrderBook;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

/**
 * The public {@code GroupedBook} endpoint, returning typed market data.
 */
public class GroupedOrderBookEndpoint extends PublicEndpoint<GroupedOrderBook> {

    /**
     * Creates the {@code GroupedBook} endpoint using Kraken's default options.
     *
     * @param pair the asset pair to query
     */
    public GroupedOrderBookEndpoint(String pair) {
        this(GroupedOrderBookParams.builder().pair(pair).build());
    }

    /**
     * Creates the {@code GroupedBook} endpoint.
     *
     * @param params the request parameters
     */
    public GroupedOrderBookEndpoint(GroupedOrderBookParams params) {
        super("GroupedBook", params, new TypeReference<>() {});
    }
}
