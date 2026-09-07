package dev.andstuff.kraken.api.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.params.OhlcParams;
import dev.andstuff.kraken.api.endpoint.market.response.OhlcData;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

/**
 * The public {@code OHLC} endpoint, returning typed market data.
 */
public class OhlcEndpoint extends PublicEndpoint<OhlcData> {

    /**
     * Creates the {@code OHLC} endpoint using Kraken's default options.
     *
     * @param pair the asset pair to query
     */
    public OhlcEndpoint(String pair) {
        this(OhlcParams.builder().pair(pair).build());
    }

    /**
     * Creates the {@code OHLC} endpoint.
     *
     * @param params the request parameters
     */
    public OhlcEndpoint(OhlcParams params) {
        super("OHLC", params, new TypeReference<>() {});
    }
}
