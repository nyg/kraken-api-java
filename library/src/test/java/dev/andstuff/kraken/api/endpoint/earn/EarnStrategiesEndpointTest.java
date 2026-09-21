package dev.andstuff.kraken.api.endpoint.earn;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
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
import dev.andstuff.kraken.api.endpoint.earn.params.EarnStrategiesParams;
import dev.andstuff.kraken.api.endpoint.earn.params.LockType;
import dev.andstuff.kraken.api.endpoint.earn.response.AssetClass;
import dev.andstuff.kraken.api.endpoint.earn.response.EarnStrategies;

@ExtendWith(MockitoExtension.class)
class EarnStrategiesEndpointTest {

    @Test
    void should_encode_all_options_and_index_lock_types_when_supplied() {
        EarnStrategiesEndpoint unit = new EarnStrategiesEndpoint(EarnStrategiesParams.builder().ascending(true).asset("DOT").cursor("2").limit(10)
                .lockTypes(List.of(LockType.BONDED, LockType.INSTANT)).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("ascending", "true"),
                Map.entry("asset", "DOT"),
                Map.entry("cursor", "2"),
                Map.entry("limit", "10"),
                Map.entry("lock_type[0]", "bonded"),
                Map.entry("lock_type[1]", "instant")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/Earn/Strategies");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/x-www-form-urlencoded");
    }

    @Test
    void should_send_only_nonce_when_no_option_is_supplied() {
        EarnStrategiesEndpoint unit = new EarnStrategiesEndpoint(EarnStrategiesParams.builder().build());

        String result = unit.encodedParamsWith("123");

        assertThat(result).isEqualTo("nonce=123");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        EarnStrategiesEndpoint unit = new EarnStrategiesEndpoint(EarnStrategiesParams.builder().build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/earn/Strategies.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<EarnStrategies> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        EarnStrategies result = unit.unwrapResponse(response);

        assertThat(result.nextCursor()).isEqualTo("2");
        assertThat(result.items()).containsExactly(new EarnStrategies.Strategy("ESRFUO3-Q62XD-WIOIL7", "DOT", null,
                new EarnStrategies.LockType(EarnStrategies.LockType.Type.INSTANT, Duration.ofDays(7), null, null, null, null, null, null, null, null),
                new EarnStrategies.AprEstimate(new BigDecimal("8.0000"), new BigDecimal("12.0000")),
                new BigDecimal("0.0000"), new BigDecimal("0.0000"),
                new EarnStrategies.AutoCompound(EarnStrategies.AutoCompound.Type.ENABLED, null),
                new EarnStrategies.YieldSource(EarnStrategies.YieldSource.Type.STAKING),
                true, true, List.of(), null, new BigDecimal("0.01")));
    }

    @Test
    void should_read_bonding_terms_and_restrictions_when_strategy_is_bonded() throws Exception {
        EarnStrategiesEndpoint unit = new EarnStrategiesEndpoint(EarnStrategiesParams.builder().lockTypes(List.of(LockType.BONDED)).build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":{"next_cursor":null,"items":[{"id":"ESDQCOL-WTZEU-NU55QF","asset":"ETH","asset_class":"currency",
                "lock_type":{"type":"bonded","payout_frequency":86400,"bonding_period":1209600,"bonding_period_variable":true,"bonding_rewards":false,
                "exit_queue_period":345600,"unbonding_period":604800,"unbonding_period_variable":false,"unbonding_rewards":true},
                "apr_estimate":null,"allocation_fee":1,"deallocation_fee":0.0000000000123456789,
                "auto_compound":{"type":"optional","default":true},"yield_source":{"type":"off_chain"},
                "can_allocate":false,"can_deallocate":true,"allocation_restriction_info":["tier","future-restriction"],"user_cap":"100.5","user_min_allocation":null}]}}
                """;

        KrakenResponse<EarnStrategies> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        EarnStrategies result = unit.unwrapResponse(response);

        assertThat(result.nextCursor()).isNull();
        assertThat(result.items()).singleElement().satisfies(strategy -> {
            assertThat(strategy.assetClass()).isEqualTo(AssetClass.CURRENCY);
            assertThat(strategy.lockType()).isEqualTo(new EarnStrategies.LockType(EarnStrategies.LockType.Type.BONDED, Duration.ofDays(1), null,
                    Duration.ofDays(14), true, false, Duration.ofDays(4), Duration.ofDays(7), false, true));
            assertThat(strategy.aprEstimate()).isNull();
            assertThat(strategy.allocationFee()).isEqualByComparingTo("1");
            assertThat(strategy.deallocationFee()).isEqualByComparingTo("0.0000000000123456789");
            assertThat(strategy.autoCompound()).isEqualTo(new EarnStrategies.AutoCompound(EarnStrategies.AutoCompound.Type.OPTIONAL, true));
            assertThat(strategy.yieldSource().type()).isEqualTo(EarnStrategies.YieldSource.Type.OFF_CHAIN);
            assertThat(strategy.canAllocate()).isFalse();
            assertThat(strategy.allocationRestrictionInfo()).containsExactly(EarnStrategies.Restriction.TIER, EarnStrategies.Restriction.UNKNOWN);
            assertThat(strategy.userCap()).isEqualByComparingTo("100.5");
            assertThat(strategy.userMinAllocation()).isNull();
        });
    }

    @Test
    void should_fall_back_to_unknown_values_when_kraken_adds_strategy_kinds() throws Exception {
        EarnStrategiesEndpoint unit = new EarnStrategiesEndpoint(EarnStrategiesParams.builder().build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":{"items":[{"id":"FUTURE","asset":"AAPLx","asset_class":"future-class","lock_type":{"type":"future-lock","duration_months":6},
                "auto_compound":{"type":"future-compound"},"yield_source":{"type":"future-source"}}]}}
                """;

        KrakenResponse<EarnStrategies> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        EarnStrategies result = unit.unwrapResponse(response);

        assertThat(result.items()).singleElement().satisfies(strategy -> {
            assertThat(strategy.assetClass()).isEqualTo(AssetClass.UNKNOWN);
            assertThat(strategy.lockType().type()).isEqualTo(EarnStrategies.LockType.Type.UNKNOWN);
            assertThat(strategy.lockType().durationMonths()).isEqualTo(6);
            assertThat(strategy.autoCompound().type()).isEqualTo(EarnStrategies.AutoCompound.Type.UNKNOWN);
            assertThat(strategy.yieldSource().type()).isEqualTo(EarnStrategies.YieldSource.Type.UNKNOWN);
        });
    }
}
