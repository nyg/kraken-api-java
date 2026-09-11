package dev.andstuff.kraken.api.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.TradesHistoryParams;
import dev.andstuff.kraken.api.endpoint.account.response.TradesHistory;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code TradesHistory} endpoint for trades history.
 */
public class TradesHistoryEndpoint extends PrivateEndpoint<TradesHistory> {

    /**
     * Creates the {@code TradesHistory} endpoint with default options.
     */
    public TradesHistoryEndpoint() {
        this(TradesHistoryParams.builder().build());
    }

    /**
     * Creates the {@code TradesHistory} endpoint.
     *
     * @param params the request parameters
     */
    public TradesHistoryEndpoint(TradesHistoryParams params) {
        super("TradesHistory", params, new TypeReference<>() {});
    }
}
