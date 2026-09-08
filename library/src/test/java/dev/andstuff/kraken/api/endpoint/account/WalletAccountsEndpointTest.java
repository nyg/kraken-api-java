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
class WalletAccountsEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        WalletAccountsEndpoint unit = new WalletAccountsEndpoint(WalletAccountsParams.builder().build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/ListWalletAccounts");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_omit_optional_values_when_not_supplied() {
        WalletAccountsEndpoint unit = new WalletAccountsEndpoint();

        assertThat(unit.encodedParamsWith("123")).isEqualTo("nonce=123");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        WalletAccountsEndpoint unit = new WalletAccountsEndpoint(WalletAccountsParams.builder().build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModule(new Jdk8Module()).build();
        String json = Files.readString(Path.of("src/test/resources/account/ListWalletAccounts.json"));

        KrakenResponse<WalletAccounts> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        WalletAccounts result = unit.unwrapResponse(response);

        assertThat(result.accounts().getFirst().flags().active()).isTrue();
        assertThat(result.accounts().getFirst().status()).isEqualTo(WalletAccounts.Status.ACTIVE);
        assertThat(result.cursor().next()).isNull();
    }
}
