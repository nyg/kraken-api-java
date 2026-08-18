package dev.andstuff.kraken.api.rest;

/**
 * The default {@link KrakenNonceGenerator}, using the current time in milliseconds since the epoch.
 */
public class EpochBasedNonceGenerator implements KrakenNonceGenerator {

    @Override
    public String generate() {
        return Long.toString(System.currentTimeMillis());
    }
}
