package dev.andstuff.kraken.api.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.model.KrakenCredentials;
import dev.andstuff.kraken.api.model.KrakenException;
import dev.andstuff.kraken.api.model.KrakenResponse;
import dev.andstuff.kraken.api.model.endpoint.Endpoint;
import dev.andstuff.kraken.api.model.endpoint.priv.PostParams;
import dev.andstuff.kraken.api.model.endpoint.priv.PrivateEndpoint;
import dev.andstuff.kraken.api.model.endpoint.pub.PublicEndpoint;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link KrakenRestRequester} implementation using {@link HttpsURLConnection}.
 */
@Slf4j
public class DefaultKrakenRestRequester implements KrakenRestRequester {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
    }

    private final KrakenCredentials credentials;

    public DefaultKrakenRestRequester() {
        this.credentials = null;
    }

    public DefaultKrakenRestRequester(KrakenCredentials credentials) {
        this.credentials = credentials;
    }

    public DefaultKrakenRestRequester(String key, String secret) {
        this.credentials = new KrakenCredentials(key, secret);
    }

    @Override
    public <T> T execute(PublicEndpoint<T> endpoint) {
        try {
            HttpsURLConnection connection = createHttpsConnection(endpoint);
            log.info("Fetching {}", connection.getURL());
            return parseResponse(connection.getInputStream(), endpoint);
        }
        catch (IOException e) {
            throw new IllegalStateException("Error while making request to Kraken API", e);
        }
    }

    @Override
    public <T> T execute(PrivateEndpoint<T> endpoint) {

        if (credentials == null) {
            throw new IllegalStateException("API credentials required");
        }

        PostParams postParams = endpoint.getPostParams();
        String nonce = postParams.initNonce(); // TODO nonce generator
        String postData = postParams.encoded();

        try {
            HttpsURLConnection connection = createHttpsConnection(endpoint);
            log.info("Fetching {}", connection.getURL());
            connection.addRequestProperty("API-Key", credentials.getKey());
            connection.addRequestProperty("API-Sign", credentials.sign(connection.getURL(), nonce, postData));
            connection.setDoOutput(true);

            try (OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream())) {
                out.write(postData);
            }

            return parseResponse(connection.getInputStream(), endpoint);
        }
        catch (IOException e) {
            throw new IllegalStateException("Error while making request to Kraken API", e);
        }
    }

    private static <T> HttpsURLConnection createHttpsConnection(Endpoint<T> endpoint) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) endpoint.buildURL().openConnection();
        connection.setRequestMethod(endpoint.getHttpMethod());
        connection.addRequestProperty("User-Agent", "github.com/nyg");
        return connection;
    }

    private static <T> T parseResponse(InputStream responseStream, Endpoint<T> endpoint) throws IOException {
        JavaType krakenResponseType = endpoint.wrappedResponseType(OBJECT_MAPPER.getTypeFactory());
        KrakenResponse<T> response = OBJECT_MAPPER.readValue(responseStream, krakenResponseType);
        return response.result().orElseThrow(() -> new KrakenException(response.error()));
    }
}
