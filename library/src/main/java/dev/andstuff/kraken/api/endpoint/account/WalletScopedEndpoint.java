package dev.andstuff.kraken.api.endpoint.account;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

abstract class WalletScopedEndpoint<T> extends PrivateEndpoint<T> {

    private final String accountId;

    WalletScopedEndpoint(String path, PostParams postParams, String accountId, TypeReference<T> responseType) {
        super(path, postParams, responseType);
        this.accountId = accountId;
    }

    /**
     * Builds the endpoint URL with the optional wallet selector.
     *
     * @return the URL with an encoded {@code account_id} query parameter when a wallet is selected
     * @throws IllegalStateException if the URL is malformed
     */
    @Override
    public URL buildURL() {
        if (accountId == null) {
            return super.buildURL();
        }
        try {
            return URI.create(super.buildURL() + "?account_id=" + URLEncoder.encode(accountId, StandardCharsets.UTF_8)).toURL();
        }
        catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid %s URL".formatted(path), e);
        }
    }
}
