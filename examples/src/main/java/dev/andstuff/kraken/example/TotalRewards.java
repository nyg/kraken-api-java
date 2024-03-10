package dev.andstuff.kraken.example;

import static dev.andstuff.kraken.example.PropertiesHelper.readFromFile;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;

import dev.andstuff.kraken.api.KrakenAPI;

/**
 * TODO Group by year
 */
public class TotalRewards {

    public static void main(String[] args) throws IOException, InterruptedException {

        Properties apiKeys = readFromFile("/api-keys.properties");
        KrakenAPI api = new KrakenAPI(apiKeys.getProperty("key"), apiKeys.getProperty("secret"));

        Map<String, String> params = new HashMap<>();
        params.put("type", "staking");
        params.put("without_count", "true");
        params.put("ofs", "0");

        Map<String, JsonNode> rewards = new HashMap<>();

        boolean hasNext = true;
        while (hasNext) {

            JsonNode response = api.query(KrakenAPI.Private.LEDGERS, params);
            params.put("ofs", String.valueOf(Integer.parseInt(params.get("ofs")) + 50));
            System.out.printf("Fetched %s rewards%n", params.get("ofs"));

            JsonNode ledgerEntries = response.findValue("result").findValue("ledger");
            Iterator<Map.Entry<String, JsonNode>> fields = ledgerEntries.fields();
            hasNext = ledgerEntries.size() == 50;

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> rewardEntry = fields.next();
                rewards.put(rewardEntry.getKey(), rewardEntry.getValue());
            }

            Thread.sleep(2000);
        }

        Map<String, List<JsonNode>> groupedRewards = rewards.values()
                .stream()
                .collect(groupingBy(e -> {
                    String asset = e.findValue("asset").textValue();
                    return asset.equals("XETH") ? "ETH" : asset.split("[0-9.]")[0];
                }));

        String fileName = "rewards.txt";
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        PrintStream printStream = new PrintStream(fileOutputStream);
        System.setOut(printStream);

        BigDecimal totalRewardAmountUsd = BigDecimal.ZERO;
        for (String asset : groupedRewards.keySet()) {
            List<JsonNode> assetRewards = groupedRewards.get(asset).stream()
                    .filter(e -> !asList("migration", "spottostaking").contains(e.findValue("subtype").textValue()))
                    .sorted(comparing(e -> e.get("time").asInt()))
                    .toList();

            BigDecimal assetTotalRewardAmount = assetRewards.stream()
                    .map(e -> new BigDecimal(e.findValue("amount").textValue())
                            .subtract(new BigDecimal(e.findValue("fee").textValue())))
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
                    reward.get("asset").textValue(),
                    Instant.ofEpochSecond(reward.get("time").asLong()),
                    new BigDecimal(reward.get("amount").textValue()),
                    new BigDecimal(reward.get("fee").textValue()),
                    reward.get("subtype").textValue()));
        }

        System.out.println();
        System.out.printf("Total USD: %s%n", totalRewardAmountUsd);
    }

    private static BigDecimal fetchRate(String asset, KrakenAPI api) {
        try {
            Map<String, String> tickerParams = new HashMap<>();
            tickerParams.put("pair", asset + "USD");

            JsonNode tickerResponse = api.query(KrakenAPI.Public.TICKER, tickerParams).findValue("result");
            return new BigDecimal(tickerResponse.findValue(tickerResponse.fieldNames().next()).findValue("c").get(0).textValue());
        }
        catch (Exception e) {
            System.err.printf("Couldn't fetch rate for %s%n", asset);
            return BigDecimal.ONE.negate();
        }
    }
}
