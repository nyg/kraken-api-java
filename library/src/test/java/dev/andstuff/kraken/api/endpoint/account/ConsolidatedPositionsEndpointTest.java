package dev.andstuff.kraken.api.endpoint.account;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
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
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.account.params.OpenPositionsParams;
import dev.andstuff.kraken.api.endpoint.account.response.ConsolidatedPosition;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;

@ExtendWith(MockitoExtension.class)
class ConsolidatedPositionsEndpointTest {

    @Test
    void should_encode_market_consolidation_when_supplied_options_omit_it() {
        // Given
        ConsolidatedPositionsEndpoint unit = new ConsolidatedPositionsEndpoint(OpenPositionsParams.builder().transactionIds(List.of("TF5GVO-T7ZZ2-6NBKBI", "T24DOR-TAFLM-ID3NYP"))
                .calculateValues(true).rebaseMultiplier(RebaseMultiplier.BASE).build());

        // When
        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        // Then
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("txid", "TF5GVO-T7ZZ2-6NBKBI,T24DOR-TAFLM-ID3NYP"),
                Map.entry("docalcs", "true"),
                Map.entry("consolidation", "market"),
                Map.entry("rebase_multiplier", "base")));
        assertThat(unit.buildURL()).hasPath("/0/private/OpenPositions").hasNoQuery();
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_encode_only_market_consolidation_when_using_default_options() {
        // Given
        ConsolidatedPositionsEndpoint unit = new ConsolidatedPositionsEndpoint();

        // When
        String result = unit.encodedParamsWith("123");

        // Then
        assertThat(result).isEqualTo("consolidation=market&nonce=123");
    }

    @Test
    void should_encode_wallet_in_query_when_wallet_is_selected() {
        // Given
        ConsolidatedPositionsEndpoint unit = new ConsolidatedPositionsEndpoint(OpenPositionsParams.builder().accountId("WX6V-JUKW-KKPB-QE36").build());

        // When
        URL result = unit.buildURL();

        // Then
        assertThat(result).hasPath("/0/private/OpenPositions").hasQuery("account_id=WX6V-JUKW-KKPB-QE36");
    }

    @Test
    void should_deserialize_positions_by_pair_when_reading_consolidated_response() throws Exception {
        // Given
        ConsolidatedPositionsEndpoint unit = new ConsolidatedPositionsEndpoint();
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
        String json = """
                {"error":[],"result":[{"pair":"XXBTZUSD","class":"margin","positions":"2","type":"buy","leverage":"5.00000","cost":"250367.29698","fee":"624.30622",
                "vol":"16.82412861","vol_closed":"0.20200000","margin":"50073.45939","value":"498921.5","net":"+248554.2042"}]}
                """;
        KrakenResponse<List<ConsolidatedPosition>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));

        // When
        List<ConsolidatedPosition> result = unit.unwrapResponse(response);

        // Then
        assertThat(result).containsExactly(new ConsolidatedPosition("XXBTZUSD", "margin", 2, "buy", "5.00000", new BigDecimal("250367.29698"),
                new BigDecimal("624.30622"), new BigDecimal("16.82412861"), new BigDecimal("0.20200000"), new BigDecimal("50073.45939"),
                new BigDecimal("498921.5"), new BigDecimal("248554.2042")));
    }
}
