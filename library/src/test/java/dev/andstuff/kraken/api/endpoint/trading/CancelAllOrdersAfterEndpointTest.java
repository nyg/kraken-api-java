package dev.andstuff.kraken.api.endpoint.trading;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.trading.params.CancelAllOrdersAfterParams;
import dev.andstuff.kraken.api.endpoint.trading.response.DeadMansSwitch;

@ExtendWith(MockitoExtension.class)
class CancelAllOrdersAfterEndpointTest {

    @Test
    void should_encode_timeout_in_seconds_when_supplied() {
        CancelAllOrdersAfterEndpoint unit = new CancelAllOrdersAfterEndpoint(Duration.ofMinutes(2));

        String result = unit.encodedParamsWith("123");

        assertThat(result.split("&")).containsExactlyInAnyOrder("timeout=120", "nonce=123");
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/CancelAllOrdersAfter");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_encode_zero_timeout_when_disabling_timer() {
        CancelAllOrdersAfterEndpoint unit = new CancelAllOrdersAfterEndpoint(CancelAllOrdersAfterParams.builder().timeout(Duration.ZERO).build());

        String result = unit.encodedParamsWith("123");

        assertThat(result.split("&")).containsExactlyInAnyOrder("timeout=0", "nonce=123");
    }

    @Test
    void should_reject_missing_timeout_when_building_parameters() {
        CancelAllOrdersAfterParams.CancelAllOrdersAfterParamsBuilder builder = CancelAllOrdersAfterParams.builder();

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("timeout");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        CancelAllOrdersAfterEndpoint unit = new CancelAllOrdersAfterEndpoint(Duration.ofSeconds(60));
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/trading/CancelAllOrdersAfter.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<DeadMansSwitch> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        DeadMansSwitch result = unit.unwrapResponse(response);

        assertThat(result.currentTime()).isEqualTo(Instant.parse("2023-03-24T17:41:56Z"));
        assertThat(result.triggerTime()).isEqualTo(Instant.parse("2023-03-24T17:42:56Z"));
    }

    @Test
    void should_return_null_trigger_time_when_timer_is_disabled() throws Exception {
        CancelAllOrdersAfterEndpoint unit = new CancelAllOrdersAfterEndpoint(Duration.ZERO);
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module()).build();

        KrakenResponse<DeadMansSwitch> response = mapper.readValue("{\"error\":[],\"result\":{\"currentTime\":\"2023-03-24T17:41:56Z\",\"triggerTime\":\"0\"}}", unit.wrappedResponseType(mapper.getTypeFactory()));
        DeadMansSwitch result = unit.unwrapResponse(response);

        assertThat(result.currentTime()).isEqualTo(Instant.parse("2023-03-24T17:41:56Z"));
        assertThat(result.triggerTime()).isNull();
    }
}
