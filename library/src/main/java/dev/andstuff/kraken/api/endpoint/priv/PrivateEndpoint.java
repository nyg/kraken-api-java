package dev.andstuff.kraken.api.endpoint.priv;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.Endpoint;
import lombok.Getter;

/**
 * A private endpoint of the Kraken REST API, queried with a POST request on {@code /0/private/{path}} and requiring an API key and a signature.
 *
 * @param <T> the type the response is deserialized into
 */
@Getter
public class PrivateEndpoint<T> extends Endpoint<T> {

    private final PostParams postParams;

    /**
     * Creates a private endpoint.
     *
     * @param path the endpoint path, e.g. {@code Ledgers} for {@code /0/private/Ledgers}
     * @param postParams the request body parameters
     * @param responseType the type the response is deserialized into
     */
    public PrivateEndpoint(String path, PostParams postParams, TypeReference<T> responseType) {
        super("POST", path, responseType);
        this.postParams = postParams;
    }

    /**
     * Returns the request body, built from the endpoint parameters and the given nonce.
     *
     * @param nonce the nonce of the request, which Kraken requires to be ever-increasing
     * @return the URL encoded request body
     */
    public String encodedParamsWith(String nonce) {
        postParams.setNonce(nonce);
        return postParams.encoded();
    }

    /**
     * Builds the endpoint URL. Parameters are sent in the request body, not in the URL.
     *
     * @return the endpoint URL
     * @throws IllegalStateException if the URL is malformed
     */
    @Override
    public URL buildURL() {
        try {
            return new URI("https://api.kraken.com/0/private/%s".formatted(path)).toURL();
        }
        catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalStateException("Error while building endpoint URL", e);
        }
    }
}
