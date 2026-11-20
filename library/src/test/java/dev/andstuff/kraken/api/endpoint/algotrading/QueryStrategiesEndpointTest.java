package dev.andstuff.kraken.api.endpoint.algotrading;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.algotrading.params.QueryStrategiesParams;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyAssetClass;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyOrderType;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyStatus;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyType;
import dev.andstuff.kraken.api.endpoint.algotrading.response.AlgoStrategy;
import dev.andstuff.kraken.api.endpoint.algotrading.response.StrategiesInfo;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderSide;

@ExtendWith(MockitoExtension.class)
class QueryStrategiesEndpointTest {

    @Test
    void should_encode_identifiers_as_comma_delimited_list_when_querying() {
        // Given
        QueryStrategiesEndpoint unit = new QueryStrategiesEndpoint(List.of("ASPBVCI-6MKBA-QE4VF6", "ASMBA64-R23M2-J7JRJV"));

        // When
        String result = unit.encodedParamsWith("1781169958000");

        // Then
        assertThat(result).isEqualTo("{\"strategy_ids\":\"ASPBVCI-6MKBA-QE4VF6,ASMBA64-R23M2-J7JRJV\",\"nonce\":1781169958000}");
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/QueryStrategies");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/json");
    }

    @Test
    void should_reject_missing_strategyIds_when_building_parameters() {
        // Given
        QueryStrategiesParams.QueryStrategiesParamsBuilder builder = QueryStrategiesParams.builder();

        // When
        Throwable result = catchThrowable(builder::build);

        // Then
        assertThat(result).isInstanceOf(NullPointerException.class).hasMessageContaining("strategyIds");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        // Given
        QueryStrategiesEndpoint unit = new QueryStrategiesEndpoint(QueryStrategiesParams.builder().strategyIds(List.of("ASPBVCI-6MKBA-QE4VF6")).build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/algotrading/QueryStrategies.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        // When
        KrakenResponse<StrategiesInfo> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        StrategiesInfo result = unit.unwrapResponse(response);

        // Then
        assertThat(result.strategies()).containsOnlyKeys("ASPBVCI-6MKBA-QE4VF6", "ASMBA64-R23M2-J7JRJV");
        assertThat(result.strategies().get("ASPBVCI-6MKBA-QE4VF6").status()).isEqualTo(StrategyStatus.PENDING);
        AlgoStrategy open = result.strategies().get("ASMBA64-R23M2-J7JRJV");
        assertThat(open.status()).isEqualTo(StrategyStatus.OPEN);
        assertThat(open.details()).isEqualTo(new AlgoStrategy.Details(StrategyType.TWAP, OrderSide.SELL, new BigDecimal("2.00000000"),
                new BigDecimal("0.50000000"), new BigDecimal("67050.10"), new BigDecimal("33525.05"), new BigDecimal("53.64"),
                Instant.ofEpochSecond(1781166400), Instant.ofEpochSecond(1781173600), "BTC/USD", StrategyAssetClass.FOREX,
                StrategyOrderType.LIMIT, new BigDecimal("67000.0")));
    }

    @Test
    void should_return_empty_map_when_no_identifier_matches() throws Exception {
        // Given
        QueryStrategiesEndpoint unit = new QueryStrategiesEndpoint(List.of("ASAAAAA-BBBBB-CCCCCC"));
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":{"strategies":{}}}
                """;

        // When
        KrakenResponse<StrategiesInfo> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        StrategiesInfo result = unit.unwrapResponse(response);

        // Then
        assertThat(result.strategies()).isEmpty();
    }
}
