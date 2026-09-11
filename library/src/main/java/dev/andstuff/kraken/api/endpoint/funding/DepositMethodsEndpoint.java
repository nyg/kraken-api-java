package dev.andstuff.kraken.api.endpoint.funding;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.funding.params.DepositMethodsParams;
import dev.andstuff.kraken.api.endpoint.funding.response.DepositMethod;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code DepositMethods} endpoint for deposit methods.
 */
public class DepositMethodsEndpoint extends PrivateEndpoint<List<DepositMethod>> {

    /**
     * Creates the {@code DepositMethods} endpoint using the required parameters.
     *
     * @param asset the asset sent to Kraken
     */
    public DepositMethodsEndpoint(String asset) {
        this(DepositMethodsParams.builder().asset(asset).build());
    }

    /**
     * Creates the {@code DepositMethods} endpoint.
     *
     * @param params the request parameters
     */
    public DepositMethodsEndpoint(DepositMethodsParams params) {
        super("DepositMethods", params, new TypeReference<>() {});
    }
}
