package dev.andstuff.kraken.example.eoy;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Stream;

import dev.andstuff.kraken.api.endpoint.account.response.LedgerEntry;

public record EoyBalance(String wallet, String asset, BigDecimal balance) {

    public static EoyBalance from(LedgerEntry ledgerEntry, boolean groupByUnderlyingAsset, boolean groupWallets) {
        return new EoyBalance(
                groupWallets ? null : ledgerEntry.wallet(),
                groupByUnderlyingAsset ? ledgerEntry.underlyingAsset() : ledgerEntry.asset(),
                ledgerEntry.balance());
    }

    public EoyBalance addingBalance(BigDecimal additionalBalance) {
        return new EoyBalance(wallet, asset, balance.add(additionalBalance));
    }

    public boolean isBalanceZero() {
        return balance.compareTo(BigDecimal.ZERO) == 0;
    }

    public String[] asStringArray() {
        return Stream.of(wallet, asset, balance.stripTrailingZeros().toPlainString())
                .filter(Objects::nonNull)
                .toArray(String[]::new);
    }
}
