package dev.andstuff.kraken.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.andstuff.kraken.api.endpoint.algotrading.AddStrategyEndpoint;
import dev.andstuff.kraken.api.endpoint.algotrading.CancelStrategyEndpoint;
import dev.andstuff.kraken.api.endpoint.algotrading.ClosedStrategiesEndpoint;
import dev.andstuff.kraken.api.endpoint.algotrading.OpenStrategiesEndpoint;
import dev.andstuff.kraken.api.endpoint.algotrading.QueryStrategiesEndpoint;
import dev.andstuff.kraken.api.endpoint.algotrading.params.AddStrategyParams;
import dev.andstuff.kraken.api.endpoint.algotrading.params.CancelStrategyParams;
import dev.andstuff.kraken.api.endpoint.algotrading.params.ClosedStrategiesParams;
import dev.andstuff.kraken.api.endpoint.algotrading.params.OpenStrategiesParams;
import dev.andstuff.kraken.api.endpoint.algotrading.params.QueryStrategiesParams;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyOrderType;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyStatus;
import dev.andstuff.kraken.api.endpoint.algotrading.params.StrategyType;
import dev.andstuff.kraken.api.endpoint.algotrading.response.AlgoStrategy;
import dev.andstuff.kraken.api.endpoint.algotrading.response.ClosedStrategies;
import dev.andstuff.kraken.api.endpoint.algotrading.response.OpenStrategies;
import dev.andstuff.kraken.api.endpoint.algotrading.response.StrategiesInfo;
import dev.andstuff.kraken.api.endpoint.algotrading.response.StrategyAdded;
import dev.andstuff.kraken.api.endpoint.algotrading.response.StrategyCancelled;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderSide;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import dev.andstuff.kraken.api.rest.KrakenNonceGenerator;
import dev.andstuff.kraken.api.rest.KrakenRestRequester;

@ExtendWith(MockitoExtension.class)
class KrakenAPIAlgoTradingTest {

    @Mock private KrakenCredentials credentials;
    @Mock private KrakenNonceGenerator nonceGenerator;
    @Mock private KrakenRestRequester requester;
    @Mock private Map<String, AlgoStrategy> strategies;

