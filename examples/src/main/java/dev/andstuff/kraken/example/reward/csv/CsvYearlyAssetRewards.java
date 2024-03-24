package dev.andstuff.kraken.example.reward.csv;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.opencsv.CSVWriter;

import dev.andstuff.kraken.example.reward.AssetRates;
import dev.andstuff.kraken.example.reward.RewardAggregate;

public class CsvYearlyAssetRewards {

    private final List<String> headers;
    private final Map<String, List<BigDecimal>> assetRewardRows;

    public CsvYearlyAssetRewards(RewardAggregate aggregate, AssetRates rates) {

        // build header row - adding columns for fiat valuation
        Set<Integer> years = aggregate.getYears();
        headers = years.stream().flatMap(e -> Stream.of(e.toString(), "")).collect(toList());
        headers.addFirst("Asset");
        headers.addAll(List.of("Total", ""));

        // build asset reward rows with rates
        Set<RewardAggregate.AssetRewards> assetRewards = aggregate.getAssetRewards();
        assetRewardRows = assetRewards.stream()
                .collect(toMap(
                        RewardAggregate.AssetRewards::getAsset,
                        assetReward -> {
                            Map<Integer, BigDecimal> yearlyRewards = assetReward.getYearlyRewards();
                            List<BigDecimal> yearlyRewardsWithRates = years.stream()
                                    .flatMap(year -> {
                                        BigDecimal reward = yearlyRewards.getOrDefault(year, BigDecimal.ZERO);
                                        BigDecimal fiatValue = rates.evaluate(reward, assetReward.getAsset());
                                        return Stream.of(reward, fiatValue);
                                    })
                                    .collect(toList());
                            BigDecimal totalReward = assetReward.getTotalReward();
                            yearlyRewardsWithRates.addAll(List.of(totalReward, rates.evaluate(totalReward, assetReward.getAsset())));
                            return yearlyRewardsWithRates;
                        }));
    }

    public void writeToFile(String fileName) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeNext(headers.toArray(new String[0])); // Asset, y1, y2, y3, ..., total

            List<String[]> array = assetRewardRows.entrySet().stream()
                    .map(entry -> {
                        List<String> collect = entry.getValue().stream().map(BigDecimal::toPlainString).collect(toList());
                        collect.addFirst(entry.getKey());
                        return collect.toArray(new String[0]);
                    })
                    .sorted(comparing(e -> new BigDecimal(e[e.length - 1]), reverseOrder()))
                    .toList();

            writer.writeAll(array); // ETH, xxx, xxx, xxx
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
