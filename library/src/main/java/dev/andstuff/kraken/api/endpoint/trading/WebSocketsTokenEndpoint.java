package dev.andstuff.kraken.api.endpoint.trading;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.response.WebSocketsToken;

/**
 * The private {@code GetWebSocketsToken} endpoint, returning a token to authenticate with the Kraken WebSocket API.
 */
public class WebSocketsTokenEndpoint extends PrivateEndpoint<WebSocketsToken> {

    /**
     * Creates the {@code GetWebSocketsToken} endpoint.
     */
    public WebSocketsTokenEndpoint() {
        super("GetWebSocketsToken", new TypeReference<>() {});
    }
}
