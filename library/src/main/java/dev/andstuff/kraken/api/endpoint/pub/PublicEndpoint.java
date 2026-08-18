package dev.andstuff.kraken.api.endpoint.pub;

import static java.util.stream.Collectors.joining;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.Endpoint;

/**
 * A public endpoint of the Kraken REST API, queried with a GET request on {@code /0/public/{path}} and needing no credentials.
 *
 * @param <T> the type the response is deserialized into
 */
public class PublicEndpoint<T> extends Endpoint<T> {

    private final QueryParams queryParams;

    /**
     * Creates a public endpoint without query parameters.
     *
     * @param path the endpoint path, e.g. {@code Time} for {@code /0/public/Time}
     * @param responseType the type the response is deserialized into
     */
    public PublicEndpoint(String path, TypeReference<T> responseType) {
        this(path, QueryParams.EMPTY, responseType);
    }

    /**
     * Creates a public endpoint.
     *
     * @param path the endpoint path, e.g. {@code Assets} for {@code /0/public/Assets}
     * @param queryParams the URL query parameters
     * @param responseType the type the response is deserialized into
     */
    public PublicEndpoint(String path, QueryParams queryParams, TypeReference<T> responseType) {
        super("GET", path, responseType);
        this.queryParams = queryParams;
    }

    /**
     * Builds the endpoint URL, appending the URL encoded query parameters.
     *
     * @return the endpoint URL
     * @throws IllegalStateException if the URL is malformed
     */
    @Override
    public URL buildURL() {

        String queryString = queryParams.toMap()
                .entrySet().stream()
                .map(e -> "%s=%s".formatted(e.getKey(), URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8)))
                .collect(joining("&"));

        try {
            String baseURL = "https://api.kraken.com/0/public/%s".formatted(path);
            return new URI(baseURL + (queryString.isEmpty() ? "" : "?" + queryString)).toURL();
        }
        catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalStateException("Error while building endpoint URL", e);
        }
    }
}
