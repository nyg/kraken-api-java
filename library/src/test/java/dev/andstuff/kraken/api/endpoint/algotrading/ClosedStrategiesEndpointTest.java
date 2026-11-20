package dev.andstuff.kraken.api.endpoint.algotrading;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

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
import dev.andstuff.kraken.api.endpoint.algotrading.params.ClosedStrategiesParams;
import dev.andstuff.kraken.api.endpoint.algotrading.params.QueryTimeMode;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyAssetClass;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyOrderType;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyStatus;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyType;
import dev.andstuff.kraken.api.endpoint.algotrading.response.AlgoStrategy;
import dev.andstuff.kraken.api.endpoint.algotrading.response.ClosedStrategies;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderSide;

@ExtendWith(MockitoExtension.class)
class ClosedStrategiesEndpointTest {

    @Test
    void should_encode_all_filters_as_json_when_supplied() throws Exception {
        // Given
        ClosedStrategiesEndpoint unit = new ClosedStrategiesEndpoint(ClosedStrategiesParams.builder()
                .assetClass(StrategyAssetClass.FOREX).strategyType(StrategyType.TWAP).status(StrategyStatus.CANCELLED)
                .start(Instant.ofEpochSecond(1781100000)).end(Instant.ofEpochSecond(1781200000, 900_000_000)).queryTimeMode(QueryTimeMode.CLOSE)
                .limit(50).cursor("Q0xPU0VEOjE3ODExNzAwMDA6QVNNQkE2NA").build());
        JsonMapper mapper = JsonMapper.builder().build();

        // When
        String result = unit.encodedParamsWith("1781169958001");

        // Then
        assertThat(mapper.readTree(result)).isEqualTo(mapper.readTree("""
                {
                  "asset_class": "forex",
                  "strategy_type": "twap",
                  "status": "cancelled",
                  "start": 1781100000,
                  "end": 1781200000,
                  "query_time_mode": "close",
                  "limit": 50,
                  "cursor": "Q0xPU0VEOjE3ODExNzAwMDA6QVNNQkE2NA",
                  "nonce": 1781169958001
                }
                """));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/GetClosedStrategies");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/json");
    }

    @Test
    void should_encode_only_nonce_when_using_default_options() {
        // Given
        ClosedStrategiesEndpoint unit = new ClosedStrategiesEndpoint();

        // When
        String result = unit.encodedParamsWith("123");

        // Then
        assertThat(result).isEqualTo("{\"nonce\":123}");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        // Given
        ClosedStrategiesEndpoint unit = new ClosedStrategiesEndpoint();
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/algotrading/GetClosedStrategies.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        // When
        KrakenResponse<ClosedStrategies> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        ClosedStrategies result = unit.unwrapResponse(response);

        // Then
        assertThat(result.nextCursor()).isEqualTo("Q0xPU0VEOjE3ODExNzAwMDA6QVNNQkE2NA");
        assertThat(result.closed()).extracting(AlgoStrategy::strategyId, AlgoStrategy::status, AlgoStrategy::reason)
                .containsExactly(
                        tuple("ASPBVCI-6MKBA-QE4VF6", StrategyStatus.CANCELLED, "userRequested"),
                        tuple("ASMBA64-R23M2-J7JRJV", StrategyStatus.CLOSED, null));
        assertThat(result.closed().getFirst().details()).isEqualTo(new AlgoStrategy.Details(StrategyType.TWAP, OrderSide.BUY,
                new BigDecimal("1.50000000"), new BigDecimal("0.80000000"), new BigDecimal("67098.42"), new BigDecimal("53678.74"),
                new BigDecimal("85.89"), Instant.ofEpochSecond(1781169958), Instant.ofEpochSecond(1781173558), "BTC/USD",
                StrategyAssetClass.FOREX, StrategyOrderType.MARKET, null));
    }

    @Test
    void should_return_null_cursor_when_reading_last_page() throws Exception {
        // Given
        ClosedStrategiesEndpoint unit = new ClosedStrategiesEndpoint();
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":{"closed":[]}}
                """;

        // When
        KrakenResponse<ClosedStrategies> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        ClosedStrategies result = unit.unwrapResponse(response);

        // Then
        assertThat(result.closed()).isEmpty();
        assertThat(result.nextCursor()).isNull();
    }
}
