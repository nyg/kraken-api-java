package dev.andstuff.kraken.api.endpoint.earn;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.earn.params.EarnStatusParams;
import dev.andstuff.kraken.api.endpoint.earn.response.AllocationStatus;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code Earn/AllocateStatus} endpoint, telling whether the last allocation to a strategy is still in progress.
 */
public class EarnAllocateStatusEndpoint extends PrivateEndpoint<AllocationStatus> {

    /**
     * Creates the endpoint.
     *
     * @param params the strategy the allocation was made to
     */
    public EarnAllocateStatusEndpoint(EarnStatusParams params) {
        super("Earn/AllocateStatus", params, new TypeReference<>() {});
    }
}
