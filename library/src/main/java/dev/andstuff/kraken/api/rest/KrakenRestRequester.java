package dev.andstuff.kraken.api.rest;

import dev.andstuff.kraken.api.model.KrakenCredentials;
import dev.andstuff.kraken.api.model.endpoint.priv.PrivateEndpoint;
import dev.andstuff.kraken.api.model.endpoint.pub.PublicEndpoint;
import lombok.NonNull;

public interface KrakenRestRequester {

    <T> T execute(PublicEndpoint<T> endpoint);

    <T> T execute(PrivateEndpoint<T> endpoint, @NonNull KrakenCredentials credentials);
}
