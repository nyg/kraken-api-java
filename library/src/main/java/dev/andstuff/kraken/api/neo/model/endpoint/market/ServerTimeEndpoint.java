package dev.andstuff.kraken.api.neo.model.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.neo.model.endpoint.market.response.ServerTime;

public class ServerTimeEndpoint extends PublicEndpoint<ServerTime> {

    public ServerTimeEndpoint() {
        super("Time", new TypeReference<>() {});
    }
}
