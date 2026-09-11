package dev.andstuff.kraken.api.endpoint.market;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
import dev.andstuff.kraken.api.endpoint.market.params.GroupedOrderBookParams;
import dev.andstuff.kraken.api.endpoint.market.response.GroupedOrderBook;

@ExtendWith(MockitoExtension.class)
class GroupedOrderBookEndpointTest {

    @Test
    void should_encode_all_documented_fields_when_all_options_are_set() {
        GroupedOrderBookEndpoint unit = new GroupedOrderBookEndpoint(GroupedOrderBookParams.builder().pair("BTC/USD").depth(25).grouping(1000).build());

        URL url = unit.buildURL();
        Map<String, String> parameters = Arrays.stream(url.getQuery().split("&"))
                .map(entry -> entry.split("=", 2))
                .collect(Collectors.toMap(entry -> URLDecoder.decode(entry[0], StandardCharsets.UTF_8),
                        entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)));

        assertThat(unit.getHttpMethod()).isEqualTo("GET");
        assertThat(url).hasProtocol("https").hasHost("api.kraken.com").hasPath("/0/public/GroupedBook");
        assertThat(parameters).containsExactlyInAnyOrderEntriesOf(Map.of("pair", "BTC/USD", "depth", "25", "grouping", "1000"));
    }

    @Test
    void should_omit_optional_parameters_when_only_the_pair_is_provided() {
        GroupedOrderBookEndpoint unit = new GroupedOrderBookEndpoint("BTC/USD");

        URL result = unit.buildURL();

        assertThat(result).hasParameter("pair", "BTC/USD");
        assertThat(result.getQuery()).isEqualTo("pair=BTC%2FUSD");
    }

    @Test
    void should_reject_missing_pair_when_building_parameters() {
        GroupedOrderBookParams.GroupedOrderBookParamsBuilder unit = GroupedOrderBookParams.builder();

        assertThatThrownBy(unit::build).isInstanceOf(NullPointerException.class).hasMessageContaining("pair");
    }

    @Test
    void should_read_grouping_and_aggregated_quantities_when_decoding_documented_response() throws Exception {
        GroupedOrderBookEndpoint unit = new GroupedOrderBookEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/grouped-book.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<GroupedOrderBook> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        GroupedOrderBook result = response.result().orElseThrow();

        assertThat(result).extracting(GroupedOrderBook::pair, GroupedOrderBook::grouping).containsExactly("BTC/USD", 1000);
        assertThat(result.bids()).hasSize(2).first()
                .isEqualTo(new GroupedOrderBook.Level(new BigDecimal("90400.00000"), new BigDecimal("19.83057746")));
        assertThat(result.asks()).hasSize(2).first()
                .isEqualTo(new GroupedOrderBook.Level(new BigDecimal("90500.00000"), new BigDecimal("38.96185061")));
    }

    @Test
    void should_return_empty_collections_when_no_entries_are_available() throws Exception {
        GroupedOrderBookEndpoint unit = new GroupedOrderBookEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"pair":"BTC/USD","grouping":1,"asks":[],"bids":[]}}
                """;

        KrakenResponse<GroupedOrderBook> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        GroupedOrderBook result = response.result().orElseThrow();

        assertThat(result.asks()).isEmpty();
        assertThat(result.bids()).isEmpty();
    }
    @Test
    void should_expose_endpoint_path_when_listing_api_endpoints() {
        assertThat(Arrays.asList(KrakenAPI.Public.values())).extracting(KrakenAPI.Public::getPath).contains("GroupedBook");
    }
}
