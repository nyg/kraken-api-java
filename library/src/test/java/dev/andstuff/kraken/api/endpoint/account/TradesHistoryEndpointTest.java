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
class TradesHistoryEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        TradesHistoryEndpoint unit = new TradesHistoryEndpoint(TradesHistoryParams.builder().type(TradeType.NO_POSITION).trades(false).start("1700000000").end("1700000000").offset(0).withoutCount(false).consolidateTaker(false).ledgers(false).rebaseMultiplier(RebaseMultiplier.BASE).assetClass(AssetClass.EXTERNAL_PAIR).pair("id +/&=").limit(0).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("type", "no position"),
                Map.entry("trades", "false"),
                Map.entry("start", "1700000000"),
                Map.entry("end", "1700000000"),
                Map.entry("ofs", "0"),
                Map.entry("without_count", "false"),
                Map.entry("consolidate_taker", "false"),
                Map.entry("ledgers", "false"),
                Map.entry("rebase_multiplier", "base"),
                Map.entry("aclass", "external_pair"),
                Map.entry("pair", "id +/&="),
                Map.entry("limit", "0")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/TradesHistory");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_omit_optional_values_when_not_supplied() {
        TradesHistoryEndpoint unit = new TradesHistoryEndpoint();

        assertThat(unit.encodedParamsWith("123")).isEqualTo("nonce=123");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        TradesHistoryEndpoint unit = new TradesHistoryEndpoint(TradesHistoryParams.builder().build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModule(new Jdk8Module()).build();
        String json = Files.readString(Path.of("src/test/resources/account/TradesHistory.json"));

        KrakenResponse<TradesHistory> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        TradesHistory result = unit.unwrapResponse(response);

        assertThat(result.trades().get("THVRQM-33VKH-UCI7BS").time()).isEqualByComparingTo("1688667796.8802");
        assertThat(result.trades().get("THVRQM-33VKH-UCI7BS").volume()).isEqualByComparingTo("0.02000000");
        assertThat(result.count()).isNull();
    }
}
