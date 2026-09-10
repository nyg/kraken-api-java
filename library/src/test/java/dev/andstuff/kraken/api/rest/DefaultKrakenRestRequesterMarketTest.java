package dev.andstuff.kraken.api.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import javax.net.ssl.HttpsURLConnection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.andstuff.kraken.api.endpoint.market.RecentTradesEndpoint;
import dev.andstuff.kraken.api.endpoint.market.response.RecentTrades;

@ExtendWith(MockitoExtension.class)
class DefaultKrakenRestRequesterMarketTest {

    @Mock private DefaultKrakenRestRequester.ConnectionFactory connectionFactory;
    @Mock private HttpsURLConnection connection;

    @Test
    void should_decode_precise_timestamps_and_future_enums_when_using_production_mapper() throws Exception {
        DefaultKrakenRestRequester unit = new DefaultKrakenRestRequester(connectionFactory);
        RecentTradesEndpoint endpoint = new RecentTradesEndpoint("BTC/USD");
        when(connectionFactory.open(any(URL.class))).thenReturn(connection);
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");
        String json = """
                {"error":[],"result":{"BTC/USD":[["0.0000000000123456789","0.1",1688669597.123456789,"future-side","m","",61044952000000001]],"last":"opaque-cursor"}}
                """;
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));

        RecentTrades result = unit.execute(endpoint);

        assertThat(result.last()).isEqualTo("opaque-cursor");
        assertThat(result.trades().get("BTC/USD")).singleElement().satisfies(trade -> {
            assertThat(trade.time()).isEqualTo(Instant.ofEpochSecond(1688669597L, 123456789L));
            assertThat(trade.price()).isEqualByComparingTo("0.0000000000123456789");
            assertThat(trade.side()).isEqualTo(RecentTrades.Side.UNKNOWN);
            assertThat(trade.orderType()).isEqualTo(RecentTrades.OrderType.MARKET);
            assertThat(trade.tradeId()).isEqualTo(61044952000000001L);
        });
    }
}
