package dev.andstuff.kraken.api.endpoint.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
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
    void should_encode_dates_over_ledger_ids_as_json_when_both_bounds_are_supplied() throws Exception {
        // Given
        LedgerInfoEndpoint unit = new LedgerInfoEndpoint(LedgerInfoParams.builder().assets(List.of("XXBT", "ZUSD")).assetClass("currency")
                .assetType(LedgerInfoParams.Type.NFT_REBATE).fromDate(Instant.ofEpochSecond(1688444262L)).toDate(Instant.ofEpochSecond(1688464484L))
                .fromLedgerId("LMKZCZ-Z3GVL-CXKK4H").toLedgerId("L4UESK-KG3EQ-UFO4T5").withoutCount(true).resultOffset(50).rebaseMultiplier(RebaseMultiplier.BASE).build());
        JsonMapper mapper = JsonMapper.builder().build();

        // When
        String result = unit.encodedParamsWith("123456789");

        // Then
        assertThat(mapper.readTree(result)).isEqualTo(mapper.readTree("""
                {
                  "asset": "XXBT,ZUSD",
                  "aclass": "currency",
                  "type": "nft_rebate",
                  "start": 1688444262,
                  "end": 1688464484,
                  "without_count": true,
                  "ofs": 50,
                  "rebase_multiplier": "base",
                  "nonce": 123456789
                }
                """));
        assertThat(unit.buildURL()).hasPath("/0/private/Ledgers").hasNoQuery();
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/json");
    }

    @Test
    void should_encode_ledger_ids_and_omit_offset_when_dates_are_absent() throws Exception {
        // Given
        LedgerInfoEndpoint unit = new LedgerInfoEndpoint(LedgerInfoParams.builder().fromLedgerId("LMKZCZ-Z3GVL-CXKK4H").toLedgerId("L4UESK-KG3EQ-UFO4T5").build());
        JsonMapper mapper = JsonMapper.builder().build();

        // When
        String result = unit.encodedParamsWith("123456789");

        // Then
        assertThat(mapper.readTree(result)).isEqualTo(mapper.readTree("""
                {"start": "LMKZCZ-Z3GVL-CXKK4H", "end": "L4UESK-KG3EQ-UFO4T5", "without_count": false, "nonce": 123456789}
                """));
    }

    @Test
    void should_encode_class_qualified_assets_and_cursor_when_paging_with_cursor() throws Exception {
        // Given
        LedgerInfoEndpoint unit = new LedgerInfoEndpoint(LedgerInfoParams.builder()
                .assetsWithClass(List.of(new LedgerInfoParams.Asset("AAPLx", "tokenized_asset"), new LedgerInfoParams.Asset("XXBT", "currency")))
                .withCursor(true).cursor("TEVER0VSOjE2ODg0NjQ0ODQ").build());
        JsonMapper mapper = JsonMapper.builder().build();

        // When
        String result = unit.encodedParamsWith("123456789");

        // Then
        assertThat(mapper.readTree(result)).isEqualTo(mapper.readTree("""
                {
                  "asset": [{"asset": "AAPLx", "aclass": "tokenized_asset"}, {"asset": "XXBT", "aclass": "currency"}],
                  "without_count": false,
                  "with_cursor": true,
                  "cursor": "TEVER0VSOjE2ODg0NjQ0ODQ",
                  "nonce": 123456789
                }
                """));
    }

    @Test
    void should_reject_both_asset_forms_when_both_are_set() {
        // Given
        LedgerInfoEndpoint unit = new LedgerInfoEndpoint(LedgerInfoParams.builder().assets(List.of("XXBT"))
                .assetsWithClass(List.of(new LedgerInfoParams.Asset("AAPLx", "tokenized_asset"))).build());

        // When
        Throwable result = catchThrowable(() -> unit.encodedParamsWith("123"));

        // Then
        assertThat(result).isInstanceOf(IllegalArgumentException.class).hasMessage("Specify assets or assetsWithClass, not both");
    }

    @Test
    void should_reject_offset_when_paging_with_cursor() {
        // Given
        LedgerInfoEndpoint unit = new LedgerInfoEndpoint(LedgerInfoParams.builder().withCursor(true).build().withNextResultOffset());

        // When
        Throwable result = catchThrowable(() -> unit.encodedParamsWith("123"));

        // Then
        assertThat(result).isInstanceOf(IllegalArgumentException.class).hasMessage("resultOffset must be absent or 0 when withCursor is true");
    }

    @Test
    void should_encode_type_in_root_locale_when_default_locale_is_turkish() throws Exception {
        // Given
        LedgerInfoEndpoint unit = new LedgerInfoEndpoint(LedgerInfoParams.builder().assetType(LedgerInfoParams.Type.WITHDRAWAL).build());
        JsonMapper mapper = JsonMapper.builder().build();
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));

        // When
        String result;
        try {
            result = unit.encodedParamsWith("123456789");
        }
        finally {
            Locale.setDefault(defaultLocale);
        }

        // Then
        assertThat(mapper.readTree(result).get("type").asText()).isEqualTo("withdrawal");
    }

    @Test
    void should_reject_cursor_when_cursor_pagination_is_off() {
        // Given
        LedgerInfoEndpoint unit = new LedgerInfoEndpoint(LedgerInfoParams.builder().cursor("TEVER0VSOjE2ODg0NjQ0ODQ").build());

        // When
        Throwable result = catchThrowable(() -> unit.encodedParamsWith("123"));

        // Then
        assertThat(result).isInstanceOf(IllegalArgumentException.class).hasMessage("cursor requires withCursor to be true");
    }

    @Test
    void should_encode_wallet_in_query_when_wallet_is_selected() {
        // Given
        LedgerInfoEndpoint unit = new LedgerInfoEndpoint(LedgerInfoParams.builder().accountId("WX6V-JUKW-KKPB-QE36").build());

        // When
        URL result = unit.buildURL();

        // Then
        assertThat(result).hasPath("/0/private/Ledgers").hasQuery("account_id=WX6V-JUKW-KKPB-QE36");
    }

    @Test
    void should_decode_null_count_and_next_cursor_when_reading_cursor_page() throws Exception {
        // Given
        LedgerInfoEndpoint unit = new LedgerInfoEndpoint(LedgerInfoParams.builder().withCursor(true).build());
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module())
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
        String json = """
                {"error":[],"result":{"ledger":{"L4UESK-KG3EQ-UFO4T5":{"refid":"TJKLXF-PGMUI-4NTLXU","time":1688464484.1787,"type":"trade","subtype":"",
                "aclass":"currency","asset":"ZGBP","amount":"-24.5000","fee":"0.0490","balance":"459567.9171"}},"count":null,"cursor":{"next":"TEVER0VSOjE2ODg0NjQ0ODQ"}}}
                """;
        KrakenResponse<LedgerInfo> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));

        // When
        LedgerInfo result = unit.unwrapResponse(response);

        // Then
        assertThat(result.count()).isNull();
        assertThat(result.nextCursor()).isEqualTo("TEVER0VSOjE2ODg0NjQ0ODQ");
        assertThat(result.asList()).extracting(LedgerEntry::id).containsExactly("L4UESK-KG3EQ-UFO4T5");
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
        assertThat(result.entries()).containsEntry("L4UESK-KG3EQ-UFO4T5", new LedgerEntry(null, "TJKLXF-PGMUI-4NTLXU",
                Instant.ofEpochSecond(1688464484L, 178_700_000L), LedgerEntry.Type.TRADE, "", "currency", null, "ZGBP", null,
                new BigDecimal("-24.5000"), new BigDecimal("0.0490"), new BigDecimal("459567.9171"), null));
        assertThat(result.entries().get("L4UESK-KG3EQ-UFO4T5").netAmount()).isEqualByComparingTo("-24.5490");
        assertThat(result.entries().get("LMKZCZ-Z3GVL-CXKK4H").underlyingAsset()).isEqualTo("USD");
        assertThat(result.stakingRewards()).isEmpty();
        assertThat(result.nextCursor()).isNull();
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
                null, "ZGBP", null, new BigDecimal("-24.5000"), new BigDecimal("0.0490"), new BigDecimal("459567.9171"), null);
        LedgerInfo unit = new LedgerInfo(IntStream.range(0, 50).mapToObj("L-%02d"::formatted)
                .collect(Collectors.toMap(Function.identity(), id -> entry)), 120, null);

        boolean result = unit.hasNext();

        assertThat(result).isTrue();
        assertThat(unit.size()).isEqualTo(50);
    }
}
