package dev.andstuff.kraken.api.endpoint.trading.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The order returned by {@code AddOrder}.
 *
 * @param description the order description
 * @param transactionIds the Kraken identifiers of the order, absent when the order was only validated
 */
public record OrderAdded(@JsonProperty("descr") OrderDescription description,
                         @JsonProperty("txid") List<String> transactionIds) {}
