package dev.andstuff.kraken.example;

import static dev.andstuff.kraken.example.PropertiesHelper.readFromFile;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.model.KrakenException;
import dev.andstuff.kraken.api.model.endpoint.account.params.LedgerInfoParams;
import dev.andstuff.kraken.api.model.endpoint.account.response.LedgerEntry;
import dev.andstuff.kraken.api.model.endpoint.account.response.LedgerInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO
 * - Group by year
 * - One csv file with staking  rewards
 * - Another with summary (asset, years, total)
 */
@Slf4j
public class TotalRewards {

    public static void main(String[] args) throws IOException, InterruptedException {

        Properties apiKeys = readFromFile("/api-keys.properties");
        KrakenAPI api = new KrakenAPI(apiKeys.getProperty("key"), apiKeys.getProperty("secret"));

        LedgerInfoParams params = LedgerInfoParams.builder()
                .assetType(LedgerInfoParams.Type.STAKING)
                .withoutCount(true)
                .build();

        Map<String, LedgerEntry> rewards = new HashMap<>();

        boolean hasNext = true;
        while (hasNext) {

            LedgerInfo ledger = api.ledgerInfo(params);
            params = params.withNextResultOffset();

            int rewardCount = ledger.entries().size();
            log.info("Fetched {} rewards", rewardCount);
            hasNext = rewardCount == 50;

            rewards.putAll(ledger.entries());
            Thread.sleep(2000);
        }

        Map<String, List<LedgerEntry>> rewardsByAsset = rewards
                .values().stream()
                .collect(groupingBy(TotalRewards::extractAsset));

        String fileName = "rewards.txt";
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        PrintStream printStream = new PrintStream(fileOutputStream);
        System.setOut(printStream);

        BigDecimal totalRewardAmountUsd = BigDecimal.ZERO;
        for (String asset : rewardsByAsset.keySet()) {
            List<LedgerEntry> assetRewards = rewardsByAsset.get(asset).stream()
                    .filter(e -> !asList("migration", "spottostaking").contains(e.subType()))
                    .sorted(comparing(LedgerEntry::time))
                    .toList();

            BigDecimal assetTotalRewardAmount = assetRewards.stream()
                    .map(LedgerEntry::netAmount)
                    .reduce(BigDecimal::add)
                    .orElse(BigDecimal.ONE.negate());
            BigDecimal assetRate = fetchRate(asset, api);
            BigDecimal assetTotalRewardAmountUsd = assetTotalRewardAmount.multiply(assetRate);
            totalRewardAmountUsd = totalRewardAmountUsd.add(assetTotalRewardAmountUsd);

            System.out.println();
            System.out.printf("Asset: %s, reward count: %s, total rewards: %s, USD: %s%n",
                    asset, assetRewards.size(), assetTotalRewardAmount, assetTotalRewardAmountUsd);
            System.out.println("=================================================================");

            assetRewards.forEach(reward -> System.out.printf("%-10s %s %16s %16s %s%n",
                    reward.asset(), reward.time(), reward.amount(), reward.fee(), reward.subType()));
        }

        System.out.println();
        System.out.printf("Total USD: %s%n", totalRewardAmountUsd);
    }

    private static BigDecimal fetchRate(String asset, KrakenAPI api) {
        try {
            String referenceAsset = "USD";
            return asset.equals(referenceAsset)
                    ? BigDecimal.ONE
                    : api.ticker(List.of(asset + referenceAsset))
                            .values().stream()
                            .findAny().orElseThrow()
                            .lastTrade().price();
        }
        catch (KrakenException e) {
            log.error("Couldn't fetch rate for {}", asset);
            return BigDecimal.ONE.negate();
        }
    }

    private static String extractAsset(LedgerEntry ledgerEntry) {
        String rawAsset = ledgerEntry.asset();
        boolean originalAsset = rawAsset.matches("^([XZ])([A-Z]{3})$");
        return originalAsset ? rawAsset.substring(1, 4) : rawAsset.split("[0-9.]")[0];
    }
}
