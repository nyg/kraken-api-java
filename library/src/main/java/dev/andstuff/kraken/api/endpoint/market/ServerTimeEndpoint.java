package dev.andstuff.kraken.api.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.response.ServerTime;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

/**
 * The public {@code Time} endpoint, returning Kraken's server time.
 */
public class ServerTimeEndpoint extends PublicEndpoint<ServerTime> {

    /**
     * Creates the endpoint.
     */
    public ServerTimeEndpoint() {
        super("Time", new TypeReference<>() {});
    }
}
