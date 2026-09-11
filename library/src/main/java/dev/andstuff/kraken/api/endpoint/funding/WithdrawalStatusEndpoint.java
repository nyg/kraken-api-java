package dev.andstuff.kraken.api.endpoint.funding;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.funding.params.WithdrawalStatusParams;
import dev.andstuff.kraken.api.endpoint.funding.response.WithdrawalStatus;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code WithdrawStatus} endpoint for withdrawal status.
 */
public class WithdrawalStatusEndpoint extends PrivateEndpoint<WithdrawalStatus> {

    /**
     * Creates the {@code WithdrawStatus} endpoint with default options.
     */
    public WithdrawalStatusEndpoint() {
        this(WithdrawalStatusParams.builder().build());
    }

    /**
     * Creates the {@code WithdrawStatus} endpoint.
     *
     * @param params the request parameters
     */
    public WithdrawalStatusEndpoint(WithdrawalStatusParams params) {
        super("WithdrawStatus", params, new TypeReference<>() {});
    }
}
