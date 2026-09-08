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
class QueryOrdersEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        QueryOrdersEndpoint unit = new QueryOrdersEndpoint(QueryOrdersParams.builder().trades(false).userReference(0L).transactionIds(List.of("ID-1", "ID+2")).consolidateTaker(false).rebaseMultiplier(RebaseMultiplier.BASE).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("trades", "false"),
                Map.entry("userref", "0"),
                Map.entry("txid", "ID-1,ID+2"),
                Map.entry("consolidate_taker", "false"),
                Map.entry("rebase_multiplier", "base")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/QueryOrders");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_reject_missing_transactionIds_when_building_parameters() {
        assertThatThrownBy(() -> QueryOrdersParams.builder().build()).isInstanceOf(NullPointerException.class).hasMessageContaining("transactionIds");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        QueryOrdersEndpoint unit = new QueryOrdersEndpoint(QueryOrdersParams.builder().transactionIds(List.of("ID-1", "ID+2")).build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModule(new Jdk8Module()).build();
        String json = Files.readString(Path.of("src/test/resources/account/QueryOrders.json"));

        KrakenResponse<Map<String, Order>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Map<String, Order> result = unit.unwrapResponse(response);

        assertThat(result.get("OBCMZD-JIEE7-77TH3F").closeTime()).isEqualByComparingTo("1688665499.1922");
        assertThat(result.get("OBCMZD-JIEE7-77TH3F").reason()).isNull();
    }
}
