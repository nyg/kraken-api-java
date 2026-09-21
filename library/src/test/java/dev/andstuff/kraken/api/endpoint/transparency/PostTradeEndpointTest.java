package dev.andstuff.kraken.api.endpoint.transparency;

import static org.assertj.core.api.Assertions.assertThat;

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
import dev.andstuff.kraken.api.endpoint.transparency.params.PostTradeParams;
import dev.andstuff.kraken.api.endpoint.transparency.response.Notation;
import dev.andstuff.kraken.api.endpoint.transparency.response.PostTrade;

@ExtendWith(MockitoExtension.class)
class PostTradeEndpointTest {

    @Test
    void should_encode_all_documented_fields_when_all_options_are_set() {
        PostTradeEndpoint unit = new PostTradeEndpoint(PostTradeParams.builder().symbol("BTC/USD").fromTimestamp(Instant.parse("2024-05-30T12:34:56.123456789Z"))
                .toTimestamp(Instant.parse("2024-05-30T13:00:00Z")).count(100).build());

        URL url = unit.buildURL();
        Map<String, String> result = Arrays.stream(url.getQuery().split("&"))
                .map(entry -> entry.split("=", 2))
                .collect(Collectors.toMap(entry -> entry[0], entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)));

        assertThat(url).hasProtocol("https").hasHost("api.kraken.com").hasPath("/0/public/PostTrade");
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of("symbol", "BTC/USD", "from_ts", "2024-05-30T12:34:56.123456789Z",
                "to_ts", "2024-05-30T13:00:00Z", "count", "100"));
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
        assertThat(KrakenAPI.Public.POST_TRADE.getPath()).isEqualTo("PostTrade");
    }

    @Test
    void should_omit_optional_parameters_when_only_the_symbol_is_provided() {
        PostTradeEndpoint unit = new PostTradeEndpoint(PostTradeParams.builder().symbol("BTC/USD").build());

        URL result = unit.buildURL();

        assertThat(result.getQuery()).isEqualTo("symbol=BTC%2FUSD");
    }

    @Test
    void should_omit_symbol_when_requesting_trades_of_all_pairs() {
        PostTradeEndpoint unit = new PostTradeEndpoint(PostTradeParams.builder().count(10).build());

        URL result = unit.buildURL();

        assertThat(result).hasPath("/0/public/PostTrade").hasNoParameter("symbol");
        assertThat(result.getQuery()).isEqualTo("count=10");
    }

    @Test
    void should_send_no_parameter_when_no_option_is_set() {
        PostTradeEndpoint unit = new PostTradeEndpoint(PostTradeParams.builder().build());

        URL result = unit.buildURL();

        assertThat(result).hasPath("/0/public/PostTrade").hasNoParameters();
    }

    @Test
    void should_read_every_trade_field_when_decoding_schema_based_response() throws Exception {
        PostTradeEndpoint unit = new PostTradeEndpoint(PostTradeParams.builder().symbol("BTC/USD").build());
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/transparency/PostTrade.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<PostTrade> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        PostTrade result = unit.unwrapResponse(response);

        assertThat(result.lastTimestamp()).isEqualTo(Instant.parse("2024-05-30T12:34:56.123456789Z"));
        assertThat(result.count()).isEqualTo(1);
        assertThat(result.trades()).containsExactly(new PostTrade.Trade("TGBB7L-HT5LX-J3BZ4A", new BigDecimal("102002.1"), new BigDecimal("1.24"),
                "BTC/USD", "Bitcoin / US Dollars", "BTC", Notation.UNIT, null, null, "USD", Notation.MONE, null, null,
                "PGSL", Instant.parse("2024-05-30T12:34:56.123456789Z"), "PGSL", Instant.parse("2024-05-30T12:34:56.123456789Z")));
        assertThat(PostTradeParams.builder().symbol("BTC/USD").fromTimestamp(result.lastTimestamp()).build().toMap())
                .containsEntry("from_ts", "2024-05-30T12:34:56.123456789Z");
    }

    @Test
    void should_return_empty_trades_when_no_trade_matches() throws Exception {
        PostTradeEndpoint unit = new PostTradeEndpoint(PostTradeParams.builder().symbol("BTC/USD").build());
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"count":0,"trades":[]}}
                """;

        KrakenResponse<PostTrade> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        PostTrade result = unit.unwrapResponse(response);

        assertThat(result.count()).isZero();
        assertThat(result.trades()).isEmpty();
        assertThat(result.lastTimestamp()).isNull();
    }
}
