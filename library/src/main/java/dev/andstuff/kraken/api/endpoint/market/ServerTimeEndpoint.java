package dev.andstuff.kraken.api.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.response.ServerTime;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

public class ServerTimeEndpoint extends PublicEndpoint<ServerTime> {

    public ServerTimeEndpoint() {
        super("Time", new TypeReference<>() {});
    }
}
