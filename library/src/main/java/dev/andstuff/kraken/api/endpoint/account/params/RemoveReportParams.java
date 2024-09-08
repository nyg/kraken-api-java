package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class RemoveReportParams extends PostParams {

    @NonNull
    private final String reportId;

    @NonNull
    private final RemovalType type;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        params.put("id", reportId);
        params.put("type", type.toString().toLowerCase());
        return params;
    }
}
