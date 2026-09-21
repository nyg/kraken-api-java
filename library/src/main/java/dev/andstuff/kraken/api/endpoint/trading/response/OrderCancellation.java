package dev.andstuff.kraken.api.endpoint.trading.response;

/**
 * The cancellation returned by {@code CancelOrder}, {@code CancelAll} and {@code CancelOrderBatch}.
 *
 * @param count the number of orders cancelled
 * @param pending whether the orders are pending cancellation
 */
public record OrderCancellation(int count,
                                boolean pending) {}
