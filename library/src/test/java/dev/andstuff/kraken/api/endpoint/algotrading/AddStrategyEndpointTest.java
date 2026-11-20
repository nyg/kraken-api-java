package dev.andstuff.kraken.api.endpoint.algotrading;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.algotrading.params.AddStrategyParams;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyAssetClass;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyOrderType;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyType;
import dev.andstuff.kraken.api.endpoint.algotrading.response.StrategyAdded;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderSide;

@ExtendWith(MockitoExtension.class)
class AddStrategyEndpointTest {

    @Test
    void should_encode_all_options_as_json_when_supplied() throws Exception {
        // Given
        AddStrategyEndpoint unit = new AddStrategyEndpoint(AddStrategyParams.builder()
                .strategyType(StrategyType.TWAP).symbol("BTC/USD").assetClass(StrategyAssetClass.FOREX).side(OrderSide.BUY)
                .volume(new BigDecimal("1.5")).orderType(StrategyOrderType.LIMIT).price(new BigDecimal("6.70E+4"))
                .startTime("+300").expireTime("+3900").validate(true).build());
        JsonMapper mapper = JsonMapper.builder().build();

        // When
        String result = unit.encodedParamsWith("1781169958000");

        // Then
        assertThat(mapper.readTree(result)).isEqualTo(mapper.readTree("""
                {
                  "strategy_type": "twap",
                  "symbol": "BTC/USD",
                  "asset_class": "forex",
                  "side": "buy",
                  "volume": "1.5",
                  "ordertype": "limit",
                  "price": "67000",
                  "starttm": "+300",
                  "expiretm": "+3900",
                  "validate": true,
                  "nonce": 1781169958000
                }
                """));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/AddStrategy");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/json");
    }

    @Test
    void should_default_to_twap_on_forex_pair_when_type_and_class_are_omitted() {
        // Given
        AddStrategyEndpoint unit = new AddStrategyEndpoint(AddStrategyParams.builder()
                .symbol("ETH/USD").side(OrderSide.SELL).volume(BigDecimal.TEN).orderType(StrategyOrderType.MARKET).expireTime("+7500").build());

        // When
        String result = unit.encodedParamsWith("123");

        // Then
        assertThat(result).isEqualTo("{\"strategy_type\":\"twap\",\"symbol\":\"ETH/USD\",\"asset_class\":\"forex\",\"side\":\"sell\",\"volume\":\"10\",\"ordertype\":\"market\",\"expiretm\":\"+7500\",\"nonce\":123}");
    }

    @Test
    void should_reject_missing_symbol_when_building_parameters() {
        // Given
        AddStrategyParams.AddStrategyParamsBuilder builder = AddStrategyParams.builder()
                .side(OrderSide.BUY).volume(BigDecimal.ONE).orderType(StrategyOrderType.MARKET).expireTime("+600");

        // When
        Throwable result = catchThrowable(builder::build);

        // Then
        assertThat(result).isInstanceOf(NullPointerException.class).hasMessageContaining("symbol");
    }

    @Test
    void should_reject_missing_side_when_building_parameters() {
        // Given
        AddStrategyParams.AddStrategyParamsBuilder builder = AddStrategyParams.builder()
                .symbol("BTC/USD").volume(BigDecimal.ONE).orderType(StrategyOrderType.MARKET).expireTime("+600");

        // When
        Throwable result = catchThrowable(builder::build);

        // Then
        assertThat(result).isInstanceOf(NullPointerException.class).hasMessageContaining("side");
    }

    @Test
    void should_reject_missing_volume_when_building_parameters() {
        // Given
        AddStrategyParams.AddStrategyParamsBuilder builder = AddStrategyParams.builder()
                .symbol("BTC/USD").side(OrderSide.BUY).orderType(StrategyOrderType.MARKET).expireTime("+600");

        // When
        Throwable result = catchThrowable(builder::build);

        // Then
        assertThat(result).isInstanceOf(NullPointerException.class).hasMessageContaining("volume");
    }

    @Test
    void should_reject_missing_orderType_when_building_parameters() {
        // Given
        AddStrategyParams.AddStrategyParamsBuilder builder = AddStrategyParams.builder()
                .symbol("BTC/USD").side(OrderSide.BUY).volume(BigDecimal.ONE).expireTime("+600");

        // When
        Throwable result = catchThrowable(builder::build);

        // Then
        assertThat(result).isInstanceOf(NullPointerException.class).hasMessageContaining("orderType");
    }

    @Test
    void should_reject_missing_expireTime_when_building_parameters() {
        // Given
        AddStrategyParams.AddStrategyParamsBuilder builder = AddStrategyParams.builder()
                .symbol("BTC/USD").side(OrderSide.BUY).volume(BigDecimal.ONE).orderType(StrategyOrderType.MARKET);

        // When
        Throwable result = catchThrowable(builder::build);

        // Then
        assertThat(result).isInstanceOf(NullPointerException.class).hasMessageContaining("expireTime");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        // Given
        AddStrategyEndpoint unit = new AddStrategyEndpoint(AddStrategyParams.builder()
                .symbol("BTC/USD").side(OrderSide.BUY).volume(BigDecimal.ONE).orderType(StrategyOrderType.MARKET).expireTime("+600").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/algotrading/AddStrategy.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        // When
        KrakenResponse<StrategyAdded> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        StrategyAdded result = unit.unwrapResponse(response);

        // Then
        assertThat(result.strategyId()).isEqualTo("ASMBA64-R23M2-J7JRJV");
    }

    @Test
    void should_return_empty_identifier_when_strategy_is_only_validated() throws Exception {
        // Given
        AddStrategyEndpoint unit = new AddStrategyEndpoint(AddStrategyParams.builder()
                .symbol("BTC/USD").side(OrderSide.BUY).volume(new BigDecimal("0.01")).orderType(StrategyOrderType.MARKET).expireTime("+600").validate(true).build());
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":{"strategy_id":""}}
                """;

        // When
        KrakenResponse<StrategyAdded> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        StrategyAdded result = unit.unwrapResponse(response);

        // Then
        assertThat(result.strategyId()).isEmpty();
    }
}
