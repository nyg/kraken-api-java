package dev.andstuff.kraken.api.endpoint.account;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.ReportsStatusesParams;
import dev.andstuff.kraken.api.endpoint.account.response.Report;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code ExportStatus} endpoint, returning the status of the previously requested reports.
 */
public class ReportsStatusesEndpoint extends PrivateEndpoint<List<Report>> {

    /**
     * Creates the endpoint.
     *
     * @param type the type of report to list
     */
    public ReportsStatusesEndpoint(ReportsStatusesParams type) {
        super("ExportStatus", type, new TypeReference<>() {});
    }
}
