package dev.andstuff.kraken.api.neo.model.endpoint;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.fasterxml.jackson.core.type.TypeReference;

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
            return new URI("https://api.kraken.com/0/private/%s".formatted(getPath())).toURL();
        }
        catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalStateException("Error while building endpoint URL", e);
        }
    }

    public String sign() {
        return "signed";
    }
}
