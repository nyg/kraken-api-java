package dev.andstuff.kraken.api.endpoint.market;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.market.response.Ticker;

@ExtendWith(MockitoExtension.class)
class TickerEndpointTest {

    @Test
    void should_join_pairs_when_requesting_several_tickers() {
        TickerEndpoint unit = new TickerEndpoint(List.of("XBTUSD", "ETH/USD"));

        URL result = unit.buildURL();

        assertThat(result).hasProtocol("https").hasHost("api.kraken.com").hasPath("/0/public/Ticker").hasParameter("pair", "XBTUSD,ETH/USD");
        assertThat(result.getQuery()).isEqualTo("pair=XBTUSD%2CETH%2FUSD");
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
    }

    @Test
    void should_read_every_ticker_position_when_decoding_documented_response() throws Exception {
        TickerEndpoint unit = new TickerEndpoint(List.of("XBTUSD"));
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/ticker.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<Map<String, Ticker>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Map<String, Ticker> result = unit.unwrapResponse(response);

        assertThat(result).containsOnlyKeys("XXBTZUSD").containsEntry("XXBTZUSD", new Ticker(
                new Ticker.Ask(new BigDecimal("30300.10000"), new BigDecimal("1"), new BigDecimal("1.000")),
                new Ticker.Bid(new BigDecimal("30300.00000"), new BigDecimal("1"), new BigDecimal("1.000")),
                new Ticker.LastTrade(new BigDecimal("30303.20000"), new BigDecimal("0.00067643")),
                new Ticker.Volume(new BigDecimal("4083.67001100"), new BigDecimal("4412.73601799")),
                new Ticker.VWAP(new BigDecimal("30706.77771"), new BigDecimal("30689.13205")),
                new Ticker.TradeCount(34619, 38907),
                new Ticker.Low(new BigDecimal("29868.30000"), new BigDecimal("29868.30000")),
                new Ticker.High(new BigDecimal("31631.00000"), new BigDecimal("31631.00000")),
                new BigDecimal("30502.80000")));
    }

    @Test
    void should_keep_display_pair_keys_when_several_pairs_are_returned() throws Exception {
        TickerEndpoint unit = new TickerEndpoint(List.of("BTC/USD", "ETH/USD"));
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/ticker.json")) {
            String ticker = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
            json = ticker.replace("\"XXBTZUSD\"", "\"BTC/USD\"").replace("\"result\": {", "\"result\": {\"ETH/USD\": {\"o\": \"0.0000000000123456789\"},");
        }

        KrakenResponse<Map<String, Ticker>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Map<String, Ticker> result = unit.unwrapResponse(response);

        assertThat(result).containsOnlyKeys("BTC/USD", "ETH/USD");
        assertThat(result.get("ETH/USD").openingPrice()).isEqualByComparingTo("0.0000000000123456789");
        assertThat(result.get("ETH/USD").ask()).isNull();
    }
}
