package dev.andstuff.kraken.api.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.LedgerInfoParams;
import dev.andstuff.kraken.api.endpoint.account.response.LedgerInfo;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

public class LedgerInfoEndpoint extends PrivateEndpoint<LedgerInfo> {

    public LedgerInfoEndpoint(LedgerInfoParams params) {
        super("Ledgers", params, new TypeReference<>() {});
    }
}
