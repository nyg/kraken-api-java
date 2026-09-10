package dev.andstuff.kraken.api.endpoint.market;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.market.params.RecentTradesParams;
import dev.andstuff.kraken.api.endpoint.market.response.RecentTrades;

@ExtendWith(MockitoExtension.class)
class RecentTradesEndpointTest {

    @Test
    void should_encode_all_documented_fields_when_all_options_are_set() {
        RecentTradesEndpoint unit = new RecentTradesEndpoint(RecentTradesParams.builder().pair("BTC/USD").since("1688671969993150842").count(2).assetVersion(1).assetClass("tokenized_asset").build());

        URL url = unit.buildURL();
        Map<String, String> parameters = Arrays.stream(url.getQuery().split("&"))
                .map(entry -> entry.split("=", 2))
                .collect(Collectors.toMap(entry -> URLDecoder.decode(entry[0], StandardCharsets.UTF_8),
                        entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)));

        assertThat(unit.getHttpMethod()).isEqualTo("GET");
        assertThat(url).hasProtocol("https").hasHost("api.kraken.com").hasPath("/0/public/Trades");
        assertThat(parameters).containsExactlyInAnyOrderEntriesOf(Map.of("pair", "BTC/USD", "since", "1688671969993150842", "count", "2", "assetVersion", "1", "asset_class", "tokenized_asset"));
    }

    @Test
    void should_omit_optional_parameters_when_only_the_pair_is_provided() {
        RecentTradesEndpoint unit = new RecentTradesEndpoint("BTC/USD");

        URL result = unit.buildURL();

        assertThat(result).hasParameter("pair", "BTC/USD");
        assertThat(result.getQuery()).isEqualTo("pair=BTC%2FUSD");
    }

    @Test
    void should_reject_missing_pair_when_building_parameters() {
        RecentTradesParams.RecentTradesParamsBuilder unit = RecentTradesParams.builder();

        assertThatThrownBy(unit::build).isInstanceOf(NullPointerException.class).hasMessageContaining("pair");
    }

    @Test
    void should_retain_trade_codes_decimal_time_and_opaque_cursor_when_decoding_documented_response() throws Exception {
        RecentTradesEndpoint unit = new RecentTradesEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/trades.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<RecentTrades> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        RecentTrades result = response.result().orElseThrow();

        assertThat(result.trades().get("XXBTZUSD")).hasSize(2).first().isEqualTo(
                new RecentTrades.Trade(new BigDecimal("30243.40000"), new BigDecimal("0.34507674"), Instant.ofEpochSecond(1688669597L, 827736900L),
                        RecentTrades.Side.BUY, RecentTrades.OrderType.MARKET, "", 61044952));
        assertThat(result.trades().get("XXBTZUSD").getLast()).extracting(RecentTrades.Trade::side, RecentTrades.Trade::orderType)
                .containsExactly(RecentTrades.Side.SELL, RecentTrades.OrderType.LIMIT);
        assertThat(result.last()).isEqualTo("1688671969993150842");
        assertThat(RecentTradesParams.builder().pair("BTC/USD").since(result.last()).build().toMap()).containsEntry("since", result.last());
    }

    @Test
    void should_retain_display_pair_keys_when_asset_version_is_enabled() throws Exception {
        RecentTradesEndpoint unit = new RecentTradesEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/trades.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8).replace("XXBTZUSD", "BTC/USD");
        }

        KrakenResponse<RecentTrades> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        RecentTrades result = response.result().orElseThrow();

        assertThat(result.trades()).containsOnlyKeys("BTC/USD");
    }

    @Test
    void should_separate_cursor_from_pair_entries_when_cursor_precedes_multiple_pairs() throws Exception {
        RecentTradesEndpoint unit = new RecentTradesEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"last":"001234567890123456789","BTC/USD":[],"ETH/USD":[]}}
                """;

        KrakenResponse<RecentTrades> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        RecentTrades result = response.result().orElseThrow();

        assertThat(result.trades()).containsExactlyInAnyOrderEntriesOf(Map.of("BTC/USD", List.of(), "ETH/USD", List.of()));
        assertThat(result.last()).isEqualTo("001234567890123456789");
    }

    @Test
    void should_return_empty_collections_when_no_entries_are_available() throws Exception {
        RecentTradesEndpoint unit = new RecentTradesEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"BTC/USD":[],"last":"001234567890123456789"}}
                """;

        KrakenResponse<RecentTrades> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        RecentTrades result = response.result().orElseThrow();

        assertThat(result.trades().get("BTC/USD")).isEmpty();
    }

    @Test
    void should_preserve_fractional_precision_and_large_trade_ids_when_decoding_numeric_values() throws Exception {
        RecentTradesEndpoint unit = new RecentTradesEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/trades.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8).replace("1688669597.8277369", "1688669597.123456789").replace("61044952", "61044952000000001");
        }

        KrakenResponse<RecentTrades> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        RecentTrades result = response.result().orElseThrow();

        assertThat(result.trades().get("XXBTZUSD").getFirst()).extracting(RecentTrades.Trade::time, RecentTrades.Trade::tradeId)
                .containsExactly(Instant.ofEpochSecond(1688669597L, 123456789L), 61044952000000001L);
    }

    @Test
    void should_fall_back_to_unknown_when_trade_codes_are_unrecognized() throws Exception {
        RecentTradesEndpoint unit = new RecentTradesEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/trades.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8).replace("\"b\"", "\"future-side\"").replace("\"m\"", "\"future-type\"");
        }

        KrakenResponse<RecentTrades> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        RecentTrades result = response.result().orElseThrow();

        assertThat(result.trades().get("XXBTZUSD").getFirst()).extracting(RecentTrades.Trade::side, RecentTrades.Trade::orderType)
                .containsExactly(RecentTrades.Side.UNKNOWN, RecentTrades.OrderType.UNKNOWN);
    }
    @Test
    void should_expose_endpoint_path_when_listing_api_endpoints() {
        assertThat(Arrays.asList(KrakenAPI.Public.values())).extracting(KrakenAPI.Public::getPath).contains("Trades");
    }
}
