package dev.andstuff.kraken.api.endpoint.funding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.math.BigDecimal;
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
import dev.andstuff.kraken.api.endpoint.funding.params.DepositAddressesParams;
import dev.andstuff.kraken.api.endpoint.funding.response.DepositAddress;

@ExtendWith(MockitoExtension.class)
class DepositAddressesEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        DepositAddressesEndpoint unit = new DepositAddressesEndpoint(DepositAddressesParams.builder().asset("id +/&=").assetClass(AssetClass.TOKENIZED_ASSET).method("id +/&=").generateNew(false).amount(new BigDecimal("0.0000000012300")).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("asset", "id +/&="),
                Map.entry("aclass", "tokenized_asset"),
                Map.entry("method", "id +/&="),
                Map.entry("new", "false"),
                Map.entry("amount", "0.0000000012300")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/DepositAddresses");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_reject_missing_asset_when_building_parameters() {
        assertThatThrownBy(() -> DepositAddressesParams.builder().method("id +/&=").build()).isInstanceOf(NullPointerException.class).hasMessageContaining("asset");
    }

    @Test
    void should_reject_missing_method_when_building_parameters() {
        assertThatThrownBy(() -> DepositAddressesParams.builder().asset("id +/&=").build()).isInstanceOf(NullPointerException.class).hasMessageContaining("method");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        DepositAddressesEndpoint unit = new DepositAddressesEndpoint(DepositAddressesParams.builder().asset("id +/&=").method("id +/&=").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/funding/DepositAddresses.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<List<DepositAddress>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        List<DepositAddress> result = response.result().orElseThrow();

        assertThat(result).hasSize(5);
        assertThat(result.getFirst().unused()).isTrue();
        assertThat(result.get(2).unused()).isNull();
        assertThat(result.get(3).tag()).isEqualTo("1361101127");
        assertThat(result.getLast().memo()).isEqualTo("4150096490");
        assertThat(result.getFirst().expireTime()).isEqualTo("0");
    }
}
