package dev.andstuff.kraken.api.endpoint.funding;

import java.math.BigDecimal;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.funding.params.WithdrawalInfoParams;
import dev.andstuff.kraken.api.endpoint.funding.response.WithdrawalInfo;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code WithdrawInfo} endpoint for withdrawal info.
 */
public class WithdrawalInfoEndpoint extends PrivateEndpoint<WithdrawalInfo> {

    /**
     * Creates the {@code WithdrawInfo} endpoint using the required parameters.
     *
     * @param asset the asset sent to Kraken
     * @param key the key sent to Kraken
     * @param amount the amount sent to Kraken
     */
    public WithdrawalInfoEndpoint(String asset, String key, BigDecimal amount) {
        this(WithdrawalInfoParams.builder().asset(asset).key(key).amount(amount).build());
    }

    /**
     * Creates the {@code WithdrawInfo} endpoint.
     *
     * @param params the request parameters
     */
    public WithdrawalInfoEndpoint(WithdrawalInfoParams params) {
        super("WithdrawInfo", params, new TypeReference<>() {});
    }
}
