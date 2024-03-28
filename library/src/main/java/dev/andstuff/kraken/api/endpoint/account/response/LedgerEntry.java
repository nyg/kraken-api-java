package dev.andstuff.kraken.api.endpoint.account.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.With;

public record LedgerEntry(@With String id, // TODO see if jackson can set this value
                          @JsonProperty("refid") String referenceId,
                          Instant time,
                          Type type,
                          @JsonProperty("subtype") String subType,
                          @JsonProperty("aclass") String assetClass,
                          String asset,
                          BigDecimal amount,
                          BigDecimal fee,
                          BigDecimal balance) {

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

        @JsonEnumDefaultValue
        UNKNOWN
    }
}
