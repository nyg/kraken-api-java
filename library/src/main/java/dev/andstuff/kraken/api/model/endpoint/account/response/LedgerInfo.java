package dev.andstuff.kraken.api.model.endpoint.account.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LedgerInfo(@JsonProperty("ledger") Map<String, LedgerEntry> entries,
                         int count) {

    public boolean hasNext() {
        return entries.size() == 50;
    }

    public List<LedgerEntry> asList() {
        return entries.entrySet().stream()
                .map(entry -> entry.getValue().withId(entry.getKey()))
                .toList();
    }
}
