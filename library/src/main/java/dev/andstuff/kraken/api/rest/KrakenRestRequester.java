package dev.andstuff.kraken.api.rest;

import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;
import lombok.NonNull;

public interface KrakenRestRequester {

    <T> T execute(PublicEndpoint<T> endpoint);

    <T> T execute(PrivateEndpoint<T> endpoint, @NonNull KrakenCredentials credentials, @NonNull KrakenNonceGenerator nonceGenerator);
}
