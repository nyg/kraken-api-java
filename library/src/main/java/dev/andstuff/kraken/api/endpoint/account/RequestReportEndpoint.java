package dev.andstuff.kraken.api.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.RequestReportParams;
import dev.andstuff.kraken.api.endpoint.account.response.ReportRequest;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code AddExport} endpoint, asking Kraken to generate a report.
 */
public class RequestReportEndpoint extends PrivateEndpoint<ReportRequest> {

    /**
     * Creates the endpoint.
     *
     * @param params the type, format and period of the report
     */
    public RequestReportEndpoint(RequestReportParams params) {
        super("AddExport", params, new TypeReference<>() {});
    }
}
