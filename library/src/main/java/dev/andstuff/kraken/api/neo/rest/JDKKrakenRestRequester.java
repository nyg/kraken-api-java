package dev.andstuff.kraken.api.neo.rest;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.neo.model.KrakenCredentials;
import dev.andstuff.kraken.api.neo.model.KrakenException;
import dev.andstuff.kraken.api.neo.model.KrakenResponse;
import dev.andstuff.kraken.api.neo.model.endpoint.Endpoint;
import dev.andstuff.kraken.api.neo.model.endpoint.PrivateEndpoint;

public class JDKKrakenRestRequester implements KrakenRestRequester {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
    }

    private final KrakenCredentials credentials;

    public JDKKrakenRestRequester() {
        this.credentials = null;
    }

    public JDKKrakenRestRequester(String key, String secret) {
        this.credentials = new KrakenCredentials(key, secret);
    }

    @Override
    public <T> T execute(Endpoint<T> endpoint) {
        try {
            URL url = endpoint.buildURL();
            System.out.printf("Fetching %s%n", url);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(endpoint.getHttpMethod());
            connection.addRequestProperty("User-Agent", "github.com/nyg");

            if (endpoint instanceof PrivateEndpoint<T> privateEndpoint) {
                connection.addRequestProperty("API-Key", credentials.key());
                connection.addRequestProperty("API-Sign", privateEndpoint.postParams().sign());

                // write POST data to request
                connection.setDoOutput(true);
                try (OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream())) {
                    out.write(privateEndpoint.postParams());
                }
            }

            JavaType krakenResponseType = endpoint.wrappedResponseType(OBJECT_MAPPER.getTypeFactory());
            KrakenResponse<T> response = OBJECT_MAPPER.readValue(connection.getInputStream(), krakenResponseType);
            return response.result().orElseThrow(() -> new KrakenException(response.error()));
        }
        catch (IOException e) {
            throw new IllegalStateException("Error while making request to Kraken API", e);
        }
    }
}
