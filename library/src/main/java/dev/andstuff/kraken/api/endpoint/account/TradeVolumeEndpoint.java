package dev.andstuff.kraken.api.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.TradeVolumeParams;
import dev.andstuff.kraken.api.endpoint.account.response.TradeVolume;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code TradeVolume} endpoint for trade volume.
 */
public class TradeVolumeEndpoint extends PrivateEndpoint<TradeVolume> {

    /**
     * Creates the {@code TradeVolume} endpoint with default options.
     */
    public TradeVolumeEndpoint() {
        this(TradeVolumeParams.builder().build());
    }

    /**
     * Creates the {@code TradeVolume} endpoint.
     *
     * @param params the request parameters
     */
    public TradeVolumeEndpoint(TradeVolumeParams params) {
        super("TradeVolume", params, new TypeReference<>() {});
    }

    /**
     * Returns the media type used by the {@code TradeVolume} request body.
     *
     * @return {@code application/json}
     */
    @Override
    public String getContentType() {
        return "application/json";
    }
}
