package dev.andstuff.kraken.api.endpoint.earn;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.earn.params.EarnAllocationParams;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code Earn/Deallocate} endpoint, removing funds from an earn strategy. It requires an API key with the earn funds permission and returns before the deallocation completes.
 */
public class EarnDeallocateEndpoint extends PrivateEndpoint<Boolean> {

    /**
     * Creates the endpoint.
     *
     * @param params the strategy and the amount to deallocate
     */
    public EarnDeallocateEndpoint(EarnAllocationParams params) {
        super("Earn/Deallocate", params, new TypeReference<>() {});
    }
}
