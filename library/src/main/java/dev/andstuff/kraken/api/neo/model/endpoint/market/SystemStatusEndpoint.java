package dev.andstuff.kraken.api.neo.model.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.neo.model.endpoint.market.response.SystemStatus;

public class SystemStatusEndpoint extends PublicEndpoint<SystemStatus> {

    public SystemStatusEndpoint() {
        super("SystemStatus", new TypeReference<>() {});
    }
}
