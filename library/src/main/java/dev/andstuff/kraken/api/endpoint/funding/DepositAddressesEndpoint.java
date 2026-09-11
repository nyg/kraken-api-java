package dev.andstuff.kraken.api.endpoint.funding;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.funding.params.DepositAddressesParams;
import dev.andstuff.kraken.api.endpoint.funding.response.DepositAddress;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code DepositAddresses} endpoint for deposit addresses.
 */
public class DepositAddressesEndpoint extends PrivateEndpoint<List<DepositAddress>> {

    /**
     * Creates the {@code DepositAddresses} endpoint using the required parameters.
     *
     * @param asset the asset sent to Kraken
     * @param method the method sent to Kraken
     */
    public DepositAddressesEndpoint(String asset, String method) {
        this(DepositAddressesParams.builder().asset(asset).method(method).build());
    }

    /**
     * Creates the {@code DepositAddresses} endpoint.
     *
     * @param params the request parameters
     */
    public DepositAddressesEndpoint(DepositAddressesParams params) {
        super("DepositAddresses", params, new TypeReference<>() {});
    }
}
