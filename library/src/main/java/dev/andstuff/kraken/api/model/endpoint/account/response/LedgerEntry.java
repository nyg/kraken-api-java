package dev.andstuff.kraken.api.model.endpoint.account.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.With;

public record LedgerEntry(@With String id, // TODO
                          @JsonProperty("refid") String referenceId,
                          Instant time,
                          Type type,
                          @JsonProperty("subtype") String subType,
                          @JsonProperty("aclass") String assetClass,
                          String asset,
                          BigDecimal amount,
                          BigDecimal fee,
                          BigDecimal balance) {

    public BigDecimal netAmount() {
        return amount.subtract(fee);
    }

    /**
     * Attempts to extract the underlying asset, e.g. DOT.28S returns DOT, XXBT returns XBT, ZUSD returns USD.
     *
     * @return the underlying asset
     */
    public String underlyingAsset() {
        return asset.matches("^([XZ])([A-Z]{3})$")
                ? asset.substring(1, 4)
                : asset.split("[0-9.]")[0];
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
        NFTTRADE, // TODO
        NFTCREATORFEE,
        NFTREBATE,
        CUSTODYTRANSFER,
        // TODO not implemented
    }
}
