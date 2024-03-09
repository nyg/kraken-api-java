package dev.andstuff.kraken.api.neo.rest;

import dev.andstuff.kraken.api.neo.model.endpoint.Endpoint;

public interface KrakenRestRequester {

    <T> T execute(Endpoint<T> endpoint);
}
