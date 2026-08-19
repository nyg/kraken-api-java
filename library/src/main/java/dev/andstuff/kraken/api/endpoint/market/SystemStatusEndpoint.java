package dev.andstuff.kraken.api.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.response.SystemStatus;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

/**
 * The public {@code SystemStatus} endpoint, returning the current status of the Kraken trading system.
 */
public class SystemStatusEndpoint extends PublicEndpoint<SystemStatus> {

    /**
     * Creates the endpoint.
     */
    public SystemStatusEndpoint() {
        super("SystemStatus", new TypeReference<>() {});
    }
}
