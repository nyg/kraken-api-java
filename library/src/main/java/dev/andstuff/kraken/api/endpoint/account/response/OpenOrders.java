package dev.andstuff.kraken.api.endpoint.account.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The open orders returned by {@code OpenOrders}.
 *
 * @param open the open
 */
public record OpenOrders(Map<String, Order> open) {}
