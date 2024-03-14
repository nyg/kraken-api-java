package dev.andstuff.kraken.api.model.endpoint.account.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LedgerEntry(@JsonProperty("refid") String referenceId,
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
