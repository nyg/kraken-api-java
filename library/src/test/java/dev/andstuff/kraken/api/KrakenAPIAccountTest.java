package dev.andstuff.kraken.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.andstuff.kraken.api.endpoint.account.AccountBalanceEndpoint;
import dev.andstuff.kraken.api.endpoint.account.ApiKeyInfoEndpoint;
import dev.andstuff.kraken.api.endpoint.account.ClosedOrdersEndpoint;
import dev.andstuff.kraken.api.endpoint.account.CreditLinesEndpoint;
import dev.andstuff.kraken.api.endpoint.account.ExtendedBalanceEndpoint;
import dev.andstuff.kraken.api.endpoint.account.OpenOrdersEndpoint;
import dev.andstuff.kraken.api.endpoint.account.OpenPositionsEndpoint;
import dev.andstuff.kraken.api.endpoint.account.OrderAmendsEndpoint;
import dev.andstuff.kraken.api.endpoint.account.QueryOrdersEndpoint;
import dev.andstuff.kraken.api.endpoint.account.QueryTradesEndpoint;
import dev.andstuff.kraken.api.endpoint.account.TradeBalanceEndpoint;
import dev.andstuff.kraken.api.endpoint.account.TradeVolumeEndpoint;
import dev.andstuff.kraken.api.endpoint.account.TradesHistoryEndpoint;
import dev.andstuff.kraken.api.endpoint.account.WalletAccountsEndpoint;
import dev.andstuff.kraken.api.endpoint.account.params.AccountBalanceParams;
import dev.andstuff.kraken.api.endpoint.account.params.ApiKeyInfoParams;
import dev.andstuff.kraken.api.endpoint.account.params.ClosedOrdersParams;
import dev.andstuff.kraken.api.endpoint.account.params.CreditLinesParams;
import dev.andstuff.kraken.api.endpoint.account.params.ExtendedBalanceParams;
import dev.andstuff.kraken.api.endpoint.account.params.OpenOrdersParams;
import dev.andstuff.kraken.api.endpoint.account.params.OpenPositionsParams;
import dev.andstuff.kraken.api.endpoint.account.params.OrderAmendsParams;
import dev.andstuff.kraken.api.endpoint.account.params.QueryOrdersParams;
import dev.andstuff.kraken.api.endpoint.account.params.QueryTradesParams;
import dev.andstuff.kraken.api.endpoint.account.params.TradeBalanceParams;
import dev.andstuff.kraken.api.endpoint.account.params.TradeVolumeParams;
import dev.andstuff.kraken.api.endpoint.account.params.TradesHistoryParams;
import dev.andstuff.kraken.api.endpoint.account.params.WalletAccountsParams;
import dev.andstuff.kraken.api.endpoint.account.response.AccountTrade;
import dev.andstuff.kraken.api.endpoint.account.response.ApiKeyInfo;
import dev.andstuff.kraken.api.endpoint.account.response.ClosedOrders;
import dev.andstuff.kraken.api.endpoint.account.response.CreditLines;
import dev.andstuff.kraken.api.endpoint.account.response.ExtendedBalance;
import dev.andstuff.kraken.api.endpoint.account.response.OpenOrders;
import dev.andstuff.kraken.api.endpoint.account.response.OpenPosition;
import dev.andstuff.kraken.api.endpoint.account.response.Order;
import dev.andstuff.kraken.api.endpoint.account.response.OrderAmends;
import dev.andstuff.kraken.api.endpoint.account.response.TradeBalance;
import dev.andstuff.kraken.api.endpoint.account.response.TradeVolume;
import dev.andstuff.kraken.api.endpoint.account.response.TradesHistory;
import dev.andstuff.kraken.api.endpoint.account.response.WalletAccounts;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import dev.andstuff.kraken.api.rest.KrakenNonceGenerator;
import dev.andstuff.kraken.api.rest.KrakenRestRequester;

@ExtendWith(MockitoExtension.class)
class KrakenAPIAccountTest {

