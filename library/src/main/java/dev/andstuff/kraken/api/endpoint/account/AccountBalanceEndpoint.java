package dev.andstuff.kraken.api.endpoint.account;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.AccountBalanceParams;

/**
 * The private {@code Balance} endpoint for account balance.
 */
public class AccountBalanceEndpoint extends WalletScopedEndpoint<Map<String, BigDecimal>> {

    /**
     * Creates the {@code Balance} endpoint with default options.
     */
    public AccountBalanceEndpoint() {
        this(AccountBalanceParams.builder().build());
    }

    /**
     * Creates the {@code Balance} endpoint.
     *
     * @param params the request parameters
     */
    public AccountBalanceEndpoint(AccountBalanceParams params) {
        super("Balance", params, params.getAccountId(), new TypeReference<>() {});
    }
}
