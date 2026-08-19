package dev.andstuff.kraken.api.endpoint.market.params;

import java.util.List;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.pub.QueryParams;
import lombok.RequiredArgsConstructor;

/**
 * The parameters of the {@code Ticker} endpoint.
 */
@RequiredArgsConstructor
public class TickerParams implements QueryParams {

    private final List<String> pairs;

    public Map<String, String> toMap() {
        return Map.of("pair", String.join(",", pairs));
    }
}
