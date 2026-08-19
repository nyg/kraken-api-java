package dev.andstuff.kraken.api.endpoint.subaccount;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;
import dev.andstuff.kraken.api.endpoint.subaccount.params.AccountTransferParams;
import dev.andstuff.kraken.api.endpoint.subaccount.response.AccountTransfer;

/**
 * The private {@code AccountTransfer} endpoint, transferring funds between the master account and its subaccounts. It must be called with an API key of the master account.
 */
public class AccountTransferEndpoint extends PrivateEndpoint<AccountTransfer> {

    /**
     * Creates the endpoint.
     *
     * @param params the asset, amount and accounts of the transfer
     */
    public AccountTransferEndpoint(AccountTransferParams params) {
        super("AccountTransfer", params, new TypeReference<>() {});
    }
}
