package dev.andstuff.kraken.api.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.LedgerInfoParams;
import dev.andstuff.kraken.api.endpoint.account.response.LedgerInfo;

/**
 * The private {@code Ledgers} endpoint, returning the ledger entries of the account, 50 at a time.
 */
public class LedgerInfoEndpoint extends WalletScopedEndpoint<LedgerInfo> {

    /**
     * Creates the endpoint.
     *
     * @param params the filtering and pagination parameters
     */
    public LedgerInfoEndpoint(LedgerInfoParams params) {
        super("Ledgers", params, params.getAccountId(), new TypeReference<>() {});
    }

    /**
     * Returns the media type used by the {@code Ledgers} request body.
     *
     * @return {@code application/json}
     */
    @Override
    public String getContentType() {
        return "application/json";
    }
}
