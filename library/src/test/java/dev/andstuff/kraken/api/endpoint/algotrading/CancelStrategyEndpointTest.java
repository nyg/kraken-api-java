package dev.andstuff.kraken.api.endpoint.algotrading;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.algotrading.params.CancelStrategyParams;
import dev.andstuff.kraken.api.endpoint.algotrading.response.StrategyCancelled;

@ExtendWith(MockitoExtension.class)
class CancelStrategyEndpointTest {

    @Test
    void should_encode_strategy_identifier_as_json_when_cancelling() {
        // Given
        CancelStrategyEndpoint unit = new CancelStrategyEndpoint("ASMBA64-R23M2-J7JRJV");

        // When
        String result = unit.encodedParamsWith("1781169958000");

        // Then
        assertThat(result).isEqualTo("{\"strategy_id\":\"ASMBA64-R23M2-J7JRJV\",\"nonce\":1781169958000}");
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/CancelStrategy");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/json");
    }

    @Test
    void should_reject_missing_strategyId_when_building_parameters() {
        // Given
        CancelStrategyParams.CancelStrategyParamsBuilder builder = CancelStrategyParams.builder();

        // When
        Throwable result = catchThrowable(builder::build);

        // Then
        assertThat(result).isInstanceOf(NullPointerException.class).hasMessageContaining("strategyId");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        // Given
        CancelStrategyEndpoint unit = new CancelStrategyEndpoint(CancelStrategyParams.builder().strategyId("ASMBA64-R23M2-J7JRJV").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/algotrading/CancelStrategy.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        // When
        KrakenResponse<StrategyCancelled> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        StrategyCancelled result = unit.unwrapResponse(response);

        // Then
        assertThat(result.strategyId()).isEqualTo("ASMBA64-R23M2-J7JRJV");
    }
}
