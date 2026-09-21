package dev.andstuff.kraken.api.endpoint.account;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.math.BigDecimal;
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

@ExtendWith(MockitoExtension.class)
class LedgerEntriesEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        LedgerEntriesEndpoint unit = new LedgerEntriesEndpoint(LedgerEntriesParams.builder()
                .entryIds(List.of("L4UESK-KG3EQ-UFO4T5", "LMKZCZ-Z3GVL-CXKK4H")).includeTrades(true).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("id", "L4UESK-KG3EQ-UFO4T5,LMKZCZ-Z3GVL-CXKK4H"),
                Map.entry("trades", "true")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/QueryLedgers");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/x-www-form-urlencoded");
    }

    @Test
    void should_exclude_trades_when_not_requested() {
        LedgerEntriesEndpoint unit = new LedgerEntriesEndpoint(LedgerEntriesParams.builder().entryIds(List.of("L4UESK-KG3EQ-UFO4T5")).build());

        String result = unit.encodedParamsWith("123");

        assertThat(result).contains("trades=false").contains("id=L4UESK-KG3EQ-UFO4T5").endsWith("nonce=123");
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
                new BigDecimal("-24.5000"), new BigDecimal("0.0490"), new BigDecimal("459567.9171")));
    }
}
