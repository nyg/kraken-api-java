package dev.andstuff.kraken.api.endpoint.priv;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.Endpoint;
import lombok.Getter;

@Getter
public class PrivateEndpoint<T> extends Endpoint<T> {

    private final PostParams postParams;

    public PrivateEndpoint(String path, PostParams postParams, TypeReference<T> responseType) {
        super("POST", path, responseType);
        this.postParams = postParams;
    }

    public String encodedParamsWith(String nonce) {
        postParams.setNonce(nonce);
        return postParams.encoded();
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
