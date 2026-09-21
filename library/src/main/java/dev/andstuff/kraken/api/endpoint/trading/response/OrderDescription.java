package dev.andstuff.kraken.api.endpoint.trading.response;

/**
 * The order description returned by {@code AddOrder}, {@code AddOrderBatch} and {@code EditOrder}.
 *
 * @param order the order description, e.g. {@code buy 1.45 XBTUSD @ limit 27500.0}
 * @param close the conditional close order description, null without conditional close
 */
public record OrderDescription(String order,
                               String close) {}
