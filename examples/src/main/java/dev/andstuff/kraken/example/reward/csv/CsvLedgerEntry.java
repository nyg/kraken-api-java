package dev.andstuff.kraken.example.reward.csv;

import java.math.BigDecimal;
import java.time.Instant;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import dev.andstuff.kraken.api.endpoint.account.response.LedgerEntry;

public record CsvLedgerEntry(@CsvBindByPosition(position = 7) @CsvBindByName(column = "ledger_entry_id") String ledgerEntryId,
                             @CsvBindByPosition(position = 8) @CsvBindByName(column = "reference_id") String referenceId,
                             @CsvBindByPosition(position = 0) @CsvBindByName(column = "date") Instant time,
                             @CsvBindByPosition(position = 3) @CsvBindByName(column = "type") String type,
                             @CsvBindByPosition(position = 4) @CsvBindByName(column = "sub_type") String subType,
                             @CsvBindByPosition(position = 1) @CsvBindByName(column = "asset") String asset,
                             @CsvBindByPosition(position = 2) @CsvBindByName(column = "staking_asset") String stakingAsset,
                             @CsvBindByPosition(position = 5) @CsvBindByName(column = "amount") BigDecimal amount,
                             @CsvBindByPosition(position = 6) @CsvBindByName(column = "fee") BigDecimal fee) {

    public CsvLedgerEntry(LedgerEntry ledgerEntry) {
        this(
                ledgerEntry.id(),
                ledgerEntry.referenceId(),
                ledgerEntry.time(),
                ledgerEntry.type().name().toLowerCase(),
                ledgerEntry.subType(),
                ledgerEntry.underlyingAsset(),
                ledgerEntry.asset(),
                ledgerEntry.amount(),
                ledgerEntry.fee());
    }
}
