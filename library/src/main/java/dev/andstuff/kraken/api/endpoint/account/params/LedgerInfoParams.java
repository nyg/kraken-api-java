package dev.andstuff.kraken.api.endpoint.account.params;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.andstuff.kraken.api.endpoint.priv.JsonPostParams;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.With;

/**
 * The parameters of the {@code Ledgers} endpoint, sent as JSON. Entries can be filtered by asset, type and period, either with dates or with ledger identifiers, dates taking precedence. With cursor pagination, the filters must be repeated on every page, e.g. with {@code params.toBuilder().cursor(page.nextCursor()).build()}.
 */
@Getter
@Builder(toBuilder = true)
public class LedgerInfoParams extends JsonPostParams {

    /**
     * The assets to return entries for, all assets when omitted.
     */
    private final List<String> assets;

    /**
     * Class-qualified assets, used instead of {@code assets} to select assets of a specific class, e.g. {@code tokenized_asset}.
     */
    private final List<Asset> assetsWithClass;

    /**
     * The asset class of the entries. Kraken defaults to {@code currency} when omitted.
     *
     * @deprecated Kraken deprecated {@code aclass}: use {@code assetsWithClass} instead.
     */
    @Deprecated(since = "5.0.0")
    private final String assetClass;

    private final Type assetType;
    private final Instant fromDate;
    private final Instant toDate;
    private final String fromLedgerId;
    private final String toLedgerId;
    private final boolean withoutCount;
    private final RebaseMultiplier rebaseMultiplier;

    /**
     * Whether to use cursor pagination instead of {@code resultOffset}, with pages of up to 50 entries. Kraken then returns a null {@code count}. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean withCursor;

    /**
     * The {@code cursor.next} of the previous page, omitted for the first page. Requires {@code withCursor}.
     */
    private final String cursor;

    /**
     * The wallet ID, sent in the URL query; omit to use the default wallet.
     */
    private final String accountId;

    /**
     * The result offset, omitted when 0.
     *
     * @deprecated Kraken deprecated {@code ofs}: use {@code withCursor} and {@code cursor} instead.
     */
    @Deprecated(since = "5.0.0")
    @With
    @Builder.Default
    private final int resultOffset = 0;

    /**
     * Returns the parameters sent to Kraken.
     *
     * @return the JSON parameters
     * @throws IllegalArgumentException if both {@code assets} and {@code assetsWithClass} are set, if {@code cursor} is set without {@code withCursor}, or if {@code resultOffset} is not 0 with {@code withCursor}
     */
    @Override
    protected Map<String, Object> params() {
        if (assets != null && assetsWithClass != null) {
            throw new IllegalArgumentException("Specify assets or assetsWithClass, not both");
        }
        CursorPagination.validate(withCursor, cursor, resultOffset);
        Map<String, Object> params = new LinkedHashMap<>();
        if (assets != null) {
            params.put("asset", String.join(",", assets));
        }
        else if (assetsWithClass != null) {
            params.put("asset", assetsWithClass);
        }
        putIfPresent(params, "aclass", assetClass);
        putIfPresent(params, "type", assetType == null ? null : assetType.toString().toLowerCase());
        putIfPresent(params, "start", fromDate != null ? fromDate.getEpochSecond() : fromLedgerId);
        putIfPresent(params, "end", toDate != null ? toDate.getEpochSecond() : toLedgerId);
        params.put("without_count", withoutCount);
        if (resultOffset != 0) {
            params.put("ofs", resultOffset);
        }
        putIfPresent(params, "with_cursor", withCursor);
        putIfPresent(params, "cursor", cursor);
        putIfPresent(params, "rebase_multiplier", rebaseMultiplier == null ? null : rebaseMultiplier.getValue());
        return params;
    }

    private static void putIfPresent(Map<String, Object> params, String key, Object value) {
        if (value != null) {
            params.put(key, value);
        }
    }

    /**
     * Returns the same parameters, pointing to the next page of results. Kraken returns at most 50 entries per call.
     *
     * @return the parameters of the next page
     * @deprecated Kraken deprecated {@code ofs}: use {@code withCursor} and {@code toBuilder().cursor(page.nextCursor())} instead.
     */
    @Deprecated(since = "5.0.0")
    public LedgerInfoParams withNextResultOffset() {
        return this.withResultOffset(resultOffset + 50);
    }

    /**
     * A class-qualified asset for {@code Ledgers}.
     *
     * @param asset the asset name
     * @param assetClass the Kraken asset class, such as {@code currency} or {@code tokenized_asset}
     */
    public record Asset(@NonNull String asset, @NonNull @JsonProperty("aclass") String assetClass) {}

    /**
     * The type of ledger entries to return.
     */
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
