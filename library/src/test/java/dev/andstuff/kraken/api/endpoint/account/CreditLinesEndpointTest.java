package dev.andstuff.kraken.api.endpoint.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.KrakenException;
import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.account.params.CreditLinesParams;
import dev.andstuff.kraken.api.endpoint.account.response.CreditLines;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;

@ExtendWith(MockitoExtension.class)
class CreditLinesEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        CreditLinesEndpoint unit = new CreditLinesEndpoint(CreditLinesParams.builder().rebaseMultiplier(RebaseMultiplier.BASE).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("rebase_multiplier", "base")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/CreditLines");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_omit_optional_values_when_not_supplied() {
        CreditLinesEndpoint unit = new CreditLinesEndpoint();

        assertThat(unit.encodedParamsWith("123")).isEqualTo("nonce=123");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        CreditLinesEndpoint unit = new CreditLinesEndpoint(CreditLinesParams.builder().build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/account/CreditLines.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<Optional<CreditLines>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        CreditLines result = unit.unwrapResponse(response).orElseThrow();

        assertThat(result.assetDetails().get("USD").availableCredit()).isEqualByComparingTo("37500.0000");
        assertThat(result.limitsMonitor().debtToEquity()).isEqualByComparingTo("0.2000");
    }

    @Test
    void should_return_null_when_no_credit_lines_exist() throws Exception {
        CreditLinesEndpoint unit = new CreditLinesEndpoint();
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module()).build();
        KrakenResponse<Optional<CreditLines>> response = mapper.readValue("{\"error\":[],\"result\":null}", unit.wrappedResponseType(mapper.getTypeFactory()));

        assertThat(unit.unwrapResponse(response)).isEmpty();
    }

    @Test
    void should_throw_kraken_error_when_credit_request_is_rejected() throws Exception {
        CreditLinesEndpoint unit = new CreditLinesEndpoint();
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module()).build();
        KrakenResponse<Optional<CreditLines>> response = mapper.readValue("{\"error\":[\"EGeneral:Permission denied\"]}", unit.wrappedResponseType(mapper.getTypeFactory()));

        assertThatThrownBy(() -> unit.unwrapResponse(response)).isInstanceOf(KrakenException.class);
    }
}
