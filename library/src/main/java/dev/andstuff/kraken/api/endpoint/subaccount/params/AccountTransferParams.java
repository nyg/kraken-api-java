package dev.andstuff.kraken.api.endpoint.subaccount.params;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of the {@code AccountTransfer} endpoint. Source and destination are public account identifiers, e.g. {@code ABCD 1234 EFGH 5678}, and the asset class defaults to {@link AssetClass#CURRENCY}.
 */
@Getter
@Builder(toBuilder = true)
public class AccountTransferParams extends PostParams {

    @NonNull
    private final String asset;

    @Builder.Default
    private final AssetClass assetClass = AssetClass.CURRENCY;

    @NonNull
    private final BigDecimal amount;

    @NonNull
    private final String from;

    @NonNull
    private final String to;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        params.put("asset", asset);
        putIfNonNull(params, "asset_class", assetClass, e -> e.toString().toLowerCase());
        params.put("amount", amount.toPlainString());
        params.put("from", from);
        params.put("to", to);
        return params;
    }
}
