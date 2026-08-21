package dev.andstuff.kraken.example.earn;

import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.endpoint.earn.response.EarnAllocations;
import dev.andstuff.kraken.api.endpoint.earn.response.EarnStrategies;
import lombok.RequiredArgsConstructor;

/**
 * Crosses the earn allocations of an account with the strategies Kraken offers
 * and with the spot balances, to tell where the assets of the account are
 * earning and what else could be allocated.
 */
@RequiredArgsConstructor
public class EarnOverview {

    private static final String FEE_ASSET = "KFEE";

    private final KrakenAPI api;

    public Report generate() {
        List<EarnStrategies.Strategy> strategies = api.earnStrategies().items();
        EarnAllocations allocations = api.earnAllocations();

        Map<String, EarnStrategies.Strategy> strategiesById = strategies.stream()
                .collect(toMap(EarnStrategies.Strategy::id, identity()));

        List<ActiveAllocation> active = allocations.items().stream()
                .filter(allocation -> isAllocated(allocation.amountAllocated().total()))
                .map(allocation -> new ActiveAllocation(allocation, strategiesById.get(allocation.strategyId())))
                .sorted(comparing(ActiveAllocation::convertedAmount).reversed())
                .toList();

        List<Opportunity> opportunities = spotBalances().entrySet().stream()
                .map(balance -> new Opportunity(
                        balance.getKey(),
                        balance.getValue(),
                        allocatableStrategiesFor(strategies, balance.getKey(), balance.getValue()),
                        flexAllocationOf(active, balance.getKey())))
                .filter(opportunity -> !opportunity.strategies().isEmpty())
                .sorted(comparing(Opportunity::bestApr).reversed())
                .toList();

        return new Report(allocations.convertedAsset(), allocations.totalAllocated(), allocations.totalRewarded(), active, opportunities);
    }

    private Map<String, BigDecimal> spotBalances() {
        JsonNode balance = api.query(KrakenAPI.Private.BALANCE);

        return balance.properties().stream()
                .filter(asset -> !AssetCodes.isEarnWallet(asset.getKey()))
                .filter(asset -> !FEE_ASSET.equals(asset.getKey()))
                .filter(asset -> isPositive(new BigDecimal(asset.getValue().asText())))
                .collect(toMap(asset -> AssetCodes.normalize(asset.getKey()), asset -> new BigDecimal(asset.getValue().asText()), BigDecimal::add));
    }

    private static List<EarnStrategies.Strategy> allocatableStrategiesFor(List<EarnStrategies.Strategy> strategies, String asset, BigDecimal spotBalance) {
        return strategies.stream()
                .filter(EarnStrategies.Strategy::canAllocate)
                .filter(strategy -> asset.equals(AssetCodes.normalize(strategy.asset())))
                .filter(strategy -> reaches(spotBalance, strategy.userMinAllocation()))
                .sorted(comparing(EarnOverview::aprOf).reversed())
                .toList();
    }

    private static ActiveAllocation flexAllocationOf(List<ActiveAllocation> allocations, String asset) {
        return allocations.stream()
                .filter(allocation -> asset.equals(AssetCodes.normalize(allocation.asset())))
                .filter(allocation -> "flex".equals(allocation.lockType()))
                .findFirst()
                .orElse(null);
    }

    private static boolean reaches(BigDecimal spotBalance, BigDecimal minAllocation) {
        return minAllocation == null || spotBalance.compareTo(minAllocation) >= 0;
    }

    private static BigDecimal aprOf(EarnStrategies.Strategy strategy) {
        return Optional.ofNullable(strategy.aprEstimate())
                .map(EarnStrategies.AprEstimate::high)
                .orElse(BigDecimal.ZERO);
    }

    private static boolean isAllocated(EarnAllocations.Amount amount) {
        return isPositive(amount.nativeAmount()) && (amount.converted() == null || isPositive(amount.converted()));
    }

    private static boolean isPositive(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public record Report(String convertedAsset,
                         BigDecimal totalAllocated,
                         BigDecimal totalRewarded,
                         List<ActiveAllocation> allocations,
                         List<Opportunity> opportunities) {}

    public record ActiveAllocation(EarnAllocations.Allocation allocation,
                                   EarnStrategies.Strategy strategy) {

        public String asset() {
            return allocation.nativeAsset();
        }

        public BigDecimal nativeAmount() {
            return allocation.amountAllocated().total().nativeAmount();
        }

        public BigDecimal convertedAmount() {
            return allocation.amountAllocated().total().converted();
        }

        public BigDecimal rewardedAmount() {
            return allocation.totalRewarded().converted();
        }

        public BigDecimal accruedReward() {
            return Optional.ofNullable(allocation.payout())
                    .map(payout -> payout.accumulatedReward().converted())
                    .orElse(BigDecimal.ZERO);
        }

        public String lockType() {
            return Optional.ofNullable(strategy)
                    .map(found -> found.lockType().type().toString().toLowerCase())
                    .orElse("unknown");
        }

        public String apr() {
            return Optional.ofNullable(strategy).map(EarnOverview::aprOf).map(apr -> apr + "%").orElse("?");
        }

        public String yieldSource() {
            return Optional.ofNullable(strategy)
                    .map(found -> found.yieldSource().type().toString().toLowerCase())
                    .orElse("unknown");
        }
    }

    public record Opportunity(String asset,
                              BigDecimal spotBalance,
                              List<EarnStrategies.Strategy> strategies,
                              ActiveAllocation earningInPlace) {

        public BigDecimal bestApr() {
            return strategies.stream().map(EarnOverview::aprOf).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        }
    }
}
