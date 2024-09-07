package dev.andstuff.kraken.api.endpoint.account.params;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

@Getter
@Builder(toBuilder = true)
public class LedgerInfoParams extends PostParams {

    private final List<String> assets;
    private final String assetClass;
    private final Type assetType;
    private final Instant fromDate;
    private final Instant toDate;
    private final String fromLedgerId;
    private final String toLedgerId;
    private final boolean withoutCount;

    @With
    @Builder.Default
    private final int resultOffset = 0;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "asset", assets, v -> String.join(",", v));
        putIfNonNull(params, "aclass", assetClass);
        putIfNonNull(params, "type", assetType, e -> e.toString().toLowerCase());

        if (fromDate != null) {
            putIfNonNull(params, "start", fromDate, d -> Long.toString(d.getEpochSecond()));
        }
        else {
            putIfNonNull(params, "start", fromLedgerId);
        }

        if (toDate != null) {
            putIfNonNull(params, "end", toDate, d -> Long.toString(d.getEpochSecond()));
        }
        else {
            putIfNonNull(params, "end", toLedgerId);
        }

        putIfNonNull(params, "without_count", withoutCount);
        putIfNonNull(params, "ofs", resultOffset);
        return params;
    }

    public LedgerInfoParams withNextResultOffset() {
        return this.withResultOffset(resultOffset + 50);
    }

    public enum Type {
        ALL,
        TRADE,
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER,
        MARGIN,
        ADJUSTMENT,
        ROLLOVER,
        CREDIT,
        SETTLED,
        STAKING,
        DIVIDEND,
        SALE,
        NFT_REBATE,

        @JsonEnumDefaultValue
        UNKNOWN
    }
}
