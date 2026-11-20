package dev.andstuff.kraken.api.endpoint.algotrading;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.algotrading.params.OpenStrategiesParams;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyAssetClass;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyOrderType;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyStatus;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyType;
import dev.andstuff.kraken.api.endpoint.algotrading.response.AlgoStrategy;
import dev.andstuff.kraken.api.endpoint.algotrading.response.OpenStrategies;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderSide;

@ExtendWith(MockitoExtension.class)
class OpenStrategiesEndpointTest {

    @Test
    void should_encode_strategy_type_as_json_when_supplied() {
        // Given
        OpenStrategiesEndpoint unit = new OpenStrategiesEndpoint(OpenStrategiesParams.builder().strategyType(StrategyType.TWAP).build());

        // When
        String result = unit.encodedParamsWith("1781169958000");

        // Then
        assertThat(result).isEqualTo("{\"strategy_type\":\"twap\",\"nonce\":1781169958000}");
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/GetOpenStrategies");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/json");
    }

    @Test
    void should_encode_only_nonce_when_using_default_options() {
        // Given
        OpenStrategiesEndpoint unit = new OpenStrategiesEndpoint();

        // When
        String result = unit.encodedParamsWith("123");

        // Then
        assertThat(result).isEqualTo("{\"nonce\":123}");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        // Given
        OpenStrategiesEndpoint unit = new OpenStrategiesEndpoint();
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/algotrading/GetOpenStrategies.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        // When
        KrakenResponse<OpenStrategies> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        OpenStrategies result = unit.unwrapResponse(response);

        // Then
        assertThat(result.open()).containsOnlyKeys("ASPBVCI-6MKBA-QE4VF6");
        AlgoStrategy strategy = result.open().get("ASPBVCI-6MKBA-QE4VF6");
        assertThat(strategy.strategyId()).isEqualTo("ASPBVCI-6MKBA-QE4VF6");
        assertThat(strategy.status()).isEqualTo(StrategyStatus.PENDING);
        assertThat(strategy.reason()).isNull();
        assertThat(strategy.details()).isEqualTo(new AlgoStrategy.Details(StrategyType.TWAP, OrderSide.BUY, new BigDecimal("0.01000000"),
                BigDecimal.ZERO, null, BigDecimal.ZERO, BigDecimal.ZERO, Instant.ofEpochSecond(1781169958), Instant.ofEpochSecond(1781173558),
                "BTC/USD", StrategyAssetClass.FOREX, StrategyOrderType.MARKET, null));
    }

    @Test
    void should_map_unknown_values_to_unknown_when_reading_new_enum_values() throws Exception {
        // Given
        OpenStrategiesEndpoint unit = new OpenStrategiesEndpoint();
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":{"open":{"ASPBVCI-6MKBA-QE4VF6":{"strategy_id":"ASPBVCI-6MKBA-QE4VF6","status":"paused","reason":"insufficientFunds",
                "details":{"strategy_type":"vwap","side":"BUY","asset_class":"equity_pair","ordertype":"stop-loss"}}}}}
                """;

        // When
        KrakenResponse<OpenStrategies> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        OpenStrategies result = unit.unwrapResponse(response);

        // Then
        AlgoStrategy strategy = result.open().get("ASPBVCI-6MKBA-QE4VF6");
        assertThat(strategy.status()).isEqualTo(StrategyStatus.UNKNOWN);
        assertThat(strategy.reason()).isEqualTo("insufficientFunds");
        assertThat(strategy.details()).extracting(AlgoStrategy.Details::strategyType, AlgoStrategy.Details::side, AlgoStrategy.Details::assetClass, AlgoStrategy.Details::orderType)
                .containsExactly(StrategyType.UNKNOWN, OrderSide.BUY, StrategyAssetClass.UNKNOWN, StrategyOrderType.UNKNOWN);
    }

    @Test
    void should_return_empty_map_when_no_strategy_is_open() throws Exception {
        // Given
        OpenStrategiesEndpoint unit = new OpenStrategiesEndpoint();
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":{"open":{}}}
                """;

        // When
        KrakenResponse<OpenStrategies> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        OpenStrategies result = unit.unwrapResponse(response);

        // Then
        assertThat(result.open()).isEmpty();
    }
}