    @Test
    void should_route_addStrategy_options_when_called() {
        // Given
        StrategyAdded addStrategyResponse = new StrategyAdded("ASMBA64-R23M2-J7JRJV");
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        AddStrategyParams params = AddStrategyParams.builder().symbol("BTC/USD").side(OrderSide.BUY).volume(new BigDecimal("1.5"))
                .orderType(StrategyOrderType.LIMIT).price(new BigDecimal("67000.0")).expireTime("+3600").build();
        when(requester.execute(any(AddStrategyEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(addStrategyResponse);

        // When
        StrategyAdded result = unit.addStrategy(params);

        // Then
        assertThat(result).isSameAs(addStrategyResponse);
        verify(requester).execute(argThat((AddStrategyEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_addStrategy_when_credentials_are_missing() {
        // Given
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        AddStrategyParams params = AddStrategyParams.builder().symbol("BTC/USD").side(OrderSide.BUY).volume(BigDecimal.ONE)
                .orderType(StrategyOrderType.MARKET).expireTime("+600").build();

        // When
        Throwable result = catchThrowable(() -> unit.addStrategy(params));

        // Then
        assertThat(result).isInstanceOf(IllegalStateException.class).hasMessageContaining("AddStrategy");
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_cancelStrategy_identifier_when_called() {
        // Given
        StrategyCancelled cancelStrategyResponse = new StrategyCancelled("ASMBA64-R23M2-J7JRJV");
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(CancelStrategyEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(cancelStrategyResponse);

        // When
        StrategyCancelled result = unit.cancelStrategy("ASMBA64-R23M2-J7JRJV");

        // Then
        assertThat(result).isSameAs(cancelStrategyResponse);
        verify(requester).execute(argThat((CancelStrategyEndpoint endpoint) ->
                "ASMBA64-R23M2-J7JRJV".equals(((CancelStrategyParams) endpoint.getPostParams()).getStrategyId())), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_cancelStrategy_when_credentials_are_missing() {
        // Given
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        // When
        Throwable result = catchThrowable(() -> unit.cancelStrategy("ASMBA64-R23M2-J7JRJV"));

        // Then
        assertThat(result).isInstanceOf(IllegalStateException.class).hasMessageContaining("CancelStrategy");
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_openStrategies_with_default_options_when_called_without_parameters() {
        // Given
        OpenStrategies openStrategiesResponse = new OpenStrategies(Map.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(OpenStrategiesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(openStrategiesResponse);

        // When
        OpenStrategies result = unit.openStrategies();

        // Then
        assertThat(result).isSameAs(openStrategiesResponse);
        verify(requester).execute(argThat((OpenStrategiesEndpoint endpoint) ->
                ((OpenStrategiesParams) endpoint.getPostParams()).getStrategyType() == null), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_openStrategies_options_when_called() {
        // Given
        OpenStrategies openStrategiesResponse = new OpenStrategies(Map.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        OpenStrategiesParams params = OpenStrategiesParams.builder().strategyType(StrategyType.TWAP).build();
        when(requester.execute(any(OpenStrategiesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(openStrategiesResponse);

        // When
        OpenStrategies result = unit.openStrategies(params);

        // Then
        assertThat(result).isSameAs(openStrategiesResponse);
        verify(requester).execute(argThat((OpenStrategiesEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_openStrategies_when_credentials_are_missing() {
        // Given
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        // When
        Throwable result = catchThrowable(unit::openStrategies);

        // Then
        assertThat(result).isInstanceOf(IllegalStateException.class).hasMessageContaining("GetOpenStrategies");
        verifyNoInteractions(requester);
    }

    @Test
    void should_return_strategies_map_when_queryStrategies_is_called() {
        // Given
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(QueryStrategiesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(new StrategiesInfo(strategies));

        // When
        Map<String, AlgoStrategy> result = unit.queryStrategies(List.of("ASPBVCI-6MKBA-QE4VF6", "ASMBA64-R23M2-J7JRJV"));

        // Then
        assertThat(result).isSameAs(strategies);
        verify(requester).execute(argThat((QueryStrategiesEndpoint endpoint) ->
                List.of("ASPBVCI-6MKBA-QE4VF6", "ASMBA64-R23M2-J7JRJV").equals(((QueryStrategiesParams) endpoint.getPostParams()).getStrategyIds())),
                same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_queryStrategies_when_credentials_are_missing() {
        // Given
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        List<String> strategyIds = List.of("ASPBVCI-6MKBA-QE4VF6");

        // When
        Throwable result = catchThrowable(() -> unit.queryStrategies(strategyIds));

        // Then
        assertThat(result).isInstanceOf(IllegalStateException.class).hasMessageContaining("QueryStrategies");
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_closedStrategies_with_default_options_when_called_without_parameters() {
        // Given
        ClosedStrategies closedStrategiesResponse = new ClosedStrategies(List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(ClosedStrategiesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(closedStrategiesResponse);

        // When
        ClosedStrategies result = unit.closedStrategies();

        // Then
        assertThat(result).isSameAs(closedStrategiesResponse);
        verify(requester).execute(argThat((ClosedStrategiesEndpoint endpoint) -> "GetClosedStrategies".equals(endpoint.getPath())), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_closedStrategies_options_when_called() {
        // Given
        ClosedStrategies closedStrategiesResponse = new ClosedStrategies(List.of(), "Q0xPU0VEOjE3ODExNzAwMDA6QVNNQkE2NA");
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        ClosedStrategiesParams params = ClosedStrategiesParams.builder().status(StrategyStatus.EXPIRED).limit(10).build();
        when(requester.execute(any(ClosedStrategiesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(closedStrategiesResponse);

        // When
        ClosedStrategies result = unit.closedStrategies(params);

        // Then
        assertThat(result).isSameAs(closedStrategiesResponse);
        verify(requester).execute(argThat((ClosedStrategiesEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_closedStrategies_when_credentials_are_missing() {
        // Given
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        // When
        Throwable result = catchThrowable(unit::closedStrategies);

        // Then
        assertThat(result).isInstanceOf(IllegalStateException.class).hasMessageContaining("GetClosedStrategies");
        verifyNoInteractions(requester);
    }

    @Test
    void should_list_algo_trading_paths_when_reading_private_endpoints() {
        // When
        List<String> result = Arrays.stream(KrakenAPI.Private.values()).map(KrakenAPI.Private::getPath).toList();

        // Then
        assertThat(result).contains("AddStrategy", "CancelStrategy", "GetOpenStrategies", "QueryStrategies", "GetClosedStrategies");
    }
}
