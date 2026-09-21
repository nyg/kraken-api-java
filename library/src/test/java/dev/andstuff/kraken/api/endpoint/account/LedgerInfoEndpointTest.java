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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.account.params.LedgerInfoParams;
import dev.andstuff.kraken.api.endpoint.account.response.LedgerEntry;
import dev.andstuff.kraken.api.endpoint.account.response.LedgerInfo;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;

@ExtendWith(MockitoExtension.class)
class LedgerInfoEndpointTest {

    @Test
    void should_encode_dates_over_ledger_ids_when_both_bounds_are_supplied() {
        LedgerInfoEndpoint unit = new LedgerInfoEndpoint(LedgerInfoParams.builder().assets(List.of("XXBT", "ZUSD")).assetClass("currency")
                .assetType(LedgerInfoParams.Type.NFT_REBATE).fromDate(Instant.ofEpochSecond(1688444262L)).toDate(Instant.ofEpochSecond(1688464484L))
                .fromLedgerId("LMKZCZ-Z3GVL-CXKK4H").toLedgerId("L4UESK-KG3EQ-UFO4T5").withoutCount(true).resultOffset(50).rebaseMultiplier(RebaseMultiplier.BASE).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("asset", "XXBT,ZUSD"),
                Map.entry("aclass", "currency"),
                Map.entry("type", "nft_rebate"),
                Map.entry("start", "1688444262"),
                Map.entry("end", "1688464484"),
                Map.entry("without_count", "true"),
                Map.entry("ofs", "50"),
                Map.entry("rebase_multiplier", "base")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/Ledgers");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/x-www-form-urlencoded");
    }

    @Test
    void should_encode_ledger_ids_and_first_page_when_dates_are_absent() {
        LedgerInfoEndpoint unit = new LedgerInfoEndpoint(LedgerInfoParams.builder().fromLedgerId("LMKZCZ-Z3GVL-CXKK4H").toLedgerId("L4UESK-KG3EQ-UFO4T5").build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("start", "LMKZCZ-Z3GVL-CXKK4H"),
                Map.entry("end", "L4UESK-KG3EQ-UFO4T5"),
                Map.entry("without_count", "false"),
                Map.entry("ofs", "0")));
    }

    @Test
    void should_advance_offset_by_one_page_when_requesting_next_results() {
        LedgerInfoParams unit = LedgerInfoParams.builder().assets(List.of("XXBT")).build();

        LedgerInfoParams result = unit.withNextResultOffset().withNextResultOffset();

        assertThat(result.getResultOffset()).isEqualTo(100);
        assertThat(result.getAssets()).containsExactly("XXBT");
        assertThat(unit.getResultOffset()).isZero();
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        LedgerInfoEndpoint unit = new LedgerInfoEndpoint(LedgerInfoParams.builder().build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/account/Ledgers.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<LedgerInfo> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        LedgerInfo result = unit.unwrapResponse(response);

        assertThat(result.count()).isEqualTo(2);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.asList()).extracting(LedgerEntry::id).containsExactlyInAnyOrder("L4UESK-KG3EQ-UFO4T5", "LMKZCZ-Z3GVL-CXKK4H");
        assertThat(result.entries().get("L4UESK-KG3EQ-UFO4T5")).isEqualTo(new LedgerEntry(null, "TJKLXF-PGMUI-4NTLXU",
                Instant.ofEpochSecond(1688464484L, 178_700_000L), LedgerEntry.Type.TRADE, "", "currency", null, "ZGBP", null,
                new BigDecimal("-24.5000"), new BigDecimal("0.0490"), new BigDecimal("459567.9171")));
        assertThat(result.entries().get("L4UESK-KG3EQ-UFO4T5").netAmount()).isEqualByComparingTo("-24.5490");
        assertThat(result.entries().get("LMKZCZ-Z3GVL-CXKK4H").underlyingAsset()).isEqualTo("USD");
        assertThat(result.stakingRewards()).isEmpty();
    }

    @Test
    void should_keep_only_rewards_and_map_unknown_types_when_decoding_earn_entries() throws Exception {
        LedgerInfoEndpoint unit = new LedgerInfoEndpoint(LedgerInfoParams.builder().assetType(LedgerInfoParams.Type.STAKING).build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":{"count":4,"ledger":{
                "L-REWARD":{"refid":"R-1","time":1704067200,"type":"earn","subtype":"reward","aclass":"currency","subclass":"crypto","asset":"DOT28.S","wallet":"earn / bonded","amount":"0.0000000000123456789","fee":"0","balance":"1"},
                "L-ALLOCATION":{"refid":"R-2","time":1704067200,"type":"earn","subtype":"allocation","aclass":"currency","asset":"DOT","amount":"-1","fee":"0","balance":"0"},
                "L-CUSTODY":{"refid":"R-3","time":1704067200,"type":"custodytransfer","subtype":"","aclass":"currency","asset":"XXBT","amount":"1","fee":"0","balance":"1"},
                "L-FUTURE":{"refid":"R-4","time":1704067200,"type":"future-type","subtype":"","aclass":"currency","asset":"XXBT","amount":"1","fee":"0","balance":"2"}}}}
                """;

        KrakenResponse<LedgerInfo> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        LedgerInfo result = unit.unwrapResponse(response);

        assertThat(result.stakingRewards()).singleElement().satisfies(entry -> {
            assertThat(entry.id()).isEqualTo("L-REWARD");
            assertThat(entry.assetSubClass()).isEqualTo("crypto");
            assertThat(entry.wallet()).isEqualTo("earn / bonded");
            assertThat(entry.amount()).isEqualByComparingTo("0.0000000000123456789");
            assertThat(entry.underlyingAsset()).isEqualTo("DOT");
            assertThat(entry.year()).isEqualTo(2024);
        });
        assertThat(result.entries().get("L-CUSTODY").type()).isEqualTo(LedgerEntry.Type.CUSTODY_TRANSFER);
        assertThat(result.entries().get("L-FUTURE").type()).isEqualTo(LedgerEntry.Type.UNKNOWN);
    }

    @Test
    void should_announce_next_page_when_kraken_returns_a_full_page() {
        LedgerEntry entry = new LedgerEntry(null, "TJKLXF-PGMUI-4NTLXU", Instant.ofEpochSecond(1688464484L), LedgerEntry.Type.TRADE, "", "currency",
                null, "ZGBP", null, new BigDecimal("-24.5000"), new BigDecimal("0.0490"), new BigDecimal("459567.9171"));
        LedgerInfo unit = new LedgerInfo(IntStream.range(0, 50).mapToObj("L-%02d"::formatted)
                .collect(Collectors.toMap(Function.identity(), id -> entry)), 120);

        boolean result = unit.hasNext();

        assertThat(result).isTrue();
        assertThat(unit.size()).isEqualTo(50);
    }
}
