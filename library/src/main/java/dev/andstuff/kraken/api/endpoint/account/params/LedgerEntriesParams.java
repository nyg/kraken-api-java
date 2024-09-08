package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class LedgerEntriesParams extends PostParams {

    @Builder.Default
    private final List<String> entryIds = List.of();
    private final boolean includeTrades;

    @Override
    public Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "id", entryIds, v -> String.join(",", v));
        putIfNonNull(params, "trades", includeTrades);
        return params;
    }
}
