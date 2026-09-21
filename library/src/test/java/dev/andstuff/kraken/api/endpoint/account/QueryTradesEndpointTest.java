package dev.andstuff.kraken.api.endpoint.account;

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
import dev.andstuff.kraken.api.endpoint.account.params.QueryTradesParams;
import dev.andstuff.kraken.api.endpoint.account.response.AccountTrade;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;

@ExtendWith(MockitoExtension.class)
class QueryTradesEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        QueryTradesEndpoint unit = new QueryTradesEndpoint(QueryTradesParams.builder().transactionIds(List.of("ID-1", "ID+2")).trades(false).rebaseMultiplier(RebaseMultiplier.BASE).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("txid", "ID-1,ID+2"),
                Map.entry("trades", "false"),
                Map.entry("rebase_multiplier", "base")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/QueryTrades");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_reject_missing_transactionIds_when_building_parameters() {
        QueryTradesParams.QueryTradesParamsBuilder builder = QueryTradesParams.builder();

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("transactionIds");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        QueryTradesEndpoint unit = new QueryTradesEndpoint(QueryTradesParams.builder().transactionIds(List.of("ID-1", "ID+2")).build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/account/QueryTrades.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<Map<String, AccountTrade>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Map<String, AccountTrade> result = unit.unwrapResponse(response);

        assertThat(result.get("TTEUX3-HDAAA-RC2RUO").tradeId()).isEqualTo(74625834L);
        assertThat(result.get("TTEUX3-HDAAA-RC2RUO").volume()).isEqualByComparingTo("0.00020000");
    }
}
