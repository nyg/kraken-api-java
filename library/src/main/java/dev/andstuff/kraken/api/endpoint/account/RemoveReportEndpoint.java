package dev.andstuff.kraken.api.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.RemoveReportParams;
import dev.andstuff.kraken.api.endpoint.account.response.ReportRemoval;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

public class RemoveReportEndpoint extends PrivateEndpoint<ReportRemoval> {

    public RemoveReportEndpoint(RemoveReportParams params) {
        super("RemoveExport", params, new TypeReference<>() {});
    }
}
