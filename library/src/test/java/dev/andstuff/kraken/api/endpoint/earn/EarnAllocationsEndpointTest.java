package dev.andstuff.kraken.api.endpoint.earn;

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
import dev.andstuff.kraken.api.endpoint.earn.params.EarnAllocationsParams;
import dev.andstuff.kraken.api.endpoint.earn.response.AssetClass;
import dev.andstuff.kraken.api.endpoint.earn.response.EarnAllocations;

@ExtendWith(MockitoExtension.class)
class EarnAllocationsEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        EarnAllocationsEndpoint unit = new EarnAllocationsEndpoint(EarnAllocationsParams.builder().ascending(false).convertedAsset("EUR").hideZeroAllocations(true).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("ascending", "false"),
                Map.entry("converted_asset", "EUR"),
                Map.entry("hide_zero_allocations", "true")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/Earn/Allocations");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/x-www-form-urlencoded");
    }

    @Test
    void should_send_only_nonce_when_no_option_is_supplied() {
        EarnAllocationsEndpoint unit = new EarnAllocationsEndpoint(EarnAllocationsParams.builder().build());

        String result = unit.encodedParamsWith("123");

        assertThat(result).isEqualTo("nonce=123");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        EarnAllocationsEndpoint unit = new EarnAllocationsEndpoint(EarnAllocationsParams.builder().build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/earn/Allocations.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<EarnAllocations> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        EarnAllocations result = unit.unwrapResponse(response);

        assertThat(result.convertedAsset()).isEqualTo("USD");
        assertThat(result.convertedAssetClass()).isNull();
        assertThat(result.totalAllocated()).isEqualByComparingTo("49.2398");
        assertThat(result.totalRewarded()).isEqualByComparingTo("0.0675");
        assertThat(result.nextCursor()).isEqualTo("2");
        assertThat(result.items()).containsExactly(new EarnAllocations.Allocation("ESDQCOL-WTZEU-NU55QF", "ETH", null,
                new EarnAllocations.AmountAllocated(
                        new EarnAllocations.State(new BigDecimal("0.0210000000"), new BigDecimal("39.0645"), 2, List.of(
                                new EarnAllocations.Entry(Instant.parse("2023-07-06T10:52:05Z"), Instant.parse("2023-08-19T02:34:05.807Z"),
                                        new BigDecimal("0.0010000000"), new BigDecimal("1.8602")),
                                new EarnAllocations.Entry(Instant.parse("2023-08-01T11:25:52Z"), Instant.parse("2023-09-06T07:55:52.648Z"),
                                        new BigDecimal("0.0200000000"), new BigDecimal("37.2043")))),
                        null, null, null, null,
                        new EarnAllocations.Amount(new BigDecimal("0.0210000000"), new BigDecimal("39.0645"))),
                new EarnAllocations.Amount(new BigDecimal("0"), new BigDecimal("0.0000")),
                null, false, null));
    }

    @Test
    void should_read_every_state_and_payout_when_allocation_is_in_use() throws Exception {
        EarnAllocationsEndpoint unit = new EarnAllocationsEndpoint(EarnAllocationsParams.builder().hideZeroAllocations(true).build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":{"converted_asset":"EUR","converted_asset_class":"currency","total_allocated":"10","total_rewarded":"1","next_cursor":null,
                "items":[{"strategy_id":"ESRFUO3-Q62XD-WIOIL7","native_asset":"DOT","asset_class":"future-class",
                "amount_allocated":{
                "allocated":{"native":"0.0000000000123456789","converted":"0.01"},
                "exit_queue":{"native":"1","converted":"5","allocation_count":1,"allocations":[{"created_at":"2024-01-01T00:00:00Z","expires":"2024-01-05T00:00:00Z","native":"1","converted":"5"}]},
                "unbonding":{"native":"2","converted":"10","allocation_count":0,"allocations":[]},
                "pending":{"native":"3","converted":"15"},
                "total":{"native":"6.0000000000123456789","converted":"30.01"}},
                "total_rewarded":{"native":"0.2","converted":"1"},
                "payout":{"period_start":"2024-01-01T00:00:00Z","period_end":"2024-01-08T00:00:00Z",
                "accumulated_reward":{"native":"0.01","converted":"0.05"},"estimated_reward":{"native":"0.02","converted":"0.1"}},
                "is_utilized":true,"utilized_pct":"87.5"}]}}
                """;

        KrakenResponse<EarnAllocations> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        EarnAllocations result = unit.unwrapResponse(response);

        assertThat(result.convertedAssetClass()).isEqualTo(AssetClass.CURRENCY);
        assertThat(result.nextCursor()).isNull();
        assertThat(result.items()).singleElement().satisfies(allocation -> {
            assertThat(allocation.assetClass()).isEqualTo(AssetClass.UNKNOWN);
            assertThat(allocation.amountAllocated().bonding()).isNull();
            assertThat(allocation.amountAllocated().allocated().nativeAmount()).isEqualByComparingTo("0.0000000000123456789");
            assertThat(allocation.amountAllocated().exitQueue().allocations()).singleElement()
                    .extracting(EarnAllocations.Entry::expires).isEqualTo(Instant.parse("2024-01-05T00:00:00Z"));
            assertThat(allocation.amountAllocated().unbonding().allocationCount()).isZero();
            assertThat(allocation.amountAllocated().pending().converted()).isEqualByComparingTo("15");
            assertThat(allocation.payout()).isEqualTo(new EarnAllocations.Payout(Instant.parse("2024-01-01T00:00:00Z"), Instant.parse("2024-01-08T00:00:00Z"),
                    new EarnAllocations.Amount(new BigDecimal("0.01"), new BigDecimal("0.05")),
                    new EarnAllocations.Amount(new BigDecimal("0.02"), new BigDecimal("0.1"))));
            assertThat(allocation.isUtilized()).isTrue();
            assertThat(allocation.utilizedPct()).isEqualByComparingTo("87.5");
        });
    }
}
