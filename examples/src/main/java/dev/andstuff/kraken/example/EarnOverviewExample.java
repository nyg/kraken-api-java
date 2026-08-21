package dev.andstuff.kraken.example;

import static dev.andstuff.kraken.example.helper.CredentialsHelper.readFromFile;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.endpoint.earn.response.EarnStrategies;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import dev.andstuff.kraken.example.earn.EarnOverview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Prints where the assets of an account are earning, i.e. which earn strategy
 * holds them and what it has paid so far, and which spot balances could still
 * be allocated to a strategy that accepts them.
 * <p>
 * <i>Note:</i> read-only API key permissions are enough, the example never
 * allocates or deallocates anything.
 */
@Slf4j
@RequiredArgsConstructor
public class EarnOverviewExample {

    private static final int TOP_STRATEGIES_PER_ASSET = 3;
    private static final DecimalFormat NATIVE_FORMAT = new DecimalFormat("#,##0.####", DecimalFormatSymbols.getInstance(Locale.ROOT));
    private static final DecimalFormat CONVERTED_FORMAT = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.ROOT));
    private static final DecimalFormat PRECISE_FORMAT = new DecimalFormat("#,##0.########", DecimalFormatSymbols.getInstance(Locale.ROOT));
    private static final DecimalFormat APR_FORMAT = new DecimalFormat("0.000", DecimalFormatSymbols.getInstance(Locale.ROOT));

    private final KrakenAPI api;

    static void main() {
        KrakenCredentials credentials = readFromFile("/api-keys.properties");
        new EarnOverviewExample(new KrakenAPI(credentials)).print();
    }

    public void print() {
        EarnOverview.Report report = new EarnOverview(api).generate();

        log.info("Allocated {} {}, earned {} {} since the creation of the account",
                converted(report.totalAllocated()), report.convertedAsset(),
                converted(report.totalRewarded()), report.convertedAsset());

        log.info("");
        log.info("Assets currently earning");
        log.info("{}", "-".repeat(120));
        log.info(String.format("%-6s %-22s %-8s %-9s %-16s %18s %14s %12s %12s",
                "ASSET", "STRATEGY", "LOCK", "APR", "YIELD", "ALLOCATED", "VALUE", "EARNED", "PERIOD"));

        report.allocations().forEach(allocation -> log.info(String.format("%-6s %-22s %-8s %-9s %-16s %18s %14s %12s %12s",
                allocation.asset(),
                strategyId(allocation),
                allocation.lockType(),
                aprOf(allocation),
                allocation.yieldSource(),
                preciseAmount(allocation.nativeAmount()),
                converted(allocation.convertedAmount()),
                converted(allocation.rewardedAmount()),
                converted(allocation.accruedReward()))));

        log.info("");
        log.info("Assets previously earning");
        log.info("{}", "-".repeat(120));
        log.info(String.format("%-6s %-22s %-8s %-9s %-16s %18s %12s",
                "ASSET", "STRATEGY", "LOCK", "APR", "YIELD", "REWARDS", "EARNED"));

        report.pastAllocations().forEach(allocation -> log.info(String.format("%-6s %-22s %-8s %-9s %-16s %18s %12s",
                allocation.asset(),
                strategyId(allocation),
                allocation.lockType(),
                aprOf(allocation),
                allocation.yieldSource(),
                preciseAmount(allocation.allocation().totalRewarded().nativeAmount()),
                converted(allocation.rewardedAmount()))));

        log.info("");
        log.info("Spot balances that could be allocated");
        log.info("{}", "-".repeat(120));

        report.opportunities().forEach(opportunity -> {
            log.info("{} {} available{}", amount(opportunity.spotBalance()), opportunity.asset(), earningInPlace(opportunity));
            opportunity.strategies().stream()
                    .limit(TOP_STRATEGIES_PER_ASSET)
                    .forEach(strategy -> log.info(String.format("    %-22s %-8s %-9s %-16s min %s %s",
                            strategy.id(),
                            strategy.lockType().type().toString().toLowerCase(),
                            apr(strategy),
                            strategy.yieldSource().type().toString().toLowerCase(),
                            amount(strategy.userMinAllocation()),
                            terms(strategy))));
            log.info("");
        });

        log.info("");
        log.info("Strategies of the flex lock type earn on the spot balance where it sits, no allocation needed.");
    }

    private static String strategyId(EarnOverview.StrategyAllocation allocation) {
        return allocation.strategy() == null ? allocation.allocation().strategyId() : allocation.strategy().id();
    }

    private static String aprOf(EarnOverview.StrategyAllocation allocation) {
        return allocation.apr() == null ? "?" : percentage(allocation.apr());
    }

    private static String earningInPlace(EarnOverview.Opportunity opportunity) {
        return Optional.ofNullable(opportunity.earningInPlace())
                .map(allocation -> ", already earning %s in flex strategy %s".formatted(percentage(allocation.apr()), allocation.strategy().id()))
                .orElse("");
    }

    private static String apr(EarnStrategies.Strategy strategy) {
        return Optional.ofNullable(strategy.aprEstimate()).map(estimate -> percentage(estimate.high())).orElse("?");
    }

    private static String percentage(BigDecimal rate) {
        return APR_FORMAT.format(rate) + "%";
    }

    private static String terms(EarnStrategies.Strategy strategy) {
        EarnStrategies.LockType lockType = strategy.lockType();

        List<String> terms = new ArrayList<>();
        Optional.ofNullable(lockType.durationMonths()).ifPresent(months -> terms.add("locked %d months".formatted(months)));
        Optional.ofNullable(lockType.bondingPeriod()).filter(period -> !period.isZero()).ifPresent(period -> terms.add("bonding " + days(period)));
        Optional.ofNullable(lockType.unbondingPeriod()).filter(period -> !period.isZero()).ifPresent(period -> terms.add("unbonding " + days(period)));
        Optional.ofNullable(lockType.payoutFrequency()).ifPresent(frequency -> terms.add("paid every " + days(frequency)));

        return String.join(", ", terms);
    }

    private static String days(Duration duration) {
        return duration.toDays() > 0 ? duration.toDays() + "d" : duration.toHours() + "h";
    }

    private static String amount(BigDecimal value) {
        return value == null ? "-" : NATIVE_FORMAT.format(value);
    }

    private static String preciseAmount(BigDecimal value) {
        return value == null ? "-" : PRECISE_FORMAT.format(value);
    }

    private static String converted(BigDecimal value) {
        return value == null ? "-" : CONVERTED_FORMAT.format(value);
    }
}
