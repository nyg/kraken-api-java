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
import dev.andstuff.kraken.api.endpoint.market.params.OhlcParams;
import dev.andstuff.kraken.api.endpoint.market.response.OhlcData;

@ExtendWith(MockitoExtension.class)
class OhlcEndpointTest {

    @Test
    void should_encode_all_documented_fields_when_all_options_are_set() {
        OhlcEndpoint unit = new OhlcEndpoint(OhlcParams.builder().pair("BTC/USD").interval(60).since(1688671200L).assetVersion(1).assetClass("tokenized_asset").build());

        URL url = unit.buildURL();
        Map<String, String> parameters = Arrays.stream(url.getQuery().split("&"))
                .map(entry -> entry.split("=", 2))
                .collect(Collectors.toMap(entry -> URLDecoder.decode(entry[0], StandardCharsets.UTF_8),
                        entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)));

        assertThat(unit.getHttpMethod()).isEqualTo("GET");
        assertThat(url).hasProtocol("https").hasHost("api.kraken.com").hasPath("/0/public/OHLC");
        assertThat(parameters).containsExactlyInAnyOrderEntriesOf(Map.of("pair", "BTC/USD", "interval", "60", "since", "1688671200", "assetVersion", "1", "asset_class", "tokenized_asset"));
        assertThat(Arrays.asList(KrakenAPI.Public.values())).extracting(KrakenAPI.Public::getPath).contains("OHLC");
    }

    @Test
    void should_omit_optional_parameters_when_only_the_pair_is_provided() {
        OhlcEndpoint unit = new OhlcEndpoint("BTC/USD");

        URL result = unit.buildURL();

        assertThat(result).hasParameter("pair", "BTC/USD");
        assertThat(result.getQuery()).isEqualTo("pair=BTC%2FUSD");
    }

    @Test
    void should_reject_missing_pair_when_building_parameters() {
        OhlcParams.OhlcParamsBuilder unit = OhlcParams.builder();

        assertThatThrownBy(unit::build).isInstanceOf(NullPointerException.class).hasMessageContaining("pair");
    }

    @Test
    void should_retain_all_candle_positions_and_committed_cursor_when_decoding_documented_response() throws Exception {
        OhlcEndpoint unit = new OhlcEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/ohlc.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<OhlcData> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        OhlcData result = response.result().orElseThrow();

        assertThat(result.candles().get("XXBTZUSD")).hasSize(2).first().isEqualTo(
                new OhlcData.Candle(1688671200, new BigDecimal("30306.1"), new BigDecimal("30306.2"), new BigDecimal("30305.7"),
                        new BigDecimal("30305.7"), new BigDecimal("30306.1"), new BigDecimal("3.39243896"), 23));
        assertThat(result.candles().get("XXBTZUSD").getLast().time()).isEqualTo(1688671260L);
        assertThat(result.last()).isEqualTo(1688672160L);
        assertThat(OhlcParams.builder().pair("BTC/USD").since(result.last()).build().toMap()).containsEntry("since", "1688672160");
    }

    @Test
    void should_retain_display_pair_keys_when_asset_version_is_enabled() throws Exception {
        OhlcEndpoint unit = new OhlcEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/ohlc.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8).replace("XXBTZUSD", "BTC/USD");
        }

        KrakenResponse<OhlcData> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        OhlcData result = response.result().orElseThrow();

        assertThat(result.candles()).containsOnlyKeys("BTC/USD");
    }

    @Test
    void should_separate_cursor_from_pair_entries_when_cursor_precedes_multiple_pairs() throws Exception {
        OhlcEndpoint unit = new OhlcEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"last":123,"BTC/USD":[],"ETH/USD":[]}}
                """;

        KrakenResponse<OhlcData> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        OhlcData result = response.result().orElseThrow();

        assertThat(result.candles()).containsExactlyInAnyOrderEntriesOf(Map.of("BTC/USD", List.of(), "ETH/USD", List.of()));
        assertThat(result.last()).isEqualTo(123L);
    }

    @Test
    void should_return_empty_collections_when_no_entries_are_available() throws Exception {
        OhlcEndpoint unit = new OhlcEndpoint("BTC/USD");
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"BTC/USD":[],"last":123}}
                """;

        KrakenResponse<OhlcData> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        OhlcData result = response.result().orElseThrow();

        assertThat(result.candles().get("BTC/USD")).isEmpty();
    }
}
