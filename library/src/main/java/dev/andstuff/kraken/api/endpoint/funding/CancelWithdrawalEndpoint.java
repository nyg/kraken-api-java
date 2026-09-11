package dev.andstuff.kraken.api.endpoint.funding;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.funding.params.CancelWithdrawalParams;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code WithdrawCancel} endpoint for cancel withdrawal.
 */
public class CancelWithdrawalEndpoint extends PrivateEndpoint<Boolean> {

    /**
     * Creates the {@code WithdrawCancel} endpoint using the required parameters.
     *
     * @param asset the asset sent to Kraken
     * @param referenceId the referenceId sent to Kraken
     */
    public CancelWithdrawalEndpoint(String asset, String referenceId) {
        this(CancelWithdrawalParams.builder().asset(asset).referenceId(referenceId).build());
    }

    /**
     * Creates the {@code WithdrawCancel} endpoint.
     *
     * @param params the request parameters
     */
    public CancelWithdrawalEndpoint(CancelWithdrawalParams params) {
        super("WithdrawCancel", params, new TypeReference<>() {});
    }
}
