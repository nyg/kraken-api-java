package dev.andstuff.kraken.api.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.LedgerInfoParams;
import dev.andstuff.kraken.api.endpoint.account.response.LedgerInfo;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code Ledgers} endpoint, returning the ledger entries of the account, 50 at a time.
 */
public class LedgerInfoEndpoint extends PrivateEndpoint<LedgerInfo> {

    /**
     * Creates the endpoint.
     *
     * @param params the filtering and pagination parameters
     */
    public LedgerInfoEndpoint(LedgerInfoParams params) {
        super("Ledgers", params, new TypeReference<>() {});
    }
}
