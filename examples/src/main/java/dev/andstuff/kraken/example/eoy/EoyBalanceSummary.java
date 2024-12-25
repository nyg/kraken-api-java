package dev.andstuff.kraken.example.eoy;

import static java.util.Comparator.comparing;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import com.opencsv.CSVWriter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EoyBalanceSummary {

    private static final String[] HEADER_ROW = {"Wallet", "Asset", "Balance"};

    private final EoyBalances eoyBalance;

    public void writeToFile(String fileName) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeNext(HEADER_ROW);
            writer.writeAll(buildRows());
        }
        catch (IOException e) {
            throw new IllegalStateException("Couldn't write EOY balance summary to file", e);
        }
    }

    private List<String[]> buildRows() {
        return eoyBalance.getBalances().stream()
                .filter(Predicate.not(EoyBalance::isBalanceZero))
                .sorted(comparing(EoyBalance::wallet).thenComparing(EoyBalance::asset).thenComparing(EoyBalance::balance))
                .map(EoyBalance::asStringArray)
                .toList();
    }
}
