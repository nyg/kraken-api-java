package dev.andstuff.kraken.api.model.endpoint;

import java.net.URL;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import dev.andstuff.kraken.api.model.KrakenResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Endpoint<T> {

    @Getter
    private final String httpMethod;

    protected final String path;

    @Getter
    private final TypeReference<T> responseType;

    public abstract URL buildURL();

    public JavaType wrappedResponseType(TypeFactory typeFactory) {
        return typeFactory.constructParametricType(
                KrakenResponse.class, typeFactory.constructType(responseType.getType()));
    }
}
