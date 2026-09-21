package dev.andstuff.kraken.api.endpoint.earn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.earn.params.EarnAllocationParams;

@ExtendWith(MockitoExtension.class)
class EarnDeallocateEndpointTest {

    @Test
    void should_encode_strategy_and_plain_amount_when_deallocating_funds() {
        EarnDeallocateEndpoint unit = new EarnDeallocateEndpoint(EarnAllocationParams.of("ESRFUO3-Q62XD-WIOIL7", new BigDecimal("1.2E-9")));

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of("nonce", "123456789", "strategy_id", "ESRFUO3-Q62XD-WIOIL7", "amount", "0.0000000012"));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/Earn/Deallocate");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/x-www-form-urlencoded");
    }

    @Test
    void should_reject_missing_strategyId_when_building_parameters() {
        assertThatThrownBy(() -> EarnAllocationParams.of(null, BigDecimal.ONE)).isInstanceOf(NullPointerException.class).hasMessageContaining("strategyId");
    }

    @Test
    void should_reject_missing_amount_when_building_parameters() {
        assertThatThrownBy(() -> EarnAllocationParams.of("ESRFUO3-Q62XD-WIOIL7", null)).isInstanceOf(NullPointerException.class).hasMessageContaining("amount");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        EarnDeallocateEndpoint unit = new EarnDeallocateEndpoint(EarnAllocationParams.of("ESRFUO3-Q62XD-WIOIL7", BigDecimal.ONE));
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/earn/Deallocate.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<Boolean> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Boolean result = unit.unwrapResponse(response);

        assertThat(result).isTrue();
    }
}
