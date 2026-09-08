package dev.andstuff.kraken.api.endpoint.account;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.ExtendedBalanceParams;
import dev.andstuff.kraken.api.endpoint.account.response.ExtendedBalance;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code BalanceEx} endpoint for extended balance.
 */
public class ExtendedBalanceEndpoint extends PrivateEndpoint<Map<String, ExtendedBalance>> {

    /**
     * Creates the {@code BalanceEx} endpoint with default options.
     */
    public ExtendedBalanceEndpoint() {
        this(ExtendedBalanceParams.builder().build());
    }

    /**
     * Creates the {@code BalanceEx} endpoint.
     *
     * @param params the request parameters
     */
    public ExtendedBalanceEndpoint(ExtendedBalanceParams params) {
        super("BalanceEx", params, new TypeReference<>() {});
    }
}
