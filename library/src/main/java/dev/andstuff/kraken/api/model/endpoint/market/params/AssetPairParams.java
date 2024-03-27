package dev.andstuff.kraken.api.model.endpoint.market.params;

import java.util.List;
import java.util.Map;

import dev.andstuff.kraken.api.model.endpoint.pub.QueryParams;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AssetPairParams implements QueryParams {

    private final List<String> pairs;
    private final Info info;

    public Map<String, String> toMap() {
        return Map.of(
                "pair", String.join(",", pairs),
                "info", info.getValue());
    }

    @Getter
    @RequiredArgsConstructor
    public enum Info {
        ALL("info"),
        LEVERAGE("leverage"),
        FEES("fees"),
        MARGIN("margin");

        private final String value;
    }
}
