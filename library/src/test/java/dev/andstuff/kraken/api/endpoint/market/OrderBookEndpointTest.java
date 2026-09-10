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
import dev.andstuff.kraken.api.endpoint.market.params.OrderBookParams;
import dev.andstuff.kraken.api.endpoint.market.response.OrderBook;

@ExtendWith(MockitoExtension.class)
class OrderBookEndpointTest {

    @Test
    void should_encode_all_documented_fields_when_all_options_are_set() {
        OrderBookEndpoint unit = new OrderBookEndpoint(OrderBookParams.builder().pair("BTC/USD").count(500).assetVersion(1).assetClass("tokenized_asset").build());

        URL url = unit.buildURL();
        Map<String, String> parameters = Arrays.stream(url.getQuery().split("&"))
                .map(entry -> entry.split("=", 2))
                .collect(Collectors.toMap(entry -> URLDecoder.decode(entry[0], StandardCharsets.UTF_8),
                        entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)));

        assertThat(unit.getHttpMethod()).isEqualTo("GET");
        assertThat(url).hasProtocol("https").hasHost("api.kraken.com").hasPath("/0/public/Depth");
        assertThat(parameters).containsExactlyInAnyOrderEntriesOf(Map.of("pair", "BTC/USD", "count", "500", "assetVersion", "1", "asset_class", "tokenized_asset"));
    }

    @Test
    void should_omit_optional_parameters_when_only_the_pair_is_provided() {
        OrderBookEndpoint unit = new OrderBookEndpoint("BTC/USD");

        URL result = unit.buildURL();

        assertThat(result).hasParameter("pair", "BTC/USD");
        assertThat(result.getQuery()).isEqualTo("pair=BTC%2FUSD");
    }

    @Test
    void should_reject_missing_pair_when_building_parameters() {
        OrderBookParams.OrderBookParamsBuilder unit = OrderBookParams.builder();

        assertThatThrownBy(unit::build).isInstanceOf(NullPointerException.class).hasMessageContaining("pair");
    }

    @Test
    void should_read_levels_and_ignore_unknown_properties_when_decoding_documented_response() throws Exception {
        OrderBookEndpoint unit = new OrderBookEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/depth.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<Map<String, OrderBook>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Map<String, OrderBook> result = response.result().orElseThrow();

        assertThat(result.get("XXBTZUSD").asks()).hasSize(2).first()
                .isEqualTo(new OrderBook.Level(new BigDecimal("30384.10000"), new BigDecimal("2.059"), Instant.ofEpochSecond(1688671659)));
        assertThat(result.get("XXBTZUSD").bids()).hasSize(2).first()
                .isEqualTo(new OrderBook.Level(new BigDecimal("30297.00000"), new BigDecimal("1.115"), Instant.ofEpochSecond(1688671636)));
    }

    @Test
    void should_retain_display_pair_keys_when_asset_version_is_enabled() throws Exception {
        OrderBookEndpoint unit = new OrderBookEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/depth.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8).replace("XXBTZUSD", "BTC/USD");
        }

        KrakenResponse<Map<String, OrderBook>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Map<String, OrderBook> result = response.result().orElseThrow();

        assertThat(result).containsOnlyKeys("BTC/USD");
    }

    @Test
    void should_return_empty_collections_when_no_entries_are_available() throws Exception {
        OrderBookEndpoint unit = new OrderBookEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"BTC/USD":{"asks":[],"bids":[]}}}
                """;

        KrakenResponse<Map<String, OrderBook>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Map<String, OrderBook> result = response.result().orElseThrow();

        assertThat(result.get("BTC/USD").asks()).isEmpty();
        assertThat(result.get("BTC/USD").bids()).isEmpty();
    }
    @Test
    void should_expose_endpoint_path_when_listing_api_endpoints() {
        assertThat(Arrays.asList(KrakenAPI.Public.values())).extracting(KrakenAPI.Public::getPath).contains("Depth");
    }
}
