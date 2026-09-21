package dev.andstuff.kraken.api.endpoint.trading.response;

import java.time.Duration;

/**
 * The token returned by {@code GetWebSocketsToken}, authenticating a connection to the Kraken WebSocket API.
 *
 * @param token the token; it must be used within 15 minutes and does not expire while a private subscription is maintained
 * @param expires the time after which the token expires
 */
public record WebSocketsToken(String token,
                              Duration expires) {}
