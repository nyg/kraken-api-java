package dev.andstuff.kraken.api.endpoint.transparency;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;
import dev.andstuff.kraken.api.endpoint.transparency.params.PreTradeParams;
import dev.andstuff.kraken.api.endpoint.transparency.response.PreTrade;

/**
 * The public {@code PreTrade} endpoint, returning the aggregated order book of a currency pair, with at most ten price levels on each side.
 */
public class PreTradeEndpoint extends PublicEndpoint<PreTrade> {

    /**
     * Creates the endpoint.
     *
     * @param params the symbol of the currency pair
     */
    public PreTradeEndpoint(PreTradeParams params) {
        super("PreTrade", params, new TypeReference<>() {});
    }
}
