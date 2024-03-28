package dev.andstuff.kraken.example.reward.csv;

import static java.util.Comparator.comparing;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import com.opencsv.bean.MappingStrategy;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import dev.andstuff.kraken.api.endpoint.account.response.LedgerEntry;
import dev.andstuff.kraken.example.helper.HeaderAndPositionMappingStrategy;

public class CsvLedgerEntries {

    private final List<CsvLedgerEntry> ledgerEntries;

    public CsvLedgerEntries(List<LedgerEntry> ledgerEntries) {
        this.ledgerEntries = ledgerEntries.stream()
                .map(CsvLedgerEntry::new)
                .sorted(comparing(CsvLedgerEntry::time))
                .toList();
    }

    public void writeToFile(String fileName) {

        try (Writer writer = new FileWriter(fileName)) {
            MappingStrategy<CsvLedgerEntry> mappingStrategy = new HeaderAndPositionMappingStrategy<>();
            mappingStrategy.setType(CsvLedgerEntry.class);

            new StatefulBeanToCsvBuilder<CsvLedgerEntry>(writer)
                    .withMappingStrategy(mappingStrategy)
                    .build()
                    .write(ledgerEntries);
        }
        catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException | IOException e) {
            throw new RuntimeException("Couldn't write ledger entries to file", e);
        }
    }
}
