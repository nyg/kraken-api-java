package dev.andstuff.kraken.example.eoy;

import static java.util.Comparator.comparing;
import static java.util.function.Predicate.not;

import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.opencsv.CSVWriter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EoyBalanceSummary {

    private static final NumberFormat BALANCE_FORMATTER = NumberFormat.getNumberInstance();

    static {
        BALANCE_FORMATTER.setMinimumFractionDigits(0);
        BALANCE_FORMATTER.setMaximumFractionDigits(32);
    }

    private final EoyBalances eoyBalances;

    public void writeToFile(String fileName) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeNext(buildHeaderRow());
            writer.writeAll(buildRows());
        }
        catch (IOException e) {
            throw new IllegalStateException("Couldn't write EOY balance summary to file", e);
        }
    }

    private String[] buildHeaderRow() {
        return eoyBalances.shouldGroupWallets()
                ? new String[] {"Asset", "Balance"}
                : new String[] {"Wallet", "Asset", "Balance"};
    }

    private List<String[]> buildRows() {
        Comparator<EoyBalance> byWalletAssetBalance = eoyBalances.shouldGroupWallets()
                ? comparing(EoyBalance::asset).thenComparing(EoyBalance::balance)
                : comparing(EoyBalance::wallet).thenComparing(EoyBalance::asset).thenComparing(EoyBalance::balance);
        return eoyBalances.getBalances().stream()
                .filter(not(EoyBalance::isBalanceZero))
                .sorted(byWalletAssetBalance)
                .map(this::toStringArray)
                .toList();
    }

    private String[] toStringArray(EoyBalance eoyBalance) {
        return Stream.of(eoyBalance.wallet(), eoyBalance.asset(), BALANCE_FORMATTER.format(eoyBalance.balance()))
                .filter(Objects::nonNull)
                .toArray(String[]::new);
    }
}
