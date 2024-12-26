package dev.andstuff.kraken.example.eoy;

import java.math.BigDecimal;

import dev.andstuff.kraken.api.endpoint.account.response.LedgerEntry;

public record EoyBalance(String wallet, String asset, BigDecimal balance) {

    public static EoyBalance from(LedgerEntry ledgerEntry, boolean groupAssets, boolean groupWallets) {
        return new EoyBalance(
                groupWallets ? null : ledgerEntry.wallet(),
                groupAssets ? ledgerEntry.underlyingAsset() : ledgerEntry.asset(),
                ledgerEntry.balance());
    }

    public EoyBalance addingBalance(BigDecimal additionalBalance) {
        return new EoyBalance(wallet, asset, balance.add(additionalBalance));
    }

    public boolean isBalanceZero() {
        return balance.compareTo(BigDecimal.ZERO) == 0;
    }
}
