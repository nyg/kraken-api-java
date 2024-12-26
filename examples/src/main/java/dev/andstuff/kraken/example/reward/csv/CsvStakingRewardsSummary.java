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
import dev.andstuff.kraken.example.reward.AssetRewards;
import dev.andstuff.kraken.example.reward.StakingRewards;

public class CsvStakingRewardsSummary {

    private final String[] headerRow;
    private final List<String[]> assetRewardRows;
    private final String[] footerRow;

    public CsvStakingRewardsSummary(StakingRewards rewards, AssetRates rates) {
        this.headerRow = buildHeaderRow(rewards.getYears());
        this.assetRewardRows = buildRewardRows(rewards, rates);
        this.footerRow = buildFooterRow(rewards, rates);
    }

    public void writeToFile(String fileName) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeNext(headerRow);
            writer.writeAll(assetRewardRows);
            writer.writeNext(footerRow);
        }
        catch (IOException e) {
            throw new IllegalStateException("Couldn't write reward summary to file", e);
        }
    }

    /**
     * Build header row, adding columns for fiat valuation.
     *
     * @return an array of String: Asset, y1, _, y2, _, …, total, _
     */
    private static String[] buildHeaderRow(Set<Integer> years) {
        List<String> headerCells = years.stream()
                .flatMap(year -> Stream.of(year.toString(), AssetRates.REFERENCE_ASSET))
                .collect(toList());
        headerCells.addFirst("Asset");
        headerCells.addAll(List.of("Total", AssetRates.REFERENCE_ASSET));
        return headerCells.toArray(new String[0]);
    }

    /**
     * Build reward rows with fiat valuation.
     */
    private static List<String[]> buildRewardRows(StakingRewards rewards, AssetRates rates) {
        return rewards.getAssetRewards().stream()
                .collect(toMap(
                        AssetRewards::getAsset,
                        assetReward -> {
                            Map<Integer, BigDecimal> yearlyRewards = assetReward.getYearlyRewards();
                            List<BigDecimal> yearlyRewardsWithRates = rewards.getYears().stream()
                                    .flatMap(year -> {
                                        BigDecimal reward = yearlyRewards.getOrDefault(year, BigDecimal.ZERO);
                                        BigDecimal fiatValue = rates.evaluate(reward, assetReward.getAsset());
                                        return Stream.of(reward, fiatValue);
                                    })
                                    .collect(toList());
                            BigDecimal totalReward = assetReward.getTotalReward();
                            yearlyRewardsWithRates.addAll(
                                    List.of(totalReward, rates.evaluate(totalReward, assetReward.getAsset())));
                            return yearlyRewardsWithRates;
                        }))
                .entrySet().stream()
                .map(entry -> {
                    List<String> cells = entry.getValue().stream().map(BigDecimal::toPlainString).collect(toList());
                    cells.addFirst(entry.getKey());
                    return cells.toArray(new String[0]);
                })
                .sorted(comparing(e -> new BigDecimal(e[e.length - 1]), reverseOrder()))
                .toList();
    }

    private String[] buildFooterRow(StakingRewards rewards, AssetRates rates) {

        BigDecimal totalFiatAmount = rewards.getAssetRewards().stream()
                .map(reward -> rates.evaluate(reward.getTotalReward(), reward.getAsset()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<String> footerCells = rewards.getYears().stream().flatMap(year -> Stream.of("", "")).collect(toList());
        footerCells.addFirst("Total");
        footerCells.addAll(List.of("", totalFiatAmount.toPlainString()));
        return footerCells.toArray(new String[0]);
    }
}
