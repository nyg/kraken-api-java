package dev.andstuff.kraken.api.endpoint.earn;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.earn.params.EarnAllocationsParams;
import dev.andstuff.kraken.api.endpoint.earn.response.EarnAllocations;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code Earn/Allocations} endpoint, listing the earn allocations of the account.
 */
public class EarnAllocationsEndpoint extends PrivateEndpoint<EarnAllocations> {

    /**
     * Creates the endpoint.
     *
     * @param params the sort order, converted asset and zero allocation parameters
     */
    public EarnAllocationsEndpoint(EarnAllocationsParams params) {
        super("Earn/Allocations", params, new TypeReference<>() {});
    }
}
