package dev.andstuff.kraken.api.model.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.model.endpoint.account.params.LedgerInfoParams;
import dev.andstuff.kraken.api.model.endpoint.account.response.LedgerInfo;
import dev.andstuff.kraken.api.model.endpoint.priv.PrivateEndpoint;

public class LedgerInfoEndpoint extends PrivateEndpoint<LedgerInfo> {

    public LedgerInfoEndpoint(LedgerInfoParams params) {
        super("Ledgers", params, new TypeReference<>() {});
    }
}
