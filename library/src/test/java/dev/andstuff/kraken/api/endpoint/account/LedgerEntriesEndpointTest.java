package dev.andstuff.kraken.api.endpoint.account;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
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
import dev.andstuff.kraken.api.endpoint.account.params.LedgerEntriesParams;
import dev.andstuff.kraken.api.endpoint.account.response.LedgerEntry;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;

@ExtendWith(MockitoExtension.class)
class LedgerEntriesEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        LedgerEntriesEndpoint unit = new LedgerEntriesEndpoint(LedgerEntriesParams.builder()
                .entryIds(List.of("L4UESK-KG3EQ-UFO4T5", "LMKZCZ-Z3GVL-CXKK4H")).includeTrades(true).rebaseMultiplier(RebaseMultiplier.REBASED).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("id", "L4UESK-KG3EQ-UFO4T5,LMKZCZ-Z3GVL-CXKK4H"),
                Map.entry("trades", "true"),
                Map.entry("rebase_multiplier", "rebased")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/QueryLedgers");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/x-www-form-urlencoded");
    }

    @Test
    void should_omit_deprecated_trades_when_not_requested() {
        // Given
        LedgerEntriesEndpoint unit = new LedgerEntriesEndpoint(LedgerEntriesParams.builder().entryIds(List.of("L4UESK-KG3EQ-UFO4T5")).build());

        // When
        String result = unit.encodedParamsWith("123");

        // Then
        assertThat(result).isEqualTo("id=L4UESK-KG3EQ-UFO4T5&nonce=123");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        LedgerEntriesEndpoint unit = new LedgerEntriesEndpoint(LedgerEntriesParams.builder().entryIds(List.of("L4UESK-KG3EQ-UFO4T5")).build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/account/QueryLedgers.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<Map<String, LedgerEntry>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Map<String, LedgerEntry> result = unit.unwrapResponse(response);

        assertThat(result).containsOnlyKeys("L4UESK-KG3EQ-UFO4T5").containsEntry("L4UESK-KG3EQ-UFO4T5", new LedgerEntry(null, "TJKLXF-PGMUI-4NTLXU",
                Instant.ofEpochSecond(1688464484L, 178_700_000L), LedgerEntry.Type.TRADE, "", "currency", null, "ZGBP", null,
                new BigDecimal("-24.5000"), new BigDecimal("0.0490"), new BigDecimal("459567.9171"), null));
    }

    @Test
    void should_encode_wallet_in_query_when_wallet_is_selected() {
        // Given
        LedgerEntriesEndpoint unit = new LedgerEntriesEndpoint(LedgerEntriesParams.builder().entryIds(List.of("L4UESK-KG3EQ-UFO4T5")).accountId("WX6V-JUKW-KKPB-QE36").build());

        // When
        URL result = unit.buildURL();

        // Then
        assertThat(result).hasPath("/0/private/QueryLedgers").hasQuery("account_id=WX6V-JUKW-KKPB-QE36");
        assertThat(unit.encodedParamsWith("123")).isEqualTo("id=L4UESK-KG3EQ-UFO4T5&nonce=123");
    }

    @Test
    void should_decode_token_amount_when_reading_tokenized_asset_entry() throws Exception {
        // Given
        LedgerEntriesEndpoint unit = new LedgerEntriesEndpoint(LedgerEntriesParams.builder().entryIds(List.of("L-TOKEN")).build());
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module())
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
        String json = """
                {"error":[],"result":{"L-TOKEN":{"refid":"TJKLXF-PGMUI-4NTLXU","time":1688464484,"type":"trade","subtype":"","aclass":"tokenized_asset",
                "asset":"AAPLx","amount":"1.5000","fee":"0","balance":"1.5000","amount_token":"1500000000000000000"}}}
                """;
        KrakenResponse<Map<String, LedgerEntry>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));

        // When
        Map<String, LedgerEntry> result = unit.unwrapResponse(response);

        // Then
        assertThat(result.get("L-TOKEN").tokenAmount()).isEqualByComparingTo("1500000000000000000");
        assertThat(result.get("L-TOKEN").assetClass()).isEqualTo("tokenized_asset");
    }
}
