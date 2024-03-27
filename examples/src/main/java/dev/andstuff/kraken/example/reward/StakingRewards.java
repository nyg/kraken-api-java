package dev.andstuff.kraken.example.reward;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Set;

import dev.andstuff.kraken.api.model.endpoint.account.response.LedgerEntry;
import lombok.Getter;

/**
 * Aggregates staking rewards by asset.
 */
@Getter
public class StakingRewards {

    private final Set<String> assets;
    private final Set<Integer> years;
    private final Set<AssetRewards> assetRewards;

    public StakingRewards(List<LedgerEntry> rewards) {
        assets = rewards.stream().map(LedgerEntry::underlyingAsset).collect(toSet());
        years = rewards.stream().map(LedgerEntry::year).collect(toSet());

        assetRewards = rewards.stream()
                .collect(groupingBy(LedgerEntry::underlyingAsset))
                .entrySet().stream()
                .map(entry -> new AssetRewards(entry.getKey(), entry.getValue()))
                .collect(toSet());
    }
}
