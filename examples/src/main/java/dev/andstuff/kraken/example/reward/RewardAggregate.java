package dev.andstuff.kraken.example.reward;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.andstuff.kraken.api.model.endpoint.account.response.LedgerEntry;
import lombok.Getter;

@Getter
public class RewardAggregate { // YearlyStakingRewards

    private final Set<String> assets;
    private final Set<Integer> years;
    private final Set<AssetRewards> assetRewards;

    public RewardAggregate(List<LedgerEntry> rewards) {

        assets = rewards.stream().map(LedgerEntry::underlyingAsset).collect(toSet());
        years = rewards.stream().map(RewardAggregate::extractYear).collect(toSet());

        assetRewards = rewards.stream()
                .collect(groupingBy(LedgerEntry::underlyingAsset))
                .entrySet().stream()
                .map(entry -> new AssetRewards(entry.getKey(), entry.getValue()))
                .collect(toSet());
    }

    @Getter
    public static class AssetRewards {

        private final String asset;
        private final BigDecimal totalReward;
        private final Map<Integer, BigDecimal> yearlyRewards;

        public AssetRewards(String asset, List<LedgerEntry> rewards) {
            this.asset = asset;

            this.totalReward = rewards.stream()
                    .map(LedgerEntry::netAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            this.yearlyRewards = rewards.stream()
                    .collect(toMap(
                            RewardAggregate::extractYear,
                            LedgerEntry::netAmount,
                            BigDecimal::add));
        }
    }

    private static int extractYear(LedgerEntry ledgerEntry) {
        return ledgerEntry.time().atZone(ZoneId.of("UTC")).getYear();
    }
}
