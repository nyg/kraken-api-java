package dev.andstuff.kraken.api.endpoint.algotrading.params;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.JsonPostParams;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderSide;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of {@code AddStrategy}, sent as JSON. A TWAP strategy executes its volume evenly between its start and end times, which must be at least 60 seconds apart and at most 30 days from now.
 */
@Getter
@Builder(toBuilder = true)
public class AddStrategyParams extends JsonPostParams {

    /**
     * The strategy type, {@code twap} by default.
     */
    @NonNull
    @Builder.Default
    private final StrategyType strategyType = StrategyType.TWAP;

    /**
     * The asset pair {@code id} or {@code altname}, e.g. {@code BTC/USD} or {@code XBTUSD}. xStocks pairs are not supported.
     */
    @NonNull
    private final String symbol;

    /**
     * The asset class of the pair, {@code forex} by default, the only class Kraken accepts.
     */
    @NonNull
    @Builder.Default
    private final StrategyAssetClass assetClass = StrategyAssetClass.FOREX;

    /**
     * The direction of the strategy.
     */
    @NonNull
    private final OrderSide side;

    /**
     * The total quantity to execute over the strategy horizon, in the base asset.
     */
    @NonNull
    private final BigDecimal volume;

    /**
     * The order type of the child orders.
     */
    @NonNull
    private final StrategyOrderType orderType;

    /**
     * The limit price of the child orders, required for limit child orders and omitted for market ones.
     */
    private final BigDecimal price;

    /**
     * The start time: {@code 0} for now, a Unix timestamp in seconds, or {@code +<n>} for n seconds from now. Kraken starts the strategy now when omitted.
     */
    private final String startTime;

    /**
     * The end time: a Unix timestamp in seconds, or {@code +<n>} for n seconds from now.
     */
    @NonNull
    private final String expireTime;

    /**
     * Whether Kraken only validates the strategy without creating it, without checking the balance. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean validate;

    @Override
    protected Map<String, Object> params() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("strategy_type", strategyType.getValue());
        params.put("symbol", symbol);
        params.put("asset_class", assetClass.getValue());
        params.put("side", side.getValue());
        params.put("volume", volume.toPlainString());
        params.put("ordertype", orderType.getValue());
        if (price != null) {
            params.put("price", price.toPlainString());
        }
        if (startTime != null) {
            params.put("starttm", startTime);
        }
        params.put("expiretm", expireTime);
        if (validate != null) {
            params.put("validate", validate);
        }
        return params;
    }
}
