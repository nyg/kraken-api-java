package dev.andstuff.kraken.example.eoy;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static java.util.function.Predicate.not;

import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
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

    public void writeToFile(String fileName, boolean thousandSeparator) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeNext(buildHeaderRow());
            writer.writeAll(buildRows(thousandSeparator));
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

    private List<String[]> buildRows(boolean thousandSeparator) {
        return eoyBalances.getBalances().stream()
                .filter(not(EoyBalance::isBalanceZero))
                .sorted(comparing(EoyBalance::wallet, nullsFirst(naturalOrder()))
                        .thenComparing(EoyBalance::asset)
                        .thenComparing(EoyBalance::balance))
                .map(eoyBalance -> buildRow(eoyBalance, thousandSeparator))
                .toList();
    }

    private String[] buildRow(EoyBalance eoyBalance, boolean thousandSeparator) {
        String balance = thousandSeparator
                ? BALANCE_FORMATTER.format(eoyBalance.balance())
                : eoyBalance.balance().stripTrailingZeros().toPlainString();
        return Stream.of(eoyBalance.wallet(), eoyBalance.asset(), balance)
                .filter(Objects::nonNull)
                .toArray(String[]::new);
    }
}
