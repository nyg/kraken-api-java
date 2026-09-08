package dev.andstuff.kraken.api.endpoint.account;

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
import dev.andstuff.kraken.api.endpoint.account.params.*;
import dev.andstuff.kraken.api.endpoint.account.response.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class AccountBalanceEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        AccountBalanceEndpoint unit = new AccountBalanceEndpoint(AccountBalanceParams.builder().rebaseMultiplier(RebaseMultiplier.BASE).accountId("id +/&=").build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("rebase_multiplier", "base")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/Balance");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_omit_optional_values_when_not_supplied() {
        AccountBalanceEndpoint unit = new AccountBalanceEndpoint();

        assertThat(unit.encodedParamsWith("123")).isEqualTo("nonce=123");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        AccountBalanceEndpoint unit = new AccountBalanceEndpoint(AccountBalanceParams.builder().build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModule(new Jdk8Module()).build();
        String json = Files.readString(Path.of("src/test/resources/account/Balance.json"));

        KrakenResponse<Map<String, BigDecimal>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Map<String, BigDecimal> result = unit.unwrapResponse(response);

        assertThat(result.get("ZUSD")).isEqualByComparingTo("171288.6158");
        assertThat(result.get("XXBT")).isEqualByComparingTo("1011.1908877900");
    }

    @Test
    void should_encode_wallet_in_query_when_wallet_is_selected() {
        AccountBalanceEndpoint unit = new AccountBalanceEndpoint(AccountBalanceParams.builder().accountId("wallet +/&=").build());

        assertThat(unit.buildURL().getQuery()).isEqualTo("account_id=wallet+%2B%2F%26%3D");
        assertThat(unit.encodedParamsWith("123")).isEqualTo("nonce=123");
    }
}
