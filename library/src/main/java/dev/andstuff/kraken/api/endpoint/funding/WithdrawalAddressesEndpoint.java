package dev.andstuff.kraken.api.endpoint.funding;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.funding.params.WithdrawalAddressesParams;
import dev.andstuff.kraken.api.endpoint.funding.response.WithdrawalAddress;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code WithdrawAddresses} endpoint for withdrawal addresses.
 */
public class WithdrawalAddressesEndpoint extends PrivateEndpoint<List<WithdrawalAddress>> {

    /**
     * Creates the {@code WithdrawAddresses} endpoint with default options.
     */
    public WithdrawalAddressesEndpoint() {
        this(WithdrawalAddressesParams.builder().build());
    }

    /**
     * Creates the {@code WithdrawAddresses} endpoint.
     *
     * @param params the request parameters
     */
    public WithdrawalAddressesEndpoint(WithdrawalAddressesParams params) {
        super("WithdrawAddresses", params, new TypeReference<>() {});
    }
}
