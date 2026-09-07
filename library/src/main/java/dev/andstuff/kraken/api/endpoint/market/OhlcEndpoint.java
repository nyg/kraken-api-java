package dev.andstuff.kraken.api.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.params.OhlcParams;
import dev.andstuff.kraken.api.endpoint.market.response.OhlcData;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

public class OhlcEndpoint extends PublicEndpoint<OhlcData> {

    public OhlcEndpoint(String pair) {
        this(OhlcParams.builder().pair(pair).build());
    }

    public OhlcEndpoint(OhlcParams params) {
        super("OHLC", params, new TypeReference<>() {});
    }
}
