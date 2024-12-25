package dev.andstuff.kraken.example.eoy;

import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;

import dev.andstuff.kraken.api.endpoint.account.response.LedgerEntry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class EoyBalance {

    private final Collection<LedgerEntry> balances;

    public EoyBalance(List<LedgerEntry> ledgerEntries) {
        this.balances = ledgerEntries.stream()
                .sorted(comparing(LedgerEntry::time).reversed())
                .collect(toMap(LedgerEntryKey::from, identity(), (a, b) -> a))
                .values();
    }

    private record LedgerEntryKey(String wallet, String asset) {

        public static LedgerEntryKey from(LedgerEntry ledgerEntry) {
            return new LedgerEntryKey(ledgerEntry.wallet(), ledgerEntry.asset());
        }
    }
}
