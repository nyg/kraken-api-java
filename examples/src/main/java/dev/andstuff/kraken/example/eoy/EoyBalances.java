package dev.andstuff.kraken.example.eoy;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;

import dev.andstuff.kraken.api.endpoint.account.response.LedgerEntry;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class EoyBalances {

    private final Collection<EoyBalance> balances;

    @Accessors(fluent = true)
    private final boolean shouldGroupWallets;

    public EoyBalances(List<LedgerEntry> ledgerEntries, boolean groupByUnderlyingAsset, boolean groupWallets) {
        this.shouldGroupWallets = groupWallets;
        this.balances = ledgerEntries.stream()
                .collect(toMap(LedgerEntryKey::from, identity(), (a, b) -> b))
                .values().stream()
                .collect(toMap(
                        ledgerEntry -> EoyBalanceKey.from(ledgerEntry, groupByUnderlyingAsset, groupWallets),
                        ledgerEntry -> EoyBalance.from(ledgerEntry, groupByUnderlyingAsset, groupWallets),
                        (a, b) -> a.addingBalance(b.balance())))
                .values();
    }

    private record EoyBalanceKey(String wallet, String asset) {

        public static EoyBalanceKey from(LedgerEntry ledgerEntry, boolean groupByUnderlyingAsset, boolean groupWallets) {
            return new EoyBalanceKey(
                    groupWallets ? null : ledgerEntry.wallet(),
                    groupByUnderlyingAsset ? ledgerEntry.underlyingAsset() : ledgerEntry.asset());
        }
    }

    private record LedgerEntryKey(String wallet, String asset) {

        public static LedgerEntryKey from(LedgerEntry ledgerEntry) {
            return new LedgerEntryKey(ledgerEntry.wallet(), ledgerEntry.asset());
        }
    }
}
