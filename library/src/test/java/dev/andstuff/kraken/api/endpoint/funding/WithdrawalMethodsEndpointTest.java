package dev.andstuff.kraken.api.endpoint.funding;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import dev.andstuff.kraken.api.endpoint.KrakenException;
import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.funding.params.*;
import dev.andstuff.kraken.api.endpoint.funding.response.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class WithdrawalMethodsEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        WithdrawalMethodsEndpoint unit = new WithdrawalMethodsEndpoint(WithdrawalMethodsParams.builder().asset("id +/&=").assetClass(AssetClass.TOKENIZED_ASSET).network("id +/&=").rebaseMultiplier(RebaseMultiplier.BASE).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("asset", "id +/&="),
                Map.entry("aclass", "tokenized_asset"),
                Map.entry("network", "id +/&="),
                Map.entry("rebase_multiplier", "base")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/WithdrawMethods");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_omit_optional_values_when_not_supplied() {
        WithdrawalMethodsEndpoint unit = new WithdrawalMethodsEndpoint();

        assertThat(unit.encodedParamsWith("123")).isEqualTo("nonce=123");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        WithdrawalMethodsEndpoint unit = new WithdrawalMethodsEndpoint(WithdrawalMethodsParams.builder().build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModule(new Jdk8Module()).build();
        String json = Files.readString(Path.of("src/test/resources/funding/WithdrawMethods.json"));

        KrakenResponse<List<WithdrawalMethod>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        List<WithdrawalMethod> result = response.result().orElseThrow();

        assertThat(result.getFirst().fee().fee()).isEqualByComparingTo("0.00001500");
        assertThat(result.getLast().fee().feePercentage()).isEqualByComparingTo("0.1");
        assertThat(result.getFirst().networkId()).isEqualTo("ee9d686d-aeb6-4e61-9d83-448e3a7511f3");
    }

    @Test
    void should_parse_limits_by_window_when_method_has_rate_limits() throws Exception {
        WithdrawalMethodsEndpoint unit = new WithdrawalMethodsEndpoint();
        JsonMapper mapper = JsonMapper.builder().addModule(new Jdk8Module()).build();
        String json = """
                {"error":[],"result":[{"asset":"XXBT","limits":[{"description":"Daily","limit_type":"amount","limits":{"86400":{"maximum":"2.00000000","remaining":"1.90000000","used":"0.10000000"}}}]}]}
                """;

        KrakenResponse<List<WithdrawalMethod>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        WithdrawalMethod.LimitWindow result = response.result().orElseThrow().getFirst().limits().getFirst().limits().get("86400");

        assertThat(result.maximum()).isEqualByComparingTo("2.00000000");
        assertThat(result.remaining()).isEqualByComparingTo("1.90000000");
        assertThat(result.used()).isEqualByComparingTo("0.10000000");
    }
}
