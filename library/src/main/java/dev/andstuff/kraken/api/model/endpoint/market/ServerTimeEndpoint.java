package dev.andstuff.kraken.api.model.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.model.endpoint.market.response.ServerTime;
import dev.andstuff.kraken.api.model.endpoint.pub.PublicEndpoint;

public class ServerTimeEndpoint extends PublicEndpoint<ServerTime> {

    public ServerTimeEndpoint() {
        super("Time", new TypeReference<>() {});
    }
}
