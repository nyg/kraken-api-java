package dev.andstuff.kraken.api.rest;

/**
 * Provides the nonce sent with every private endpoint request. Kraken requires it to be ever-increasing for a given API key.
 */
public interface KrakenNonceGenerator {

    /**
     * Returns the nonce of the next private endpoint request.
     *
     * @return the nonce, which must be greater than the previous one
     */
    String generate();
}
