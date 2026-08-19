package dev.andstuff.kraken.api.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.RemoveReportParams;
import dev.andstuff.kraken.api.endpoint.account.response.ReportRemoval;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code RemoveExport} endpoint, deleting a processed report or canceling one being generated.
 */
public class RemoveReportEndpoint extends PrivateEndpoint<ReportRemoval> {

    /**
     * Creates the endpoint.
     *
     * @param params the report identifier and the kind of removal
     */
    public RemoveReportEndpoint(RemoveReportParams params) {
        super("RemoveExport", params, new TypeReference<>() {});
    }
}
