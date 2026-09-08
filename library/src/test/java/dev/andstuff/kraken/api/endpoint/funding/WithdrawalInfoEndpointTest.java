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
class WithdrawalInfoEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        WithdrawalInfoEndpoint unit = new WithdrawalInfoEndpoint(WithdrawalInfoParams.builder().asset("id +/&=").key("id +/&=").amount(new BigDecimal("0.0000000012300")).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("asset", "id +/&="),
                Map.entry("key", "id +/&="),
                Map.entry("amount", "0.0000000012300")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/WithdrawInfo");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_reject_missing_asset_when_building_parameters() {
        assertThatThrownBy(() -> WithdrawalInfoParams.builder().key("id +/&=").amount(new BigDecimal("0.0000000012300")).build()).isInstanceOf(NullPointerException.class).hasMessageContaining("asset");
    }

    @Test
    void should_reject_missing_key_when_building_parameters() {
        assertThatThrownBy(() -> WithdrawalInfoParams.builder().asset("id +/&=").amount(new BigDecimal("0.0000000012300")).build()).isInstanceOf(NullPointerException.class).hasMessageContaining("key");
    }

    @Test
    void should_reject_missing_amount_when_building_parameters() {
        assertThatThrownBy(() -> WithdrawalInfoParams.builder().asset("id +/&=").key("id +/&=").build()).isInstanceOf(NullPointerException.class).hasMessageContaining("amount");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        WithdrawalInfoEndpoint unit = new WithdrawalInfoEndpoint(WithdrawalInfoParams.builder().asset("id +/&=").key("id +/&=").amount(new BigDecimal("0.0000000012300")).build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModule(new Jdk8Module()).build();
        String json = Files.readString(Path.of("src/test/resources/funding/WithdrawInfo.json"));

        KrakenResponse<WithdrawalInfo> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        WithdrawalInfo result = response.result().orElseThrow();

        assertThat(result.limit()).isEqualByComparingTo("332.00956139");
        assertThat(result.amount()).isEqualByComparingTo("0.72485000");
        assertThat(result.fee()).isEqualByComparingTo("0.00020000");
    }
}
