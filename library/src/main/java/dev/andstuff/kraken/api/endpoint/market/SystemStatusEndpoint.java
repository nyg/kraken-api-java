package dev.andstuff.kraken.api.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.response.SystemStatus;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

public class SystemStatusEndpoint extends PublicEndpoint<SystemStatus> {

    public SystemStatusEndpoint() {
        super("SystemStatus", new TypeReference<>() {});
    }
}
