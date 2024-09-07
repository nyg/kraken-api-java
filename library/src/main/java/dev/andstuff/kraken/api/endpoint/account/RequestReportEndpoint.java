package dev.andstuff.kraken.api.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.RequestReportParams;
import dev.andstuff.kraken.api.endpoint.account.response.ReportRequest;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

public class RequestReportEndpoint extends PrivateEndpoint<ReportRequest> {

    public RequestReportEndpoint(RequestReportParams params) {
        super("AddExport", params, new TypeReference<>() {});
    }
}
