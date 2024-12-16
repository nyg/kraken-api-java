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

@Slf4j
@RequiredArgsConstructor
public abstract class Endpoint<T> {

    @Getter
    private final String httpMethod;

    protected final String path;

    @Getter
    private final TypeReference<T> responseType;

    public abstract URL buildURL();

    // TODO maybe there's a more OO way for the two methods below
    public JavaType wrappedResponseType(TypeFactory typeFactory) {
        return typeFactory.constructParametricType(
                KrakenResponse.class, typeFactory.constructType(responseType.getType()));
    }

    public T processZipResponse(ZipInputStream zipStream) throws IOException {
        throw new IllegalStateException("Not implemented for this endpoint");
    }
}
