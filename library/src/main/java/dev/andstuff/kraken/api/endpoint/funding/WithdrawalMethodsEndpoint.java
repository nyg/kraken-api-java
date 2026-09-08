package dev.andstuff.kraken.api.endpoint.funding;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.funding.params.WithdrawalMethodsParams;
import dev.andstuff.kraken.api.endpoint.funding.response.WithdrawalMethod;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code WithdrawMethods} endpoint for withdrawal methods.
 */
public class WithdrawalMethodsEndpoint extends PrivateEndpoint<List<WithdrawalMethod>> {

    /**
     * Creates the {@code WithdrawMethods} endpoint with default options.
     */
    public WithdrawalMethodsEndpoint() {
        this(WithdrawalMethodsParams.builder().build());
    }

    /**
     * Creates the {@code WithdrawMethods} endpoint.
     *
     * @param params the request parameters
     */
    public WithdrawalMethodsEndpoint(WithdrawalMethodsParams params) {
        super("WithdrawMethods", params, new TypeReference<>() {});
    }
}
