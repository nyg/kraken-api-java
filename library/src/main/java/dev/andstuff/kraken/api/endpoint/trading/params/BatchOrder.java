package dev.andstuff.kraken.api.endpoint.trading.params;

import static java.util.stream.Collectors.joining;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * An order of {@code AddOrderBatch}; its pair, asset class, deadline, validation and broker are set on {@link AddOrderBatchParams}. Omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class BatchOrder {

    /**
     * A non-unique numeric identifier grouping orders of the client, mutually exclusive with {@code clientOrderId}.
     */
    private final Integer userReference;

    /**
     * A client order identifier, unique among open orders: a UUID, with or without dashes, or free text of up to 18 ASCII characters. Mutually exclusive with {@code userReference}.
     */
    private final String clientOrderId;

    /**
     * The execution model of the order.
     */
    @NonNull
    private final OrderType orderType;

    /**
     * The order direction.
     */
    @NonNull
    private final OrderSide side;

    /**
     * The order quantity in terms of the base asset; zero closes a margin position.
     */
    @NonNull
    private final BigDecimal volume;

    /**
     * The quantity shown in the book for iceberg orders, at least 1/15 of the volume.
     */
    private final BigDecimal displayVolume;

    /**
     * The limit price of limit and iceberg orders, or the trigger price of triggered orders, absolute or relative as for {@code AddOrder}.
     */
    private final String price;

    /**
     * The limit price of stop-loss-limit and take-profit-limit orders.
     */
    private final String price2;

    /**
     * The price signal triggering triggered orders. Kraken defaults to {@code last} when omitted.
     */
    private final Trigger trigger;

    /**
     * The amount of leverage desired, e.g. {@code 5}. Kraken places a spot order when omitted.
     */
    private final String leverage;

    /**
     * Whether the order may only reduce an open position. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean reduceOnly;

    /**
     * The self trade prevention mode. Kraken defaults to {@code cancel-newest} when omitted.
     */
    private final SelfTradePrevention selfTradePrevention;

    /**
     * The order flags.
     */
    private final Set<OrderFlag> orderFlags;

    /**
     * The time in force, except {@code FOK}. Kraken defaults to {@code GTC} when omitted.
     */
    private final TimeInForce timeInForce;

    /**
     * The scheduled start time: {@code 0} for now, a Unix timestamp in seconds, or {@code +<n>} for n seconds from now.
     */
    private final String startTime;

    /**
     * The expiry time of GTD orders: {@code 0} for none, a Unix timestamp in seconds, or {@code +<n>} for n seconds from now, at least 5.
     */
    private final String expireTime;

    /**
     * The conditional close order placed once this order executes.
     */
    private final ConditionalClose close;

    Map<String, Object> params() {
        Map<String, Object> params = new LinkedHashMap<>();
        putIfNonNull(params, "userref", userReference);
        putIfNonNull(params, "cl_ord_id", clientOrderId);
        params.put("ordertype", orderType.getValue());
        params.put("type", side.getValue());
        params.put("volume", volume.toPlainString());
        putIfNonNull(params, "displayvol", displayVolume, BigDecimal::toPlainString);
        putIfNonNull(params, "price", price);
        putIfNonNull(params, "price2", price2);
        putIfNonNull(params, "trigger", trigger, Trigger::getValue);
        putIfNonNull(params, "leverage", leverage);
        putIfNonNull(params, "reduce_only", reduceOnly);
        putIfNonNull(params, "stptype", selfTradePrevention, SelfTradePrevention::getValue);
        putIfNonNull(params, "oflags", orderFlags, flags -> flags.stream().sorted().map(OrderFlag::getValue).collect(joining(",")));
        putIfNonNull(params, "timeinforce", timeInForce, TimeInForce::getValue);
        putIfNonNull(params, "starttm", startTime);
        putIfNonNull(params, "expiretm", expireTime);
        putIfNonNull(params, "close", close, ConditionalClose::params);
        return params;
    }

    private static void putIfNonNull(Map<String, Object> params, String key, Object value) {
        putIfNonNull(params, key, value, Function.identity());
    }

    private static <T> void putIfNonNull(Map<String, Object> params, String key, T value, Function<T, ?> apply) {
        if (value != null) {
            params.put(key, apply.apply(value));
        }
    }

    /**
     * Builds batch orders, accepting prices as decimals or as relative price expressions.
     */
    public static class BatchOrderBuilder {

        /**
         * Sets the price as a decimal or a relative price expression, e.g. {@code +1.5%}.
         *
         * @param price the price, or null to omit it
         * @return this builder
         */
        public BatchOrderBuilder price(String price) {
            this.price = price;
            return this;
        }

        /**
         * Sets an absolute price.
         *
         * @param price the price, or null to omit it
         * @return this builder
         */
        public BatchOrderBuilder price(BigDecimal price) {
            this.price = price == null ? null : price.toPlainString();
            return this;
        }

        /**
         * Sets the secondary price as a decimal or a relative price expression.
         *
         * @param price2 the secondary price, or null to omit it
         * @return this builder
         */
        public BatchOrderBuilder price2(String price2) {
            this.price2 = price2;
            return this;
        }

        /**
         * Sets an absolute secondary price.
         *
         * @param price2 the secondary price, or null to omit it
         * @return this builder
         */
        public BatchOrderBuilder price2(BigDecimal price2) {
            this.price2 = price2 == null ? null : price2.toPlainString();
            return this;
        }
    }
}
