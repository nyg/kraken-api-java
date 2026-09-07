package dev.andstuff.kraken.api.endpoint.market.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder(toBuilder = true)
public class Level3OrderBookParams extends PostParams {

    @NonNull
    private final String pair;

    private final Integer depth;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        params.put("pair", pair);
        putIfNonNull(params, "depth", depth, String::valueOf);
        return params;
    }
}
