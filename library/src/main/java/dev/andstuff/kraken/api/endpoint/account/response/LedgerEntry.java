package dev.andstuff.kraken.api.endpoint.account.response;

import static java.util.regex.Pattern.matches;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

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
     * Attempts to extract the underlying asset, e.g. DOT28.S returns DOT, XXBT
     * returns XBT, ZUSD returns USD.
     *
     * @return the underlying asset
     */
    public String underlyingAsset() {
        return switch (asset) {
            // Kraken returns DOGE and BTC when exporting ledger as a file, but uses XDG and XBT in the API
            case String s when "DOGE".equals(s) -> "XDG";
            case String s when "BTC".equals(s) -> "XBT";
            // Take care of asset migrations
            case String s when "ETH2".equals(s) -> "ETH";
            case String s when matches("^MATIC(\\d+\\.S)?$", s) -> "POL";
            // Remove X or Z prefix for fiat and some cryptos
            case String s when matches("^[XZ][A-Z]{3}$", s) -> s.substring(1, 4);
            // Return asset as is if it only contains numbers and capital letters
            case String s when matches("^[0-9A-Z]+$", s) -> s;
            // Strip staking suffix (e.g. `28.S`)
            default -> asset.split("[0-9.]")[0];
        };
    }

    public BigDecimal netAmount() {
        return amount.subtract(fee);
    }

    public boolean isStakingReward() {
        boolean isStakingOrEarnType = List.of(Type.STAKING, Type.EARN).contains(type);
        boolean isRewardSubType = !List.of("allocation", "deallocation", "autoallocation", "migration").contains(subType);
        return isStakingOrEarnType && isRewardSubType;
    }

    public int year() {
        return time.atZone(ZoneId.of("UTC")).getYear();
    }

    public enum Type {
        ADJUSTMENT,
        CONVERSION,
        CREDIT,
        DEPOSIT,
        DIVIDEND,
        EARN,
        MARGIN,
        NONE,
        RECEIVE,
        REWARD,
        ROLLOVER,
        SALE,
        SETTLED,
        SPEND,
        STAKING,
        TRADE,
        TRANSFER,
        WITHDRAWAL,

        @JsonProperty("custodytransfer")
        CUSTODY_TRANSFER,
        @JsonProperty("nftcreatorfee")
        NFT_CREATOR_FEE,
        @JsonProperty("nftrebate")
        NFT_REBATE,
        @JsonProperty("nfttrade")
        NFT_TRADE,

        @JsonEnumDefaultValue
        UNKNOWN
    }
}
