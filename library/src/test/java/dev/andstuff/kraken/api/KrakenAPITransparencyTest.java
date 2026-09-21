package dev.andstuff.kraken.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.andstuff.kraken.api.endpoint.transparency.PostTradeEndpoint;
import dev.andstuff.kraken.api.endpoint.transparency.PreTradeEndpoint;
import dev.andstuff.kraken.api.endpoint.transparency.params.PostTradeParams;
import dev.andstuff.kraken.api.endpoint.transparency.response.PostTrade;
import dev.andstuff.kraken.api.endpoint.transparency.response.PreTrade;
import dev.andstuff.kraken.api.rest.KrakenRestRequester;

@ExtendWith(MockitoExtension.class)
class KrakenAPITransparencyTest {

    @Mock private KrakenRestRequester requester;

    @Test
    void should_route_preTrade_symbol_without_credentials_when_called() {
        PreTrade preTradeResponse = new PreTrade("BTC/USD", null, null, null, null, null, null, null, null, null, null, null, List.of(), List.of());
        KrakenAPI unit = new KrakenAPI(null, requester);
        when(requester.execute(any(PreTradeEndpoint.class))).thenReturn(preTradeResponse);

        PreTrade result = unit.preTrade("BTC/USD");

        assertThat(result).isSameAs(preTradeResponse);
        verify(requester).execute(argThat((PreTradeEndpoint endpoint) -> endpoint.buildURL().getQuery().equals("symbol=BTC%2FUSD")));
    }

    @Test
    void should_route_postTrade_symbol_without_credentials_when_called() {
        PostTrade postTradeResponse = new PostTrade(null, 0, List.of());
        KrakenAPI unit = new KrakenAPI(null, requester);
        when(requester.execute(any(PostTradeEndpoint.class))).thenReturn(postTradeResponse);

        PostTrade result = unit.postTrade("BTC/USD");

        assertThat(result).isSameAs(postTradeResponse);
        verify(requester).execute(argThat((PostTradeEndpoint endpoint) -> endpoint.buildURL().getQuery().equals("symbol=BTC%2FUSD")));
    }

    @Test
    void should_route_postTrade_options_without_credentials_when_called() {
        PostTrade postTradeResponse = new PostTrade(Instant.parse("2024-05-30T12:34:56.123456789Z"), 0, List.of());
        KrakenAPI unit = new KrakenAPI(null, requester);
        PostTradeParams params = PostTradeParams.builder().symbol("BTC/USD").fromTimestamp(Instant.parse("2024-05-30T12:34:56.123456789Z")).count(10).build();
        when(requester.execute(any(PostTradeEndpoint.class))).thenReturn(postTradeResponse);

        PostTrade result = unit.postTrade(params);

        assertThat(result).isSameAs(postTradeResponse);
        verify(requester).execute(argThat((PostTradeEndpoint endpoint) -> endpoint.buildURL().getQuery().contains("from_ts=2024-05-30T12%3A34%3A56.123456789Z")));
    }
}
