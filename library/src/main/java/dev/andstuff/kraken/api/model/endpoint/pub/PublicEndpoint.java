package dev.andstuff.kraken.api.model.endpoint.pub;

import static java.util.stream.Collectors.joining;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.model.endpoint.Endpoint;

public class PublicEndpoint<T> extends Endpoint<T> {

    private final QueryParams queryParams;

    public PublicEndpoint(String path, TypeReference<T> responseType) {
        this(path, QueryParams.EMPTY, responseType);
    }

    public PublicEndpoint(String path, QueryParams queryParams, TypeReference<T> responseType) {
        super("GET", path, responseType);
        this.queryParams = queryParams;
    }

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
