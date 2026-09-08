package dev.andstuff.kraken.api.endpoint.funding;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.funding.params.DepositStatusParams;
import dev.andstuff.kraken.api.endpoint.funding.response.DepositStatus;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code DepositStatus} endpoint for deposit status.
 */
public class DepositStatusEndpoint extends PrivateEndpoint<DepositStatus> {

    /**
     * Creates the {@code DepositStatus} endpoint with default options.
     */
    public DepositStatusEndpoint() {
        this(DepositStatusParams.builder().build());
    }

    /**
     * Creates the {@code DepositStatus} endpoint.
     *
     * @param params the request parameters
     */
    public DepositStatusEndpoint(DepositStatusParams params) {
        super("DepositStatus", params, new TypeReference<>() {});
    }
}
