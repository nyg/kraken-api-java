package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class ReportDataParams extends PostParams {

    @NonNull
    private final String reportId;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        params.put("id", reportId);
        return params;
    }
}
