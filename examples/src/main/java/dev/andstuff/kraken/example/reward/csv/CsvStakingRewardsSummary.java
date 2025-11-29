package dev.andstuff.kraken.example.reward.csv;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
        List<String> headerCells = new ArrayList<>();
        headerCells.add("Asset");
        headerCells.addAll(buildYearlyHeaders(years));
        headerCells.add("Total");
        headerCells.add(AssetRates.REFERENCE_ASSET);
        return headerCells.toArray(String[]::new);
    }

    private static List<String> buildYearlyHeaders(Set<Integer> years) {
        return years.stream()
                .flatMap(year -> Stream.of(year.toString(), AssetRates.REFERENCE_ASSET))
                .toList();
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
                    List<String> cells = new ArrayList<>();
                    cells.add(entry.getKey());
                    cells.addAll(entry.getValue().stream().map(BigDecimal::toPlainString).toList());
                    return cells.toArray(String[]::new);
                })
                .sorted(comparing(row -> new BigDecimal(row[row.length - 1]), reverseOrder()))
                .toList();
    }

    private String[] buildFooterRow(StakingRewards rewards, AssetRates rates) {
        List<String> footerCells = new ArrayList<>();
        footerCells.add("Total");
        footerCells.addAll(buildYearlyTotals(rewards, rates));
        footerCells.add("");
        footerCells.add(rewards.totalFiatAmount(rates).toPlainString());
        return footerCells.toArray(String[]::new);
    }

    private List<String> buildYearlyTotals(StakingRewards rewards, AssetRates rates) {
        return rewards.getYears().stream()
                .flatMap(year -> Stream.of("", rewards.totalFiatAmountFor(year, rates).toPlainString()))
                .toList();
    }
}
