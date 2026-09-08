package dev.andstuff.kraken.api.endpoint.funding;

import java.math.BigDecimal;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.funding.params.WithdrawParams;
import dev.andstuff.kraken.api.endpoint.funding.response.FundingReference;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code Withdraw} endpoint for withdraw.
 */
public class WithdrawEndpoint extends PrivateEndpoint<FundingReference> {

    /**
     * Creates the {@code Withdraw} endpoint using the required parameters.
     *
     * @param asset the asset sent to Kraken
     * @param key the key sent to Kraken
     * @param amount the amount sent to Kraken
     */
    public WithdrawEndpoint(String asset, String key, BigDecimal amount) {
        this(WithdrawParams.builder().asset(asset).key(key).amount(amount).build());
    }

    /**
     * Creates the {@code Withdraw} endpoint.
     *
     * @param params the request parameters
     */
    public WithdrawEndpoint(WithdrawParams params) {
        super("Withdraw", params, new TypeReference<>() {});
    }
}
