package dev.andstuff.kraken.example;

import static dev.andstuff.kraken.example.helper.CredentialsHelper.readFromFile;
import static java.util.function.Predicate.not;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.endpoint.KrakenException;
import dev.andstuff.kraken.api.endpoint.account.response.LedgerEntry;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import dev.andstuff.kraken.example.report.ReportFetcher;
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

    private final KrakenAPI api;

    public static void main(String[] args) {
        KrakenCredentials credentials = readFromFile("/api-keys.properties");
        new StakingRewardsSummaryExample(new KrakenAPI(credentials))
                .generate("rewards.csv", "rewards-summary.csv");
    }

    public void generate(String rewardsFileName, String rewardSummaryFileName) {
        ReportFetcher reportFetcher = new ReportFetcher(api);
        String reportId = reportFetcher.requestReport(Instant.now());
        List<LedgerEntry> ledgerEntries = reportFetcher.fetchReportData(reportId);
        reportFetcher.deleteReport(reportId);

        List<LedgerEntry> rewards = ledgerEntries.stream().filter(LedgerEntry::isStakingReward).toList();
        StakingRewards stakingRewards = new StakingRewards(rewards);
        AssetRates rates = fetchRatesFor(stakingRewards.getAssets());

        new CsvLedgerEntries(rewards).writeToFile(rewardsFileName);
        new CsvStakingRewardsSummary(stakingRewards, rates).writeToFile(rewardSummaryFileName);
    }

    private AssetRates fetchRatesFor(Set<String> assets) {
        try {
            return new AssetRates(api.ticker(assets.stream()
                    .map(asset -> asset + AssetRates.REFERENCE_ASSET)
                    .filter(not(AssetRates.REFERENCE_PAIR::equals))
                    .toList()));
        }
        catch (KrakenException e) {
            throw new IllegalStateException("Couldn't fetch rates", e);
        }
    }
}
