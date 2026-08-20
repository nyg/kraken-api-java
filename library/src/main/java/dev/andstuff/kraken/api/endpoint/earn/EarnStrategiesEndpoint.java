package dev.andstuff.kraken.api.endpoint.earn;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.earn.params.EarnStrategiesParams;
import dev.andstuff.kraken.api.endpoint.earn.response.EarnStrategies;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code Earn/Strategies} endpoint, listing the earn strategies available to the account.
 */
public class EarnStrategiesEndpoint extends PrivateEndpoint<EarnStrategies> {

    /**
     * Creates the endpoint.
     *
     * @param params the asset, lock type and pagination parameters restricting the strategies returned
     */
    public EarnStrategiesEndpoint(EarnStrategiesParams params) {
        super("Earn/Strategies", params, new TypeReference<>() {});
    }
}
