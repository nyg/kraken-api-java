package dev.andstuff.kraken.example.reward;

import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import dev.andstuff.kraken.api.model.endpoint.account.response.LedgerEntry;
import lombok.Getter;

/**
 * Aggregates asset rewards by year.
 */
@Getter
public class AssetRewards {

    private final String asset;
    private final BigDecimal totalReward;
    private final Map<Integer, BigDecimal> yearlyRewards;

    public AssetRewards(String asset, List<LedgerEntry> rewards) {
        this.asset = asset;

        this.totalReward = rewards.stream()
                .map(LedgerEntry::netAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.yearlyRewards = rewards.stream()
                .collect(toMap(LedgerEntry::year, LedgerEntry::netAmount, BigDecimal::add));
    }
}
