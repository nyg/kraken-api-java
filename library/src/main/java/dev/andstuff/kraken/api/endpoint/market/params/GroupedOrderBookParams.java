package dev.andstuff.kraken.api.endpoint.market.params;

import static dev.andstuff.kraken.api.endpoint.pub.QueryParams.putIfNonNull;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.pub.QueryParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder(toBuilder = true)
public class GroupedOrderBookParams implements QueryParams {

    @NonNull
    private final String pair;

    private final Integer depth;

    private final Integer grouping;

    @Override
    public Map<String, String> toMap() {
        Map<String, String> params = new HashMap<>();
        params.put("pair", pair);
        putIfNonNull(params, "depth", depth, String::valueOf);
        putIfNonNull(params, "grouping", grouping, String::valueOf);
        return params;
    }
}
