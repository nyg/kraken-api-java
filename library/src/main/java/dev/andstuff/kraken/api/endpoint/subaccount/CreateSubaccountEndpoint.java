package dev.andstuff.kraken.api.endpoint.subaccount;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;
import dev.andstuff.kraken.api.endpoint.subaccount.params.CreateSubaccountParams;

/**
 * The private {@code CreateSubaccount} endpoint, creating a trading subaccount. It must be called with an API key of the master account.
 */
public class CreateSubaccountEndpoint extends PrivateEndpoint<Boolean> {

    /**
     * Creates the endpoint.
     *
     * @param params the username and email address of the subaccount
     */
    public CreateSubaccountEndpoint(CreateSubaccountParams params) {
        super("CreateSubaccount", params, new TypeReference<>() {});
    }
}
