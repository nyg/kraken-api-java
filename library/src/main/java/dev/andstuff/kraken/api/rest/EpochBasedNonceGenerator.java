package dev.andstuff.kraken.api.rest;

public class EpochBasedNonceGenerator implements KrakenNonceGenerator {

    @Override
    public String generate() {
        return Long.toString(System.currentTimeMillis());
    }
}
