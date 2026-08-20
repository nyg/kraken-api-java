package dev.andstuff.kraken.api.endpoint.earn;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.earn.params.EarnAllocationParams;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code Earn/Allocate} endpoint, allocating funds to an earn strategy. It requires an API key with the earn funds permission and returns before the allocation completes.
 */
public class EarnAllocateEndpoint extends PrivateEndpoint<Boolean> {

    /**
     * Creates the endpoint.
     *
     * @param params the strategy and the amount to allocate
     */
    public EarnAllocateEndpoint(EarnAllocationParams params) {
        super("Earn/Allocate", params, new TypeReference<>() {});
    }
}
