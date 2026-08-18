package dev.andstuff.kraken.api.endpoint.account;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.LedgerEntriesParams;
import dev.andstuff.kraken.api.endpoint.account.response.LedgerEntry;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code QueryLedgers} endpoint, returning specific ledger entries by identifier.
 */
public class LedgerEntriesEndpoint extends PrivateEndpoint<Map<String, LedgerEntry>> {

    /**
     * Creates the endpoint.
     *
     * @param params the ledger entry identifiers to retrieve
     */
    public LedgerEntriesEndpoint(LedgerEntriesParams params) {
        super("QueryLedgers", params, new TypeReference<>() {});
    }
}
