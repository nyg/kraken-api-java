package dev.andstuff.kraken.api.endpoint.trading;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.trading.response.WebSocketsToken;

@ExtendWith(MockitoExtension.class)
class WebSocketsTokenEndpointTest {

    @Test
    void should_send_only_nonce_when_encoding_request() {
        WebSocketsTokenEndpoint unit = new WebSocketsTokenEndpoint();

        String result = unit.encodedParamsWith("123");

        assertThat(result).isEqualTo("nonce=123");
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/GetWebSocketsToken");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        WebSocketsTokenEndpoint unit = new WebSocketsTokenEndpoint();
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/trading/GetWebSocketsToken.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<WebSocketsToken> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        WebSocketsToken result = unit.unwrapResponse(response);

        assertThat(result.token()).isEqualTo("1Dwc4lzSwNWOAwkMdqhssNNFhs1ed606d1WcF3XfEMw");
        assertThat(result.expires()).isEqualTo(Duration.ofMinutes(15));
    }
}
