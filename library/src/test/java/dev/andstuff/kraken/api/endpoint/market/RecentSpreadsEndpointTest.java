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
import dev.andstuff.kraken.api.endpoint.market.params.RecentSpreadsParams;
import dev.andstuff.kraken.api.endpoint.market.response.RecentSpreads;

@ExtendWith(MockitoExtension.class)
class RecentSpreadsEndpointTest {

    @Test
    void should_encode_all_documented_fields_when_all_options_are_set() {
        RecentSpreadsEndpoint unit = new RecentSpreadsEndpoint(RecentSpreadsParams.builder().pair("BTC/USD").since(1688672106L).assetVersion(1).assetClass("tokenized_asset").build());

        URL url = unit.buildURL();
        Map<String, String> parameters = Arrays.stream(url.getQuery().split("&"))
                .map(entry -> entry.split("=", 2))
                .collect(Collectors.toMap(entry -> URLDecoder.decode(entry[0], StandardCharsets.UTF_8),
                        entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)));

        assertThat(unit.getHttpMethod()).isEqualTo("GET");
        assertThat(url).hasProtocol("https").hasHost("api.kraken.com").hasPath("/0/public/Spread");
        assertThat(parameters).containsExactlyInAnyOrderEntriesOf(Map.of("pair", "BTC/USD", "since", "1688672106", "assetVersion", "1", "asset_class", "tokenized_asset"));
        assertThat(Arrays.asList(KrakenAPI.Public.values())).extracting(KrakenAPI.Public::getPath).contains("Spread");
    }

    @Test
    void should_omit_optional_parameters_when_only_the_pair_is_provided() {
        RecentSpreadsEndpoint unit = new RecentSpreadsEndpoint("BTC/USD");

        URL result = unit.buildURL();

        assertThat(result).hasParameter("pair", "BTC/USD");
        assertThat(result.getQuery()).isEqualTo("pair=BTC%2FUSD");
    }

    @Test
    void should_reject_missing_pair_when_building_parameters() {
        RecentSpreadsParams.RecentSpreadsParamsBuilder unit = RecentSpreadsParams.builder();

        assertThatThrownBy(unit::build).isInstanceOf(NullPointerException.class).hasMessageContaining("pair");
    }

    @Test
    void should_retain_spread_positions_and_cursor_when_decoding_documented_response() throws Exception {
        RecentSpreadsEndpoint unit = new RecentSpreadsEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/spread.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<RecentSpreads> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        RecentSpreads result = response.result().orElseThrow();

        assertThat(result.spreads().get("XXBTZUSD")).hasSize(2).first()
                .isEqualTo(new RecentSpreads.Spread(1688671834, new BigDecimal("30292.10000"), new BigDecimal("30297.50000")));
        assertThat(result.last()).isEqualTo(1688672106L);
        assertThat(RecentSpreadsParams.builder().pair("BTC/USD").since(result.last()).build().toMap()).containsEntry("since", "1688672106");
    }

    @Test
    void should_retain_display_pair_keys_when_asset_version_is_enabled() throws Exception {
        RecentSpreadsEndpoint unit = new RecentSpreadsEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/spread.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8).replace("XXBTZUSD", "BTC/USD");
        }

        KrakenResponse<RecentSpreads> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        RecentSpreads result = response.result().orElseThrow();

        assertThat(result.spreads()).containsOnlyKeys("BTC/USD");
    }

    @Test
    void should_separate_cursor_from_pair_entries_when_cursor_precedes_multiple_pairs() throws Exception {
        RecentSpreadsEndpoint unit = new RecentSpreadsEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"last":123,"BTC/USD":[],"ETH/USD":[]}}
                """;

        KrakenResponse<RecentSpreads> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        RecentSpreads result = response.result().orElseThrow();

        assertThat(result.spreads()).containsExactlyInAnyOrderEntriesOf(Map.of("BTC/USD", List.of(), "ETH/USD", List.of()));
        assertThat(result.last()).isEqualTo(123L);
    }

    @Test
    void should_return_empty_collections_when_no_entries_are_available() throws Exception {
        RecentSpreadsEndpoint unit = new RecentSpreadsEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"BTC/USD":[],"last":123}}
                """;

        KrakenResponse<RecentSpreads> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        RecentSpreads result = response.result().orElseThrow();

        assertThat(result.spreads().get("BTC/USD")).isEmpty();
    }
}
