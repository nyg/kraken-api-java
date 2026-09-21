package dev.andstuff.kraken.api.endpoint.market;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.market.response.ServerTime;

@ExtendWith(MockitoExtension.class)
class ServerTimeEndpointTest {

    @InjectMocks
    private ServerTimeEndpoint unit;

    @Test
    void should_use_public_get_without_parameters_when_requesting_server_time() {
        URL result = unit.buildURL();

        assertThat(result).hasProtocol("https").hasHost("api.kraken.com").hasPath("/0/public/Time").hasNoParameters();
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
        assertThat(KrakenAPI.Public.TIME.getPath()).isEqualTo("Time");
    }

    @Test
    void should_read_both_time_representations_when_decoding_documented_response() throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/time.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<ServerTime> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        ServerTime result = unit.unwrapResponse(response);

        assertThat(result).isEqualTo(new ServerTime(1688669448L, "Thu, 06 Jul 23 18:50:48 +0000"));
    }
}
