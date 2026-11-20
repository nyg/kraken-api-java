package dev.andstuff.kraken.api.endpoint.account;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.TradeBalanceParams;
import dev.andstuff.kraken.api.endpoint.account.response.TradeBalance;

/**
 * The private {@code TradeBalance} endpoint for trade balance.
 */
public class TradeBalanceEndpoint extends WalletScopedEndpoint<TradeBalance> {

    /**
     * Creates the {@code TradeBalance} endpoint with default options.
     */
    public TradeBalanceEndpoint() {
        this(TradeBalanceParams.builder().build());
    }

    /**
     * Creates the {@code TradeBalance} endpoint.
     *
     * @param params the request parameters
     */
    public TradeBalanceEndpoint(TradeBalanceParams params) {
        super("TradeBalance", params, params.getAccountId(), new TypeReference<>() {});
    }
}
