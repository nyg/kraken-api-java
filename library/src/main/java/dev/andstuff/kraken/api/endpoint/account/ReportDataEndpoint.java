package dev.andstuff.kraken.api.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.ReportDataParams;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

public class ReportDataEndpoint extends PrivateEndpoint<String> {

    public ReportDataEndpoint(ReportDataParams params) {
        super("RetrieveExport", params, new TypeReference<>() {});
    }
}
