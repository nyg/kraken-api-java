package dev.andstuff.kraken.api.endpoint.transparency;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;
import dev.andstuff.kraken.api.endpoint.transparency.params.PostTradeParams;
import dev.andstuff.kraken.api.endpoint.transparency.response.PostTrade;

/**
 * The public {@code PostTrade} endpoint, returning the trades executed on the spot exchange for a currency pair.
 */
public class PostTradeEndpoint extends PublicEndpoint<PostTrade> {

    /**
     * Creates the endpoint.
     *
     * @param params the currency pair and the period and count restricting the trades returned
     */
    public PostTradeEndpoint(PostTradeParams params) {
        super("PostTrade", params, new TypeReference<>() {});
    }
}
