package dev.andstuff.kraken.api.endpoint.funding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
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
import dev.andstuff.kraken.api.endpoint.funding.params.CancelWithdrawalParams;

@ExtendWith(MockitoExtension.class)
class CancelWithdrawalEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        CancelWithdrawalEndpoint unit = new CancelWithdrawalEndpoint(CancelWithdrawalParams.builder().asset("id +/&=").referenceId("id +/&=").build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("asset", "id +/&="),
                Map.entry("refid", "id +/&=")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/WithdrawCancel");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_reject_missing_asset_when_building_parameters() {
        CancelWithdrawalParams.CancelWithdrawalParamsBuilder builder = CancelWithdrawalParams.builder().referenceId("id +/&=");

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("asset");
    }

    @Test
    void should_reject_missing_referenceId_when_building_parameters() {
        CancelWithdrawalParams.CancelWithdrawalParamsBuilder builder = CancelWithdrawalParams.builder().asset("id +/&=");

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("referenceId");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        CancelWithdrawalEndpoint unit = new CancelWithdrawalEndpoint(CancelWithdrawalParams.builder().asset("id +/&=").referenceId("id +/&=").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/funding/WithdrawCancel.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<Boolean> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Boolean result = response.result().orElseThrow();

        assertThat(result).isTrue();
    }

    @Test
    void should_preserve_false_when_cancellation_did_not_succeed() throws Exception {
        CancelWithdrawalEndpoint unit = new CancelWithdrawalEndpoint("XBT", "reference");
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module()).build();

        KrakenResponse<Boolean> response = mapper.readValue("{\"error\":[],\"result\":false}", unit.wrappedResponseType(mapper.getTypeFactory()));

        assertThat(response.result()).contains(false);
    }
}