    @Mock private KrakenCredentials credentials;
    @Mock private KrakenNonceGenerator nonceGenerator;
    @Mock private KrakenRestRequester requester;
    @Mock private Map<String, BigDecimal> accountBalanceResponse;
    @Mock private Map<String, ExtendedBalance> extendedBalanceResponse;
    @Mock private Map<String, Order> queryOrdersResponse;
    @Mock private Map<String, AccountTrade> queryTradesResponse;
    @Mock private Map<String, OpenPosition> openPositionsResponse;

    @Test
    void should_route_accountBalance_options_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        AccountBalanceParams params = AccountBalanceParams.builder().build();
        when(requester.execute(any(AccountBalanceEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(accountBalanceResponse);

        Map<String, BigDecimal> result = unit.accountBalance(params);

        assertThat(result).isSameAs(accountBalanceResponse);
        verify(requester).execute(argThat((AccountBalanceEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_accountBalance_defaults_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(AccountBalanceEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(accountBalanceResponse);

        Map<String, BigDecimal> result = unit.accountBalance();

        assertThat(result).isSameAs(accountBalanceResponse);
        verify(requester).execute(any(AccountBalanceEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_accountBalance_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(() -> unit.accountBalance()).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_extendedBalance_options_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        ExtendedBalanceParams params = ExtendedBalanceParams.builder().build();
        when(requester.execute(any(ExtendedBalanceEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(extendedBalanceResponse);

        Map<String, ExtendedBalance> result = unit.extendedBalance(params);

        assertThat(result).isSameAs(extendedBalanceResponse);
        verify(requester).execute(argThat((ExtendedBalanceEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_extendedBalance_defaults_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(ExtendedBalanceEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(extendedBalanceResponse);

        Map<String, ExtendedBalance> result = unit.extendedBalance();

        assertThat(result).isSameAs(extendedBalanceResponse);
        verify(requester).execute(any(ExtendedBalanceEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_extendedBalance_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(() -> unit.extendedBalance()).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_creditLines_options_when_called() {
        Optional<CreditLines> creditLinesResponse = Optional.of(new CreditLines(Map.of(), null));
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        CreditLinesParams params = CreditLinesParams.builder().build();
        when(requester.execute(any(CreditLinesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(creditLinesResponse);

        Optional<CreditLines> result = unit.creditLines(params);

        assertThat(result).isSameAs(creditLinesResponse);
        verify(requester).execute(argThat((CreditLinesEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_creditLines_defaults_when_called() {
        Optional<CreditLines> creditLinesResponse = Optional.of(new CreditLines(Map.of(), null));
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(CreditLinesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(creditLinesResponse);

        Optional<CreditLines> result = unit.creditLines();

        assertThat(result).isSameAs(creditLinesResponse);
        verify(requester).execute(any(CreditLinesEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_creditLines_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(() -> unit.creditLines()).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_tradeBalance_options_when_called() {
        TradeBalance tradeBalanceResponse = new TradeBalance(null, null, null, null, null, null, null, null, null, null, null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        TradeBalanceParams params = TradeBalanceParams.builder().build();
        when(requester.execute(any(TradeBalanceEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(tradeBalanceResponse);

        TradeBalance result = unit.tradeBalance(params);

        assertThat(result).isSameAs(tradeBalanceResponse);
        verify(requester).execute(argThat((TradeBalanceEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_tradeBalance_defaults_when_called() {
        TradeBalance tradeBalanceResponse = new TradeBalance(null, null, null, null, null, null, null, null, null, null, null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(TradeBalanceEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(tradeBalanceResponse);

        TradeBalance result = unit.tradeBalance();

        assertThat(result).isSameAs(tradeBalanceResponse);
        verify(requester).execute(any(TradeBalanceEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_tradeBalance_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(() -> unit.tradeBalance()).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_openOrders_options_when_called() {
        OpenOrders openOrdersResponse = new OpenOrders(Map.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        OpenOrdersParams params = OpenOrdersParams.builder().build();
        when(requester.execute(any(OpenOrdersEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(openOrdersResponse);

        OpenOrders result = unit.openOrders(params);

        assertThat(result).isSameAs(openOrdersResponse);
        verify(requester).execute(argThat((OpenOrdersEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_openOrders_defaults_when_called() {
        OpenOrders openOrdersResponse = new OpenOrders(Map.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(OpenOrdersEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(openOrdersResponse);

        OpenOrders result = unit.openOrders();

        assertThat(result).isSameAs(openOrdersResponse);
        verify(requester).execute(any(OpenOrdersEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_openOrders_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(() -> unit.openOrders()).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_closedOrders_options_when_called() {
        ClosedOrders closedOrdersResponse = new ClosedOrders(Map.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        ClosedOrdersParams params = ClosedOrdersParams.builder().build();
        when(requester.execute(any(ClosedOrdersEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(closedOrdersResponse);

        ClosedOrders result = unit.closedOrders(params);

        assertThat(result).isSameAs(closedOrdersResponse);
        verify(requester).execute(argThat((ClosedOrdersEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_closedOrders_defaults_when_called() {
        ClosedOrders closedOrdersResponse = new ClosedOrders(Map.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(ClosedOrdersEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(closedOrdersResponse);

        ClosedOrders result = unit.closedOrders();

        assertThat(result).isSameAs(closedOrdersResponse);
        verify(requester).execute(any(ClosedOrdersEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_closedOrders_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(() -> unit.closedOrders()).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_queryOrders_options_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        QueryOrdersParams params = QueryOrdersParams.builder().transactionIds(List.of("ID-1", "ID+2")).build();
        when(requester.execute(any(QueryOrdersEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(queryOrdersResponse);

        Map<String, Order> result = unit.queryOrders(params);

        assertThat(result).isSameAs(queryOrdersResponse);
        verify(requester).execute(argThat((QueryOrdersEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_queryOrders_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        QueryOrdersParams params = QueryOrdersParams.builder().transactionIds(List.of("ID-1", "ID+2")).build();

        assertThatThrownBy(() -> unit.queryOrders(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_orderAmends_options_when_called() {
        OrderAmends orderAmendsResponse = new OrderAmends(null, List.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        OrderAmendsParams params = OrderAmendsParams.builder().build();
        when(requester.execute(any(OrderAmendsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(orderAmendsResponse);

        OrderAmends result = unit.orderAmends(params);

        assertThat(result).isSameAs(orderAmendsResponse);
        verify(requester).execute(argThat((OrderAmendsEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_orderAmends_defaults_when_called() {
        OrderAmends orderAmendsResponse = new OrderAmends(null, List.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(OrderAmendsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(orderAmendsResponse);

        OrderAmends result = unit.orderAmends();

        assertThat(result).isSameAs(orderAmendsResponse);
        verify(requester).execute(any(OrderAmendsEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_orderAmends_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(() -> unit.orderAmends()).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_tradesHistory_options_when_called() {
        TradesHistory tradesHistoryResponse = new TradesHistory(Map.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        TradesHistoryParams params = TradesHistoryParams.builder().build();
        when(requester.execute(any(TradesHistoryEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(tradesHistoryResponse);

        TradesHistory result = unit.tradesHistory(params);

        assertThat(result).isSameAs(tradesHistoryResponse);
        verify(requester).execute(argThat((TradesHistoryEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_tradesHistory_defaults_when_called() {
        TradesHistory tradesHistoryResponse = new TradesHistory(Map.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(TradesHistoryEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(tradesHistoryResponse);

        TradesHistory result = unit.tradesHistory();

        assertThat(result).isSameAs(tradesHistoryResponse);
        verify(requester).execute(any(TradesHistoryEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_tradesHistory_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(() -> unit.tradesHistory()).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_queryTrades_options_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        QueryTradesParams params = QueryTradesParams.builder().transactionIds(List.of("ID-1", "ID+2")).build();
        when(requester.execute(any(QueryTradesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(queryTradesResponse);

        Map<String, AccountTrade> result = unit.queryTrades(params);

        assertThat(result).isSameAs(queryTradesResponse);
        verify(requester).execute(argThat((QueryTradesEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_queryTrades_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        QueryTradesParams params = QueryTradesParams.builder().transactionIds(List.of("ID-1", "ID+2")).build();

        assertThatThrownBy(() -> unit.queryTrades(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_openPositions_options_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        OpenPositionsParams params = OpenPositionsParams.builder().build();
        when(requester.execute(any(OpenPositionsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(openPositionsResponse);

        Map<String, OpenPosition> result = unit.openPositions(params);

        assertThat(result).isSameAs(openPositionsResponse);
        verify(requester).execute(argThat((OpenPositionsEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_openPositions_defaults_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(OpenPositionsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(openPositionsResponse);

        Map<String, OpenPosition> result = unit.openPositions();

        assertThat(result).isSameAs(openPositionsResponse);
        verify(requester).execute(any(OpenPositionsEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_openPositions_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(() -> unit.openPositions()).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_tradeVolume_options_when_called() {
        TradeVolume tradeVolumeResponse = new TradeVolume(null, null, null, null, Map.of(), Map.of(), List.of(), List.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        TradeVolumeParams params = TradeVolumeParams.builder().build();
        when(requester.execute(any(TradeVolumeEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(tradeVolumeResponse);

        TradeVolume result = unit.tradeVolume(params);

        assertThat(result).isSameAs(tradeVolumeResponse);
        verify(requester).execute(argThat((TradeVolumeEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_tradeVolume_defaults_when_called() {
        TradeVolume tradeVolumeResponse = new TradeVolume(null, null, null, null, Map.of(), Map.of(), List.of(), List.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(TradeVolumeEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(tradeVolumeResponse);

        TradeVolume result = unit.tradeVolume();

        assertThat(result).isSameAs(tradeVolumeResponse);
        verify(requester).execute(any(TradeVolumeEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_tradeVolume_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(() -> unit.tradeVolume()).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_apiKeyInfo_options_when_called() {
        ApiKeyInfo apiKeyInfoResponse = new ApiKeyInfo(null, null, null, null, List.of(), null, null, null, null, null, null, List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        ApiKeyInfoParams params = ApiKeyInfoParams.builder().build();
        when(requester.execute(any(ApiKeyInfoEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(apiKeyInfoResponse);

        ApiKeyInfo result = unit.apiKeyInfo(params);

        assertThat(result).isSameAs(apiKeyInfoResponse);
        verify(requester).execute(argThat((ApiKeyInfoEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_apiKeyInfo_defaults_when_called() {
        ApiKeyInfo apiKeyInfoResponse = new ApiKeyInfo(null, null, null, null, List.of(), null, null, null, null, null, null, List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(ApiKeyInfoEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(apiKeyInfoResponse);

        ApiKeyInfo result = unit.apiKeyInfo();

        assertThat(result).isSameAs(apiKeyInfoResponse);
        verify(requester).execute(any(ApiKeyInfoEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_apiKeyInfo_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(() -> unit.apiKeyInfo()).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_walletAccounts_options_when_called() {
        WalletAccounts walletAccountsResponse = new WalletAccounts(List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        WalletAccountsParams params = WalletAccountsParams.builder().build();
        when(requester.execute(any(WalletAccountsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(walletAccountsResponse);

        WalletAccounts result = unit.walletAccounts(params);

        assertThat(result).isSameAs(walletAccountsResponse);
        verify(requester).execute(argThat((WalletAccountsEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_walletAccounts_defaults_when_called() {
        WalletAccounts walletAccountsResponse = new WalletAccounts(List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(WalletAccountsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(walletAccountsResponse);

        WalletAccounts result = unit.walletAccounts();

        assertThat(result).isSameAs(walletAccountsResponse);
        verify(requester).execute(any(WalletAccountsEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_walletAccounts_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(() -> unit.walletAccounts()).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

}
