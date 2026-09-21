package dev.andstuff.kraken.api.endpoint.market;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

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
import dev.andstuff.kraken.api.endpoint.market.response.SystemStatus;

@ExtendWith(MockitoExtension.class)
class SystemStatusEndpointTest {

    @InjectMocks
    private SystemStatusEndpoint unit;

    @Test
    void should_use_public_get_without_parameters_when_requesting_system_status() {
        URL result = unit.buildURL();

        assertThat(result).hasProtocol("https").hasHost("api.kraken.com").hasPath("/0/public/SystemStatus").hasNoParameters();
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
        assertThat(KrakenAPI.Public.SYSTEM_STATUS.getPath()).isEqualTo("SystemStatus");
    }

    @Test
    void should_read_status_and_timestamp_when_decoding_documented_emergency_response() throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/system-status.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<SystemStatus> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        SystemStatus result = unit.unwrapResponse(response);

        assertThat(result).isEqualTo(new SystemStatus(SystemStatus.Description.CANCEL_ONLY, Instant.parse("2026-05-08T14:28:00Z")));
    }

    @Test
    void should_read_online_status_when_system_operates_normally() throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"status":"online","timestamp":"2023-07-06T18:52:00Z","upcoming_maintenance":[],"emergency":[]}}
                """;

        KrakenResponse<SystemStatus> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        SystemStatus result = unit.unwrapResponse(response);

        assertThat(result.status()).isEqualTo(SystemStatus.Description.ONLINE);
    }

    @Test
    void should_fall_back_to_unknown_status_when_kraken_adds_a_trading_mode() throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"status":"future-mode","timestamp":"2023-07-06T18:52:00Z"}}
                """;

        KrakenResponse<SystemStatus> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        SystemStatus result = unit.unwrapResponse(response);

        assertThat(result.status()).isEqualTo(SystemStatus.Description.UNKNOWN);
    }
}
