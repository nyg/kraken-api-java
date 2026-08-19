package dev.andstuff.kraken.api.endpoint.transparency.params;

import java.util.Map;

import dev.andstuff.kraken.api.endpoint.pub.QueryParams;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The parameters of the {@code PreTrade} endpoint. Kraken expects a single symbol, in the {@code BASE/QUOTE} display format.
 */
@Getter
@RequiredArgsConstructor(staticName = "of")
public class PreTradeParams implements QueryParams {

    @NonNull
    private final String symbol;

    @Override
    public Map<String, String> toMap() {
        return Map.of("symbol", symbol);
    }
}
