package dev.andstuff.kraken.api.rest;

import dev.andstuff.kraken.api.endpoint.KrakenException;
import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingBetaEndpoint;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;
import lombok.NonNull;

/**
 * Performs the HTTP calls to the Kraken REST API and deserializes their responses.
 *
 * <p>{@link DefaultKrakenRestRequester} is the implementation used by default. Implement this interface to use another HTTP client, e.g. Spring RestTemplate, Apache HttpComponents or OkHttp, and pass it to {@link dev.andstuff.kraken.api.KrakenAPI KrakenAPI}.
 */
public interface KrakenRestRequester {

    /**
     * Queries a public endpoint.
     *
     * @param <T> the type the response is deserialized into
     * @param endpoint the endpoint to query
     * @return the deserialized response
     * @throws KrakenException if Kraken returns an error
     */
    <T> T execute(PublicEndpoint<T> endpoint);

    /**
     * Queries a private endpoint, signing the request with the given credentials.
     *
     * @param <T> the type the response is deserialized into
     * @param endpoint the endpoint to query
     * @param credentials the credentials used to sign the request
     * @param nonceGenerator the generator providing the nonce of the request
     * @return the deserialized response
     * @throws KrakenException if Kraken returns an error
     */
    <T> T execute(PrivateEndpoint<T> endpoint, @NonNull KrakenCredentials credentials, @NonNull KrakenNonceGenerator nonceGenerator);

    /**
     * Queries a Funding (Beta) endpoint, sending the nonce in the {@code API-Nonce} header and signing the URL path, its query string and the JSON body with the given credentials. Requesters written before Funding (Beta) support don't implement this method.
     *
     * @param <T> the type the response is deserialized into
     * @param endpoint the endpoint to query
     * @param credentials the credentials used to sign the request
     * @param nonceGenerator the generator providing the nonce of the request
     * @return the deserialized response
     * @throws KrakenException if Kraken answers with an HTTP error status
     * @throws UnsupportedOperationException if the requester doesn't support Funding (Beta) endpoints
     */
    default <T> T execute(FundingBetaEndpoint<T> endpoint, @NonNull KrakenCredentials credentials, @NonNull KrakenNonceGenerator nonceGenerator) {
        throw new UnsupportedOperationException("%s doesn't support Funding (Beta) endpoints".formatted(getClass().getSimpleName()));
    }
}
