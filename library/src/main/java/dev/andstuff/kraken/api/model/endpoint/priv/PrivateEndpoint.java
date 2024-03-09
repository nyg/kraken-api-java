package dev.andstuff.kraken.api.model.endpoint.priv;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.model.endpoint.Endpoint;
import lombok.Getter;

@Getter
public class PrivateEndpoint<T> extends Endpoint<T> {

    private final PostParams postParams;

    public PrivateEndpoint(String path, TypeReference<T> responseType) {
        this(path, null, responseType);
    }

    public PrivateEndpoint(String path, PostParams postParams, TypeReference<T> responseType) {
        super("POST", path, responseType);
        this.postParams = postParams;
    }

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
