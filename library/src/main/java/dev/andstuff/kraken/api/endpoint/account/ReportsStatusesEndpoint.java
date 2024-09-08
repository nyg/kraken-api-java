package dev.andstuff.kraken.api.endpoint.account;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.ReportsStatusesParams;
import dev.andstuff.kraken.api.endpoint.account.response.Report;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

public class ReportsStatusesEndpoint extends PrivateEndpoint<List<Report>> {

    public ReportsStatusesEndpoint(ReportsStatusesParams type) {
        super("ExportStatus", type, new TypeReference<>() {});
    }
}
