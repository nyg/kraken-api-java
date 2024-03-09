package dev.andstuff.kraken.api.neo.model.endpoint;

import java.net.URL;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import dev.andstuff.kraken.api.neo.model.KrakenResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Endpoint<T> {

    private final String httpMethod;
    private final String path;
    private final TypeReference<T> responseType;

    public abstract URL buildURL();

    public JavaType wrappedResponseType(TypeFactory typeFactory) {
        return typeFactory.constructParametricType(
                KrakenResponse.class, typeFactory.constructType(responseType.getType()));
    }
}
