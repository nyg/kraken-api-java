package dev.andstuff.kraken.api.endpoint.trading.params;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code AmendOrder}, identifying the order by exactly one of {@code transactionId} or {@code clientOrderId}; omitted options are left unchanged.
 */
@Getter
@Builder(toBuilder = true)
public class AmendOrderParams extends PostParams {

    /**
     * The Kraken identifier of the order to amend.
     */
    private final String transactionId;

    /**
     * The client identifier of the order to amend.
     */
    private final String clientOrderId;

    /**
     * The new order quantity in terms of the base asset. A quantity below the filled quantity cancels the remaining quantity.
     */
    private final BigDecimal orderQuantity;

    /**
     * The new quantity shown in the book for iceberg orders, at least 1/15 of the remaining quantity.
     */
    private final BigDecimal displayQuantity;

    /**
     * The new limit price, for order types supporting one. A {@code +} or {@code -} prefix and a {@code %} suffix make it relative to the reference price.
     */
    private final String limitPrice;

    /**
     * The new trigger price, for triggered order types. A {@code +} or {@code -} prefix and a {@code %} suffix make it relative to the reference price.
     */
    private final String triggerPrice;

    /**
     * The asset pair, required for non-crypto pairs such as xStocks.
     */
    private final String pair;

    /**
     * Whether a limit price amend is rejected if the order cannot be posted passively in the book. Kraken defaults to {@code false} when omitted.
     */
    private final Boolean postOnly;

    /**
     * The time after which the matching engine rejects the amend request, between 2 and 60 seconds from now.
     */
    private final Instant deadline;

    /**
     * Returns the parameters sent to Kraken.
     *
     * @return the POST parameters
     * @throws IllegalArgumentException if not exactly one of {@code transactionId} or {@code clientOrderId} is set
     */
    @Override
    protected Map<String, String> params() {
        if ((transactionId == null) == (clientOrderId == null)) {
            throw new IllegalArgumentException("Specify exactly one of transactionId or clientOrderId");
        }
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "txid", transactionId);
        putIfNonNull(params, "cl_ord_id", clientOrderId);
        putIfNonNull(params, "order_qty", orderQuantity, BigDecimal::toPlainString);
        putIfNonNull(params, "display_qty", displayQuantity, BigDecimal::toPlainString);
        putIfNonNull(params, "limit_price", limitPrice);
        putIfNonNull(params, "trigger_price", triggerPrice);
        putIfNonNull(params, "pair", pair);
        putIfNonNull(params, "post_only", postOnly);
        putIfNonNull(params, "deadline", deadline);
        return params;
    }

    /**
     * Builds amend parameters, accepting prices as decimals or as relative price expressions.
     */
    public static class AmendOrderParamsBuilder {

        /**
         * Sets the new limit price as a decimal or a relative price expression, e.g. {@code +50}.
         *
         * @param limitPrice the limit price, or null to leave it unchanged
         * @return this builder
         */
        public AmendOrderParamsBuilder limitPrice(String limitPrice) {
            this.limitPrice = limitPrice;
            return this;
        }

        /**
         * Sets a new absolute limit price.
         *
         * @param limitPrice the limit price, or null to leave it unchanged
         * @return this builder
         */
        public AmendOrderParamsBuilder limitPrice(BigDecimal limitPrice) {
            this.limitPrice = limitPrice == null ? null : limitPrice.toPlainString();
            return this;
        }

        /**
         * Sets the new trigger price as a decimal or a relative price expression, e.g. {@code -100}.
         *
         * @param triggerPrice the trigger price, or null to leave it unchanged
         * @return this builder
         */
        public AmendOrderParamsBuilder triggerPrice(String triggerPrice) {
            this.triggerPrice = triggerPrice;
            return this;
        }

        /**
         * Sets a new absolute trigger price.
         *
         * @param triggerPrice the trigger price, or null to leave it unchanged
         * @return this builder
         */
        public AmendOrderParamsBuilder triggerPrice(BigDecimal triggerPrice) {
            this.triggerPrice = triggerPrice == null ? null : triggerPrice.toPlainString();
            return this;
        }
    }
}
