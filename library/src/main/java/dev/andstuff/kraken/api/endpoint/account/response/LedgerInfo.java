package dev.andstuff.kraken.api.endpoint.account.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The response of the {@code Ledgers} endpoint.
 *
 * @param entries the ledger entries of the page, by identifier, at most 50 of them
 * @param count the total number of entries matching the request, all pages included
 */
public record LedgerInfo(@JsonProperty("ledger") Map<String, LedgerEntry> entries,
                         int count) {

    /**
     * Returns the entries as a list, each entry carrying the identifier Kraken returned it under.
     *
     * @return the ledger entries
     */
    public List<LedgerEntry> asList() {
        return entries.entrySet().stream()
                .map(entry -> entry.getValue().withId(entry.getKey()))
                .toList();
    }

    /**
     * Returns the entries of the page that are staking or earn rewards.
     *
     * @return the reward entries
     */
    public List<LedgerEntry> stakingRewards() {
        return asList().stream().filter(LedgerEntry::isStakingReward).toList();
    }

    /**
     * Returns whether another page of entries may be available, i.e. whether this one is full.
     *
     * @return whether another page may follow
     */
    public boolean hasNext() {
        return entries.size() == 50;
    }

    /**
     * Returns the number of entries in this page.
     *
     * @return the number of entries
     */
    public int size() {
        return entries.size();
    }
}
