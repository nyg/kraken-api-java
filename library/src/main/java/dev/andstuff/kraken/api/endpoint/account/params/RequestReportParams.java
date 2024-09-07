package dev.andstuff.kraken.api.endpoint.account.params;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class RequestReportParams extends PostParams {

    private final ReportType type;

    @Builder.Default
    private final ReportFormat format = ReportFormat.CSV;
    private final String description;

    @Builder.Default
    private final String fields = "all";
    private final Instant fromDate;
    private final Instant toDate;

    @Override
    protected Map<String, String> params() {
        HashMap<String, String> params = new HashMap<>();
        putIfNonNull(params, "report", type, t -> t.toString().toLowerCase());
        putIfNonNull(params, "format", format);
        putIfNonNull(params, "description", description);
        putIfNonNull(params, "fields", fields);

        if (fromDate != null) {
            putIfNonNull(params, "start", fromDate, d -> Long.toString(d.getEpochSecond()));
        }

        if (toDate != null) {
            putIfNonNull(params, "end", toDate, d -> Long.toString(d.getEpochSecond()));
        }

        return params;
    }
}
