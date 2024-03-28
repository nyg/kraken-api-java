package dev.andstuff.kraken.example;

import static dev.andstuff.kraken.example.helper.CredentialsHelper.readFromFile;
import static java.util.function.Predicate.not;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import dev.andstuff.kraken.api.endpoint.KrakenException;
import dev.andstuff.kraken.api.endpoint.account.params.LedgerInfoParams;
import dev.andstuff.kraken.api.endpoint.account.response.LedgerEntry;
import dev.andstuff.kraken.api.endpoint.account.response.LedgerInfo;
import dev.andstuff.kraken.example.reward.AssetRates;
import dev.andstuff.kraken.example.reward.StakingRewards;
import dev.andstuff.kraken.example.reward.csv.CsvLedgerEntries;
import dev.andstuff.kraken.example.reward.csv.CsvStakingRewardsSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Generates a CSV file containing all the ledger entries corresponding to
 * staking rewards, and another CSV file containing the summary of staking
 * rewards earned each year for each asset.
 */
@Slf4j
@RequiredArgsConstructor
public class StakingRewardsSummaryExample {

    private static final int SLEEP_BETWEEN_API_CALLS = 2000;

    private final KrakenAPI api;

    public static void main(String[] args) {
        KrakenCredentials credentials = readFromFile("/api-keys.properties");
        new StakingRewardsSummaryExample(new KrakenAPI(credentials))
                .generate("rewards.csv", "rewards-summary.csv");
    }

    public void generate(String rewardsFileName, String rewardSummaryFileName) {
        List<LedgerEntry> rewards = fetchStakingRewards();
        StakingRewards stakingRewards = new StakingRewards(rewards);
        AssetRates rates = fetchRatesFor(stakingRewards.getAssets());

        new CsvLedgerEntries(rewards).writeToFile(rewardsFileName);
        new CsvStakingRewardsSummary(stakingRewards, rates).writeToFile(rewardSummaryFileName);
    }

    private List<LedgerEntry> fetchStakingRewards() {

        List<LedgerEntry> rewards = new ArrayList<>();
        LedgerInfoParams params = LedgerInfoParams.builder()
                .assetType(LedgerInfoParams.Type.STAKING)
                .withoutCount(true)
                .build();

        boolean hasNext = true;
        while (hasNext) {
            LedgerInfo ledgerInfo = api.ledgerInfo(params);
            params = params.withNextResultOffset();
            hasNext = ledgerInfo.hasNext();

            rewards.addAll(ledgerInfo.stakingRewards());
            log.info("Fetched {} staking rewards", rewards.size());

            try {
                Thread.sleep(SLEEP_BETWEEN_API_CALLS);
            }
            catch (InterruptedException e) {
                log.warn("Thread was interrupted");
                Thread.currentThread().interrupt();
            }
        }

        return rewards;
    }

    private AssetRates fetchRatesFor(Set<String> assets) {
        try {
            List<String> pairs = assets.stream()
                    .map(asset -> asset + AssetRates.REFERENCE_ASSET)
                    .filter(not(AssetRates.REFERENCE_PAIR::equals))
                    .toList();
            return new AssetRates(api.ticker(pairs));
        }
        catch (KrakenException e) {
            throw new RuntimeException("Couldn't fetch rates", e);
        }
    }
}
