package dev.andstuff.kraken.api.endpoint.market;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
import dev.andstuff.kraken.api.endpoint.market.params.Level3OrderBookParams;
import dev.andstuff.kraken.api.endpoint.market.response.Level3OrderBook;

@ExtendWith(MockitoExtension.class)
class Level3OrderBookEndpointTest {

    @Test
    void should_encode_all_documented_fields_when_all_options_are_set() {
        Level3OrderBookEndpoint unit = new Level3OrderBookEndpoint(Level3OrderBookParams.builder().pair("YFI/EUR").depth(0).build());

        URL url = unit.buildURL();
        Map<String, String> parameters = Arrays.stream(unit.encodedParamsWith("123").split("&"))
                .map(entry -> entry.split("=", 2))
                .collect(Collectors.toMap(entry -> URLDecoder.decode(entry[0], StandardCharsets.UTF_8),
                        entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)));

        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(url).hasProtocol("https").hasHost("api.kraken.com").hasPath("/0/private/Level3");
        assertThat(parameters).containsExactlyInAnyOrderEntriesOf(Map.of("pair", "YFI/EUR", "depth", "0", "nonce", "123"));
        assertThat(List.of(KrakenAPI.Private.LEVEL3)).extracting(KrakenAPI.Private::getPath).contains("Level3");
    }

    @Test
    void should_omit_optional_parameters_when_only_the_pair_is_provided() {
        Level3OrderBookEndpoint unit = new Level3OrderBookEndpoint("BTC/USD");

        Map<String, String> parameters = Arrays.stream(unit.encodedParamsWith("123").split("&"))
                .map(entry -> entry.split("=", 2))
                .collect(Collectors.toMap(entry -> URLDecoder.decode(entry[0], StandardCharsets.UTF_8),
                        entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)));

        assertThat(parameters).containsExactlyInAnyOrderEntriesOf(Map.of("pair", "BTC/USD", "nonce", "123"));
    }

    @Test
    void should_reject_missing_pair_when_building_parameters() {
        Level3OrderBookParams.Level3OrderBookParamsBuilder unit = Level3OrderBookParams.builder();

        assertThatThrownBy(unit::build).isInstanceOf(NullPointerException.class).hasMessageContaining("pair");
    }

    @Test
    void should_retain_individual_orders_and_nanosecond_timestamps_when_decoding_documented_response() throws Exception {
        Level3OrderBookEndpoint unit = new Level3OrderBookEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/level3.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<Level3OrderBook> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Level3OrderBook result = response.result().orElseThrow();

        assertThat(result.pair()).isEqualTo("YFI/EUR");
        assertThat(result.bids()).hasSize(2).first().isEqualTo(
                new Level3OrderBook.Order(new BigDecimal("3062.00000"), new BigDecimal("0.29665800"), "O5KJU4-IEQTM-NDMS6W", 1765622008594292000L));
        assertThat(result.bids().getLast().orderId()).isNotEqualTo(result.bids().getFirst().orderId());
        assertThat(result.asks().getFirst().quantity()).isEqualTo(new BigDecimal("0.00278335"));
        assertThat(result.asks().getLast().timestamp()).isEqualTo(1765622021013826600L);
    }

    @Test
    void should_return_empty_collections_when_no_entries_are_available() throws Exception {
        Level3OrderBookEndpoint unit = new Level3OrderBookEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"pair":"BTC/USD","grouping":1,"asks":[],"bids":[]}}
                """;

        KrakenResponse<Level3OrderBook> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Level3OrderBook result = response.result().orElseThrow();

        assertThat(result.asks()).isEmpty();
        assertThat(result.bids()).isEmpty();
    }

    @Test
    void should_replace_previous_nonce_when_reusing_endpoint() {
        Level3OrderBookEndpoint unit = new Level3OrderBookEndpoint("YFI/EUR");
        unit.encodedParamsWith("123");

        Map<String, String> parameters = Arrays.stream(unit.encodedParamsWith("124").split("&"))
                .map(entry -> entry.split("=", 2))
                .collect(Collectors.toMap(entry -> URLDecoder.decode(entry[0], StandardCharsets.UTF_8),
                        entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)));

        assertThat(parameters).containsExactlyInAnyOrderEntriesOf(Map.of("pair", "YFI/EUR", "nonce", "124"));
    }
}
