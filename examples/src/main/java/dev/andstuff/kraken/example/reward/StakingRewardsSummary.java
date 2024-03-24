package dev.andstuff.kraken.example.reward;

import static dev.andstuff.kraken.example.helper.PropertiesHelper.readFromFile;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.model.KrakenException;
import dev.andstuff.kraken.api.model.endpoint.account.params.LedgerInfoParams;
import dev.andstuff.kraken.api.model.endpoint.account.response.LedgerEntry;
import dev.andstuff.kraken.api.model.endpoint.account.response.LedgerInfo;
import dev.andstuff.kraken.example.reward.csv.CsvLedgerEntries;
import dev.andstuff.kraken.example.reward.csv.CsvYearlyAssetRewards;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class StakingRewardsSummary {

    private final KrakenAPI api;

    public static void main(String[] args) {
        Properties apiKeys = readFromFile("/api-keys.properties");
        new StakingRewardsSummary(new KrakenAPI(apiKeys.getProperty("key"), apiKeys.getProperty("secret"))).generate();
    }

    public void generate() {
        List<LedgerEntry> rewards = fetchAllStakingRewards();
        RewardAggregate rewardAggregate = new RewardAggregate(rewards);
        AssetRates rates = fetchRatesFor(rewardAggregate.getAssets());

        new CsvLedgerEntries(rewards).writeToFile("rewards.csv");
        new CsvYearlyAssetRewards(rewardAggregate, rates).writeToFile("rewards-summary.csv");
    }

    private List<LedgerEntry> fetchAllStakingRewards() {

        List<LedgerEntry> rewards = new ArrayList<>();
        LedgerInfoParams params = LedgerInfoParams.builder()
                .assetType(LedgerInfoParams.Type.STAKING)
                .withoutCount(true)
                .build();

        boolean hasNext = true;
        while (hasNext) {

            LedgerInfo ledgerInfo = api.ledgerInfo(params);
            List<LedgerEntry> ledgerEntries = ledgerInfo.asList();
            log.info("Fetched {} rewards", ledgerEntries.size());

            params = params.withNextResultOffset();
            hasNext = ledgerInfo.hasNext();

            rewards.addAll(ledgerEntries.stream().filter(StakingRewardsSummary::isStakingReward).toList());

            try {
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {
                log.warn("Thread has been interrupted");
                Thread.currentThread().interrupt();
            }
        }

        return rewards;
    }

    private AssetRates fetchRatesFor(Set<String> assets) {
        try {
            List<String> pairs = assets.stream()
                    .map(asset -> asset + AssetRates.REFERENCE_ASSET)
                    .filter(pair -> !pair.equals("USD" + AssetRates.REFERENCE_ASSET))
                    .toList();
            return new AssetRates(api.ticker(pairs));
        }
        catch (KrakenException e) {
            throw new RuntimeException("Couldn't fetch rates", e);
        }
    }

    private static boolean isStakingReward(LedgerEntry entry) {
        return !asList("migration", "spottostaking").contains(entry.subType());
    }
}
