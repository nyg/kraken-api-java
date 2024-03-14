package dev.andstuff.kraken.api.model.endpoint.account.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LedgerInfo(@JsonProperty("ledger") Map<String, LedgerEntry> entries,
                         int count) {}
