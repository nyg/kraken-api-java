package dev.andstuff.kraken.api.endpoint.funding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
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
import dev.andstuff.kraken.api.endpoint.funding.params.AssetClass;
import dev.andstuff.kraken.api.endpoint.funding.params.DepositMethodsParams;
import dev.andstuff.kraken.api.endpoint.funding.response.DepositMethod;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;

@ExtendWith(MockitoExtension.class)
class DepositMethodsEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        DepositMethodsEndpoint unit = new DepositMethodsEndpoint(DepositMethodsParams.builder().asset("id +/&=").assetClass(AssetClass.TOKENIZED_ASSET).rebaseMultiplier(RebaseMultiplier.BASE).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("asset", "id +/&="),
                Map.entry("aclass", "tokenized_asset"),
                Map.entry("rebase_multiplier", "base")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/DepositMethods");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_reject_missing_asset_when_building_parameters() {
        DepositMethodsParams.DepositMethodsParamsBuilder builder = DepositMethodsParams.builder();

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("asset");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        DepositMethodsEndpoint unit = new DepositMethodsEndpoint(DepositMethodsParams.builder().asset("id +/&=").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/funding/DepositMethods.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<List<DepositMethod>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        List<DepositMethod> result = response.result().orElseThrow();

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().limit().unlimited()).isTrue();
        assertThat(result.getFirst().limit().amount()).isNull();
        assertThat(result.getFirst().fee()).isEqualByComparingTo("0.0000000000");
        assertThat(result.getFirst().minimum()).isEqualByComparingTo("0.00010000");
    }

    @Test
    void should_preserve_finite_limits_when_strings_or_numbers_are_returned() throws Exception {
        DepositMethodsEndpoint unit = new DepositMethodsEndpoint("XBT");
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":[{"limit":"0.0000000000123456789","fee-percentage":"0.1"},{"limit":1234567890.1234567890123456789},{"limit":0},{"limit":null},{}]}
                """;

        KrakenResponse<List<DepositMethod>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        List<DepositMethod> result = response.result().orElseThrow();

        assertThat(result.getFirst().limit().amount()).isEqualByComparingTo("0.0000000000123456789");
        assertThat(result.getFirst().limit().unlimited()).isFalse();
        assertThat(result.getFirst().feePercentage()).isEqualByComparingTo("0.1");
        assertThat(result.get(1).limit().amount()).isEqualByComparingTo("1234567890.1234567890123456789");
        assertThat(result.get(2).limit().amount()).isZero();
        assertThat(result.get(2).limit().unlimited()).isFalse();
        assertThat(result.get(3).limit()).isNull();
        assertThat(result.getLast().limit()).isNull();
    }

    @Test
    void should_reject_invalid_limit_when_true_is_returned() {
        DepositMethodsEndpoint unit = new DepositMethodsEndpoint("XBT");
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module()).build();

        assertThatThrownBy(() -> mapper.readValue("{\"error\":[],\"result\":[{\"limit\":true}]}", unit.wrappedResponseType(mapper.getTypeFactory())))
                .isInstanceOf(com.fasterxml.jackson.databind.JsonMappingException.class);
    }
}
