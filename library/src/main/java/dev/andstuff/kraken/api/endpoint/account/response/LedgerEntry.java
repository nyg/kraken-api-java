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

/**
 * A single ledger entry, as returned by the {@code Ledgers} and {@code QueryLedgers} endpoints, or as read from a report exported by Kraken.
 *
 * @param id the identifier of the entry, which Kraken returns as the key of the entry, not as a field
 * @param referenceId the identifier of the operation the entry belongs to
 * @param time the time of the entry
 * @param type the type of the entry
 * @param subType the sub type of the entry, e.g. {@code allocation} for an earn entry
 * @param assetClass the class of the asset, e.g. {@code currency}
 * @param assetSubClass the sub class of the asset
 * @param asset the asset of the entry, possibly with a staking suffix, e.g. {@code DOT28.S}
 * @param wallet the wallet the entry belongs to, e.g. {@code spot}
 * @param amount the amount of the entry, fee excluded
 * @param fee the fee of the entry
 * @param balance the balance of the asset after the entry
 */
public record LedgerEntry(@CsvBindByName(column = "txid") @With String id, // TODO see if jackson can set this value
                          @CsvBindByName(column = "refid") @JsonProperty("refid") String referenceId,
                          @CsvBindByName(column = "time") Instant time,
                          @CsvBindByName(column = "type") Type type,
                          @CsvBindByName(column = "subtype") @JsonProperty("subtype") String subType,
                          @CsvBindByName(column = "aclass") @JsonProperty("aclass") String assetClass,
                          @CsvBindByName(column = "subclass") @JsonProperty("subclass") String assetSubClass,
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

    /**
     * Returns the amount of the entry, fee deducted.
     *
     * @return the net amount
     */
    public BigDecimal netAmount() {
        return amount.subtract(fee);
    }

    /**
     * Returns whether the entry is a staking or earn reward, as opposed to an allocation, a deallocation or a migration.
     *
     * @return whether the entry is a reward
     */
    public boolean isStakingReward() {
        boolean isStakingOrEarnType = List.of(Type.STAKING, Type.EARN).contains(type);
        boolean isRewardSubType = !List.of("allocation", "deallocation", "autoallocation", "migration").contains(subType);
        return isStakingOrEarnType && isRewardSubType;
    }

    /**
     * Returns the year the entry belongs to, in UTC.
     *
     * @return the year of the entry
     */
    public int year() {
        return time.atZone(ZoneId.of("UTC")).getYear();
    }

    /**
     * The type of a ledger entry.
     */
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
