package dev.andstuff.kraken.api.endpoint.account;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.AccountBalanceParams;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code Balance} endpoint for account balance.
 */
public class AccountBalanceEndpoint extends PrivateEndpoint<Map<String, BigDecimal>> {

    private final AccountBalanceParams params;

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
        super("Balance", params, new TypeReference<>() {});
        this.params = params;
    }

    /**
     * Builds the {@code Balance} URL with the optional wallet selector.
     *
     * @return the URL with an encoded {@code account_id} query parameter when set
     */
    @Override
    public URL buildURL() {
        if (params.getAccountId() == null) {
            return super.buildURL();
        }
        try {
            return URI.create(super.buildURL() + "?account_id=" + URLEncoder.encode(params.getAccountId(), StandardCharsets.UTF_8)).toURL();
        }
        catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid Balance URL", e);
        }
    }
}
