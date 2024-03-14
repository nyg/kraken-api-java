package dev.andstuff.kraken.api.model.endpoint.account;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.model.endpoint.account.params.LedgerEntriesParams;
import dev.andstuff.kraken.api.model.endpoint.account.response.LedgerEntry;
import dev.andstuff.kraken.api.model.endpoint.priv.PrivateEndpoint;

public class LedgerEntriesEndpoint extends PrivateEndpoint<Map<String, LedgerEntry>> {

    public LedgerEntriesEndpoint(LedgerEntriesParams params) {
        super("QueryLedgers", params, new TypeReference<>() {});
    }
}
