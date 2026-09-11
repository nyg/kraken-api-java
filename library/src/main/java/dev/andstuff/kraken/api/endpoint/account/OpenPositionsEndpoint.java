package dev.andstuff.kraken.api.endpoint.account;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.OpenPositionsParams;
import dev.andstuff.kraken.api.endpoint.account.response.OpenPosition;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code OpenPositions} endpoint for open positions.
 */
public class OpenPositionsEndpoint extends PrivateEndpoint<Map<String, OpenPosition>> {

    /**
     * Creates the {@code OpenPositions} endpoint with default options.
     */
    public OpenPositionsEndpoint() {
        this(OpenPositionsParams.builder().build());
    }

    /**
     * Creates the {@code OpenPositions} endpoint.
     *
     * @param params the request parameters
     */
    public OpenPositionsEndpoint(OpenPositionsParams params) {
        super("OpenPositions", params, new TypeReference<>() {});
    }
}
