package dev.andstuff.kraken.api.model.endpoint.account.params;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.andstuff.kraken.api.model.endpoint.priv.PostParams;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

@Getter
@Builder(toBuilder = true)
public class LedgerInfoParams extends PostParams {

    private final List<String> assets;
    private final String assetClass;
    private final Type assetType;
    private final Instant startDate;
    private final Instant endDate;
    private final String startId; // TODO handle
    private final String endId; // TODO naming
    private final boolean withoutCount;

    @With
    @Builder.Default
    private final int resultOffset = 0;

    @Override
    protected Map<String, String> params() {
        HashMap<String, String> params = new HashMap<>();
        putIfNonNull(params, "asset", assets, v -> String.join(",", v));
        putIfNonNull(params, "aclass", assetClass);
        putIfNonNull(params, "type", assetType, e -> e.toString().toLowerCase());
        putIfNonNull(params, "start", startDate, d -> Long.toString(d.getEpochSecond()));
        putIfNonNull(params, "end", endDate, d -> Long.toString(d.getEpochSecond()));
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
    }
}
