package dev.andstuff.kraken.api.endpoint.trading.params;

import static java.util.stream.Collectors.joining;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of {@code EditOrder}, identifying the order by exactly one of {@code transactionId} or {@code originalUserReference}; omitted options are left unchanged.
 */
@Getter
@Builder(toBuilder = true)
public class EditOrderParams extends PostParams {

    /**
     * The Kraken identifier of the order to edit.
     */
    private final String transactionId;

    /**
     * The user reference of the order to edit; Kraken rejects the request if several orders share it.
     */
    private final Integer originalUserReference;

    /**
     * The user reference of the edit request. The user reference of the original order is not retained on the new order.
     */
    private final Integer userReference;

    /**
     * The new order quantity in terms of the base asset.
     */
    private final BigDecimal volume;

    /**
     * The new quantity shown in the book for iceberg orders, at least 1/15 of the volume.
     */
    private final BigDecimal displayVolume;

    /**
     * The asset pair {@code id} or {@code altname} of the order, e.g. {@code XBTUSD}.
     */
    @NonNull
    private final String pair;

    /**
     * The asset class, required for non-crypto pairs such as xStocks.
     */
    private final AssetClass assetClass;

    /**
     * The new limit price of limit and iceberg orders, or trigger price of triggered orders, absolute or relative as for {@code AddOrder}.
     */
    private final String price;

    /**
     * The new limit price of stop-loss-limit, take-profit-limit and trailing-stop-limit orders.
     */
    private final String price2;

    /**
     * The order flags; only {@link OrderFlag#POST} can be changed and must be repeated to be kept.
     */
    private final Set<OrderFlag> orderFlags;

    /**
     * The time after which the matching engine rejects the edit request, between 2 and 60 seconds from now.
     */
    private final Instant deadline;

    /**
     * Whether Kraken returns a pending replace response before the order is completely replaced.
     */
    private final Boolean cancelResponse;

    /**
     * Whether Kraken only validates the request without editing the order. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean validate;

    /**
     * Returns the parameters sent to Kraken.
     *
     * @return the POST parameters
     * @throws IllegalArgumentException if not exactly one of {@code transactionId} or {@code originalUserReference} is set
     */
    @Override
    protected Map<String, String> params() {
        if ((transactionId == null) == (originalUserReference == null)) {
            throw new IllegalArgumentException("Specify exactly one of transactionId or originalUserReference");
        }
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "txid", transactionId);
        putIfNonNull(params, "txid", originalUserReference);
        putIfNonNull(params, "userref", userReference);
        putIfNonNull(params, "volume", volume, BigDecimal::toPlainString);
        putIfNonNull(params, "displayvol", displayVolume, BigDecimal::toPlainString);
        params.put("pair", pair);
        putIfNonNull(params, "asset_class", assetClass, AssetClass::getValue);
        putIfNonNull(params, "price", price);
        putIfNonNull(params, "price2", price2);
        putIfNonNull(params, "oflags", orderFlags, flags -> flags.stream().sorted().map(OrderFlag::getValue).collect(joining(",")));
        putIfNonNull(params, "deadline", deadline);
        putIfNonNull(params, "cancel_response", cancelResponse);
        putIfNonNull(params, "validate", validate);
        return params;
    }

    /**
     * Builds edit parameters, accepting prices as decimals or as relative price expressions.
     */
    public static class EditOrderParamsBuilder {

        /**
         * Sets the new price as a decimal or a relative price expression, e.g. {@code +1.5%}.
         *
         * @param price the price, or null to leave it unchanged
         * @return this builder
         */
        public EditOrderParamsBuilder price(String price) {
            this.price = price;
            return this;
        }

        /**
         * Sets a new absolute price.
         *
         * @param price the price, or null to leave it unchanged
         * @return this builder
         */
        public EditOrderParamsBuilder price(BigDecimal price) {
            this.price = price == null ? null : price.toPlainString();
            return this;
        }

        /**
         * Sets the new secondary price as a decimal or a relative price expression.
         *
         * @param price2 the secondary price, or null to leave it unchanged
         * @return this builder
         */
        public EditOrderParamsBuilder price2(String price2) {
            this.price2 = price2;
            return this;
        }

        /**
         * Sets a new absolute secondary price.
         *
         * @param price2 the secondary price, or null to leave it unchanged
         * @return this builder
         */
        public EditOrderParamsBuilder price2(BigDecimal price2) {
            this.price2 = price2 == null ? null : price2.toPlainString();
            return this;
        }
    }
}
