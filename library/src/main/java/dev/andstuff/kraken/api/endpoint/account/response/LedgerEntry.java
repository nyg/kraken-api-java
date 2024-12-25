package dev.andstuff.kraken.api.endpoint.account.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;

import lombok.With;

public record LedgerEntry(@CsvBindByName(column = "txid") @With String id, // TODO see if jackson can set this value
                          @CsvBindByName(column = "refid") @JsonProperty("refid") String referenceId,
                          @CsvBindByName(column = "time") Instant time,
                          @CsvBindByName(column = "type") Type type,
                          @CsvBindByName(column = "subtype") @JsonProperty("subtype") String subType,
                          @CsvBindByName(column = "aclass") @JsonProperty("aclass") String assetClass,
                          @CsvBindByName(column = "asset") String asset,
                          @CsvBindByName(column = "wallet") String wallet,
                          @CsvBindByName(column = "amount") BigDecimal amount,
                          @CsvBindByName(column = "fee") BigDecimal fee,
                          @CsvBindByName(column = "balance") BigDecimal balance) {

    /**
     * Attempts to extract the underlying asset, e.g. DOT.28S returns DOT, XXBT
     * returns XBT, ZUSD returns USD.
     *
     * @return the underlying asset
     */
    public String underlyingAsset() {
        return asset.matches("^([XZ])([A-Z]{3})$")
                ? asset.substring(1, 4)
                : asset.split("[0-9.]")[0];
    }

    public BigDecimal netAmount() {
        return amount.subtract(fee);
    }

    public boolean isStakingReward() {
        return type == Type.STAKING && (subType == null || subType.isEmpty());
    }

    public int year() {
        return time.atZone(ZoneId.of("UTC")).getYear();
    }

    public boolean isBalanceZero() {
        return balance.compareTo(BigDecimal.ZERO) == 0;
    }

    public enum Type {
        NONE,
        TRADE,
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER,
        MARGIN,
        ADJUSTMENT,
        ROLLOVER,
        SPEND,
        RECEIVE,
        SETTLED,
        CREDIT,
        STAKING,
        REWARD,
        DIVIDEND,
        SALE,
        CONVERSION,
        NFTTRADE, // TODO add underscore
        NFTCREATORFEE,
        NFTREBATE,
        CUSTODYTRANSFER,

        EARN,

        @JsonEnumDefaultValue
        UNKNOWN
    }
}
