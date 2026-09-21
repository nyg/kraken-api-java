package dev.andstuff.kraken.api.endpoint.transparency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

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
import dev.andstuff.kraken.api.endpoint.transparency.params.PreTradeParams;
import dev.andstuff.kraken.api.endpoint.transparency.response.Notation;
import dev.andstuff.kraken.api.endpoint.transparency.response.PreTrade;

@ExtendWith(MockitoExtension.class)
class PreTradeEndpointTest {

    @Test
    void should_encode_symbol_when_requesting_order_book_levels() {
        PreTradeEndpoint unit = new PreTradeEndpoint(PreTradeParams.of("BTC/USD"));

        URL result = unit.buildURL();

        assertThat(result).hasProtocol("https").hasHost("api.kraken.com").hasPath("/0/public/PreTrade").hasParameter("symbol", "BTC/USD");
        assertThat(result.getQuery()).isEqualTo("symbol=BTC%2FUSD");
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
        assertThat(KrakenAPI.Public.PRE_TRADE.getPath()).isEqualTo("PreTrade");
    }

    @Test
    void should_reject_missing_symbol_when_building_parameters() {
        assertThatThrownBy(() -> PreTradeParams.of(null)).isInstanceOf(NullPointerException.class).hasMessageContaining("symbol");
    }

    @Test
    void should_read_every_price_level_field_when_decoding_schema_based_response() throws Exception {
        PreTradeEndpoint unit = new PreTradeEndpoint(PreTradeParams.of("BTC/USD"));
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/transparency/PreTrade.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<PreTrade> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        PreTrade result = unit.unwrapResponse(response);

        assertThat(result).isEqualTo(new PreTrade("BTC/USD", "Bitcoin / US Dollars", "BTC", Notation.UNIT, "4H95J0R2X", "BTC",
                "USD", Notation.MONE, "", "", "PGSL", PreTrade.OrderSystem.CLOB,
                List.of(new PreTrade.PriceLevel(PreTrade.Side.BUY, new BigDecimal("102002.1"), new BigDecimal("1.24"), 3,
                        Instant.parse("2024-05-30T12:34:56.123456Z"), Instant.parse("2024-05-30T12:34:56.123456Z"))),
                List.of(new PreTrade.PriceLevel(PreTrade.Side.SELL, new BigDecimal("102002.2"), new BigDecimal("0.5"), 1,
                        Instant.parse("2024-05-30T12:34:55.654321Z"), Instant.parse("2024-05-30T12:34:56.123456Z")))));
    }

    @Test
    void should_fall_back_to_unknown_values_when_kraken_adds_notations_or_systems() throws Exception {
        PreTradeEndpoint unit = new PreTradeEndpoint(PreTradeParams.of("BTC/USD"));
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"symbol":"BTC/USD","base_notation":"NOML","quote_notation":"FUTURE","system":"FUTURE","bids":[{"side":"FUTURE","price":"0.0000000000123456789","qty":"1","count":1}],"asks":[]}}
                """;

        KrakenResponse<PreTrade> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        PreTrade result = unit.unwrapResponse(response);

        assertThat(result.baseNotation()).isEqualTo(Notation.NOML);
        assertThat(result.quoteNotation()).isEqualTo(Notation.UNKNOWN);
        assertThat(result.system()).isEqualTo(PreTrade.OrderSystem.UNKNOWN);
        assertThat(result.bids()).singleElement().satisfies(level -> {
            assertThat(level.side()).isEqualTo(PreTrade.Side.UNKNOWN);
            assertThat(level.price()).isEqualByComparingTo("0.0000000000123456789");
            assertThat(level.submissionTimestamp()).isNull();
        });
        assertThat(result.asks()).isEmpty();
    }
}
