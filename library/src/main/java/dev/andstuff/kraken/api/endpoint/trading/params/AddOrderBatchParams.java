package dev.andstuff.kraken.api.endpoint.trading.params;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.JsonPostParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of {@code AddOrderBatch}, sent as JSON; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class AddOrderBatchParams extends JsonPostParams {

    /**
     * The orders, between 2 and 15, all on the same pair. Kraken rejects the whole batch if one order fails validation.
     */
    @NonNull
    private final List<BatchOrder> orders;

    /**
     * The asset pair {@code id} or {@code altname} of every order, e.g. {@code BTC/USD}.
     */
    @NonNull
    private final String pair;

    /**
     * The asset class, required for non-crypto pairs such as xStocks.
     */
    private final AssetClass assetClass;

    /**
     * The time after which the matching engine rejects the orders, between 2 and 60 seconds from now.
     */
    private final Instant deadline;

    /**
     * Whether Kraken only validates the orders without submitting them. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean validate;

    /**
     * The broker IIBAN of a Kraken partner.
     */
    private final String broker;

    @Override
    protected Map<String, Object> params() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("orders", orders.stream().map(BatchOrder::params).toList());
        params.put("pair", pair);
        if (assetClass != null) {
            params.put("asset_class", assetClass.getValue());
        }
        if (deadline != null) {
            params.put("deadline", deadline.toString());
        }
        if (validate != null) {
            params.put("validate", validate);
        }
        if (broker != null) {
            params.put("broker", broker);
        }
        return params;
    }
}
