package dev.andstuff.kraken.api.endpoint.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.InputStream;
import java.net.URL;
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
import dev.andstuff.kraken.api.endpoint.account.params.Consolidation;
import dev.andstuff.kraken.api.endpoint.account.params.OpenPositionsParams;
import dev.andstuff.kraken.api.endpoint.account.response.OpenPosition;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;

@ExtendWith(MockitoExtension.class)
class OpenPositionsEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        OpenPositionsEndpoint unit = new OpenPositionsEndpoint(OpenPositionsParams.builder().transactionIds(List.of("ID-1", "ID+2")).calculateValues(false).rebaseMultiplier(RebaseMultiplier.BASE).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("txid", "ID-1,ID+2"),
                Map.entry("docalcs", "false"),
                Map.entry("rebase_multiplier", "base")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/OpenPositions");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_omit_optional_values_when_not_supplied() {
        OpenPositionsEndpoint unit = new OpenPositionsEndpoint();

        assertThat(unit.encodedParamsWith("123")).isEqualTo("nonce=123");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        OpenPositionsEndpoint unit = new OpenPositionsEndpoint(OpenPositionsParams.builder().build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/account/OpenPositions.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<Map<String, OpenPosition>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Map<String, OpenPosition> result = unit.unwrapResponse(response);

        assertThat(result.get("TF5GVO-T7ZZ2-6NBKBI").net()).isEqualByComparingTo("154186.9728");
        assertThat(result.get("TF5GVO-T7ZZ2-6NBKBI").closedVolume()).isEqualByComparingTo("0.20200000");
        assertThat(result.get("TF5GVO-T7ZZ2-6NBKBI").rolloverTime()).isEqualTo("1616672637");
    }

    @Test
    void should_encode_wallet_in_query_when_wallet_is_selected() {
        // Given
        OpenPositionsEndpoint unit = new OpenPositionsEndpoint(OpenPositionsParams.builder().accountId("WX6V-JUKW-KKPB-QE36").build());

        // When
        URL result = unit.buildURL();

        // Then
        assertThat(result).hasPath("/0/private/OpenPositions").hasQuery("account_id=WX6V-JUKW-KKPB-QE36");
        assertThat(unit.encodedParamsWith("123")).isEqualTo("nonce=123");
    }

    @Test
    void should_reject_market_consolidation_when_reading_positions_by_transaction() {
        // Given
        OpenPositionsParams params = OpenPositionsParams.builder().consolidation(Consolidation.MARKET).build();

        // When
        Throwable result = catchThrowable(() -> new OpenPositionsEndpoint(params));

        // Then
        assertThat(result).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("ConsolidatedPositionsEndpoint");
    }
}
