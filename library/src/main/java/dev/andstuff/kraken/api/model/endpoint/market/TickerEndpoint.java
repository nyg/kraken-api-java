package dev.andstuff.kraken.api.model.endpoint.market;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.model.endpoint.market.params.TickerParams;
import dev.andstuff.kraken.api.model.endpoint.market.response.Ticker;
import dev.andstuff.kraken.api.model.endpoint.pub.PublicEndpoint;

public class TickerEndpoint extends PublicEndpoint<Map<String, Ticker>> {

    public TickerEndpoint(List<String> pairs) {
        super("Ticker", new TickerParams(pairs), new TypeReference<>() {});
    }
}
