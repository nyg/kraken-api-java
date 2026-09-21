package dev.andstuff.kraken.api.endpoint.earn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import dev.andstuff.kraken.api.endpoint.earn.params.EarnStatusParams;
import dev.andstuff.kraken.api.endpoint.earn.response.AllocationStatus;

@ExtendWith(MockitoExtension.class)
class EarnAllocateStatusEndpointTest {

    @Test
    void should_encode_strategy_when_polling_allocation_status() {
        EarnAllocateStatusEndpoint unit = new EarnAllocateStatusEndpoint(EarnStatusParams.of("ESRFUO3-Q62XD-WIOIL7"));

        String result = unit.encodedParamsWith("123");

        assertThat(result).isEqualTo("strategy_id=ESRFUO3-Q62XD-WIOIL7&nonce=123");
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/Earn/AllocateStatus");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/x-www-form-urlencoded");
    }

    @Test
    void should_reject_missing_strategyId_when_building_parameters() {
        assertThatThrownBy(() -> EarnStatusParams.of(null)).isInstanceOf(NullPointerException.class).hasMessageContaining("strategyId");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        EarnAllocateStatusEndpoint unit = new EarnAllocateStatusEndpoint(EarnStatusParams.of("ESRFUO3-Q62XD-WIOIL7"));
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/earn/AllocateStatus.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<AllocationStatus> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        AllocationStatus result = unit.unwrapResponse(response);

        assertThat(result.pending()).isFalse();
    }

    @Test
    void should_report_pending_allocation_when_kraken_is_still_processing() throws Exception {
        EarnAllocateStatusEndpoint unit = new EarnAllocateStatusEndpoint(EarnStatusParams.of("ESRFUO3-Q62XD-WIOIL7"));
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":{"pending":true}}
                """;

        KrakenResponse<AllocationStatus> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        AllocationStatus result = unit.unwrapResponse(response);

        assertThat(result.pending()).isTrue();
    }
}
