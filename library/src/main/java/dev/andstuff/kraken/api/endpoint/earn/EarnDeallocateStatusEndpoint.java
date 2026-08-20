package dev.andstuff.kraken.api.endpoint.earn;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.earn.params.EarnStatusParams;
import dev.andstuff.kraken.api.endpoint.earn.response.AllocationStatus;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code Earn/DeallocateStatus} endpoint, telling whether the last deallocation from a strategy is still in progress.
 */
public class EarnDeallocateStatusEndpoint extends PrivateEndpoint<AllocationStatus> {

    /**
     * Creates the endpoint.
     *
     * @param params the strategy the deallocation was made from
     */
    public EarnDeallocateStatusEndpoint(EarnStatusParams params) {
        super("Earn/DeallocateStatus", params, new TypeReference<>() {});
    }
}
