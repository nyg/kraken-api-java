package dev.andstuff.kraken.api.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.WalletAccountsParams;
import dev.andstuff.kraken.api.endpoint.account.response.WalletAccounts;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code ListWalletAccounts} endpoint for wallet accounts.
 */
public class WalletAccountsEndpoint extends PrivateEndpoint<WalletAccounts> {

    /**
     * Creates the {@code ListWalletAccounts} endpoint with default options.
     */
    public WalletAccountsEndpoint() {
        this(WalletAccountsParams.builder().build());
    }

    /**
     * Creates the {@code ListWalletAccounts} endpoint.
     *
     * @param params the request parameters
     */
    public WalletAccountsEndpoint(WalletAccountsParams params) {
        super("ListWalletAccounts", params, new TypeReference<>() {});
    }
}
