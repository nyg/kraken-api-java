package dev.andstuff.kraken.api.endpoint;

import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipInputStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A single call to the Kraken REST API: its HTTP method, path, parameters and response type.
 *
 * <p>Endpoints are not queried directly but handed to a {@link dev.andstuff.kraken.api.rest.KrakenRestRequester KrakenRestRequester}. Concrete endpoints extend either {@link dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint PublicEndpoint} or {@link dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint PrivateEndpoint}.
 *
 * @param <T> the type the response is deserialized into
 */
@Slf4j
@RequiredArgsConstructor
public abstract class Endpoint<T> {

    @Getter
    private final String httpMethod;

    /**
     * The path of the endpoint, relative to {@code /0/public} or {@code /0/private}, e.g. {@code Assets}.
     */
    protected final String path;

    @Getter
    private final TypeReference<T> responseType;

    /**
     * Builds the URL this endpoint must be queried at.
     *
     * @return the endpoint URL
     */
    public abstract URL buildURL();

    // TODO maybe there's a more OO way for the two methods below

    /**
     * Returns the response type wrapped in a {@link KrakenResponse}, i.e. the type Jackson must deserialize the response into.
     *
     * @param typeFactory the factory used to build the parametric type
     * @return the wrapped response type
     */
    public JavaType wrappedResponseType(TypeFactory typeFactory) {
        return typeFactory.constructParametricType(
                KrakenResponse.class, typeFactory.constructType(responseType.getType()));
    }

    /**
     * Reads the response of endpoints answering with a ZIP archive instead of JSON, e.g. report exports. Endpoints that can return such a response override this method.
     *
     * @param zipStream the response body
     * @return the deserialized response
     * @throws IOException if the archive cannot be read
     * @throws IllegalStateException if the endpoint doesn't return ZIP responses
     */
    public T processZipResponse(ZipInputStream zipStream) throws IOException {
        throw new IllegalStateException("Not implemented for this endpoint");
    }
}
