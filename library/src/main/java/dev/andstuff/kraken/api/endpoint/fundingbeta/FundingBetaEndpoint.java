package dev.andstuff.kraken.api.endpoint.fundingbeta;

import static java.util.stream.Collectors.joining;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.Endpoint;
import lombok.Getter;

/**
 * An endpoint of Kraken's Funding (Beta) API, queried on {@code /funding/{path}} with the HTTP method of the operation, e.g. {@code GET}, {@code PUT} or {@code DELETE}.
 *
 * <p>Unlike {@link dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint PrivateEndpoint}, parameters are sent in the URL query string and in a JSON body, the nonce is sent in the {@code API-Nonce} header, the signed path includes the query string, and responses are not wrapped in the {@code {error, result}} envelope: the response type is deserialized from the whole body, and errors are returned with an HTTP error status.
 *
 * @param <T> the type the response is deserialized into
 */
@Getter
public class FundingBetaEndpoint<T> extends Endpoint<T> {

    private final FundingBetaParams params;

    /**
     * Creates a Funding (Beta) endpoint taking neither query parameters nor a body.
     *
     * @param httpMethod the HTTP method of the operation, e.g. {@code GET}
     * @param path the endpoint path, relative to {@code /funding}, e.g. {@code v1/networks}
     * @param responseType the type the response is deserialized into
     */
    public FundingBetaEndpoint(String httpMethod, String path, TypeReference<T> responseType) {
        this(httpMethod, path, FundingBetaParams.EMPTY, responseType);
    }

    /**
     * Creates a Funding (Beta) endpoint.
     *
     * @param httpMethod the HTTP method of the operation, e.g. {@code POST}
     * @param path the endpoint path, relative to {@code /funding}, with its path parameters already encoded, e.g. {@code v1/addresses}
     * @param params the query and body parameters
     * @param responseType the type the response is deserialized into
     */
    public FundingBetaEndpoint(String httpMethod, String path, FundingBetaParams params, TypeReference<T> responseType) {
        super(httpMethod, path, responseType);
        this.params = params;
    }

    /**
     * Returns the request body, which is also part of the signed message.
     *
     * @return the JSON request body, or an empty string for an endpoint sending no body
     */
    public String encodedBody() {
        return params.encodedBody();
    }

    /**
     * Returns the media type of the request body.
     *
     * @return {@code application/json}
     */
    public String getContentType() {
        return "application/json";
    }

    /**
     * Builds the endpoint URL, appending the URL encoded query parameters. The path and query of this URL are the path Kraken expects to be signed.
     *
     * @return the endpoint URL
     * @throws IllegalStateException if the URL is malformed
     */
    @Override
    public URL buildURL() {

        String queryString = params.toMap()
                .entrySet().stream()
                .map(e -> "%s=%s".formatted(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8), URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8)))
                .collect(joining("&"));

        try {
            String baseURL = "https://api.kraken.com/funding/%s".formatted(path);
            return new URI(baseURL + (queryString.isEmpty() ? "" : "?" + queryString)).toURL();
        }
        catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalStateException("Error while building endpoint URL", e);
        }
    }

    /**
     * Encodes a path parameter so that it can be inserted in the endpoint path.
     *
     * @param value the path parameter value
     * @return the percent-encoded value
     */
    protected static String pathSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
