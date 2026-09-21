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
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.andstuff.kraken.api.endpoint.trading.AddOrderBatchEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.AddOrderEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.AmendOrderEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.CancelAllOrdersAfterEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.CancelAllOrdersEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.CancelOrderBatchEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.CancelOrderEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.EditOrderEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.WebSocketsTokenEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.params.AddOrderBatchParams;
import dev.andstuff.kraken.api.endpoint.trading.params.AddOrderParams;
import dev.andstuff.kraken.api.endpoint.trading.params.AmendOrderParams;
import dev.andstuff.kraken.api.endpoint.trading.params.BatchOrder;
import dev.andstuff.kraken.api.endpoint.trading.params.CancelAllOrdersAfterParams;
import dev.andstuff.kraken.api.endpoint.trading.params.CancelOrderBatchParams;
import dev.andstuff.kraken.api.endpoint.trading.params.CancelOrderParams;
import dev.andstuff.kraken.api.endpoint.trading.params.EditOrderParams;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderSide;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderType;
import dev.andstuff.kraken.api.endpoint.trading.response.DeadMansSwitch;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderAdded;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderAmended;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderBatchAdded;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderCancellation;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderEdited;
import dev.andstuff.kraken.api.endpoint.trading.response.WebSocketsToken;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import dev.andstuff.kraken.api.rest.KrakenNonceGenerator;
import dev.andstuff.kraken.api.rest.KrakenRestRequester;

@ExtendWith(MockitoExtension.class)
class KrakenAPITradingTest {

    @Mock private KrakenCredentials credentials;
    @Mock private KrakenNonceGenerator nonceGenerator;
    @Mock private KrakenRestRequester requester;

    @Test
    void should_route_addOrder_options_when_called() {
        OrderAdded addOrderResponse = new OrderAdded(null, List.of("OUF4EM-FRGI2-MQMWZD"));
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        AddOrderParams params = AddOrderParams.builder().pair("XBTUSD").side(OrderSide.BUY).orderType(OrderType.LIMIT).volume(BigDecimal.ONE).price("27500").validate(true).build();
        when(requester.execute(any(AddOrderEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(addOrderResponse);

        OrderAdded result = unit.addOrder(params);

        assertThat(result).isSameAs(addOrderResponse);
        verify(requester).execute(argThat((AddOrderEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_addOrder_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        AddOrderParams params = AddOrderParams.builder().pair("XBTUSD").side(OrderSide.BUY).orderType(OrderType.MARKET).volume(BigDecimal.ONE).build();

        assertThatThrownBy(() -> unit.addOrder(params)).isInstanceOf(IllegalStateException.class).hasMessageContaining("AddOrder");
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_addOrderBatch_options_when_called() {
        OrderBatchAdded addOrderBatchResponse = new OrderBatchAdded(List.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        AddOrderBatchParams params = AddOrderBatchParams.builder().pair("BTC/USD").orders(List.of(
                BatchOrder.builder().orderType(OrderType.LIMIT).side(OrderSide.BUY).volume(BigDecimal.ONE).price("40000").build(),
                BatchOrder.builder().orderType(OrderType.LIMIT).side(OrderSide.SELL).volume(BigDecimal.ONE).price("42000").build())).build();
        when(requester.execute(any(AddOrderBatchEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(addOrderBatchResponse);

        OrderBatchAdded result = unit.addOrderBatch(params);

        assertThat(result).isSameAs(addOrderBatchResponse);
        verify(requester).execute(argThat((AddOrderBatchEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_addOrderBatch_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        AddOrderBatchParams params = AddOrderBatchParams.builder().pair("BTC/USD").orders(List.of()).build();

        assertThatThrownBy(() -> unit.addOrderBatch(params)).isInstanceOf(IllegalStateException.class).hasMessageContaining("AddOrderBatch");
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_amendOrder_options_when_called() {
        OrderAmended amendOrderResponse = new OrderAmended("TEZA4R-DSDGT-IJBOJK");
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        AmendOrderParams params = AmendOrderParams.builder().transactionId("OHYO67-6LP66-HMQ437").orderQuantity(BigDecimal.TWO).build();
        when(requester.execute(any(AmendOrderEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(amendOrderResponse);

        OrderAmended result = unit.amendOrder(params);

        assertThat(result).isSameAs(amendOrderResponse);
        verify(requester).execute(argThat((AmendOrderEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_amendOrder_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        AmendOrderParams params = AmendOrderParams.builder().transactionId("OHYO67-6LP66-HMQ437").build();

        assertThatThrownBy(() -> unit.amendOrder(params)).isInstanceOf(IllegalStateException.class).hasMessageContaining("AmendOrder");
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_editOrder_options_when_called() {
        OrderEdited editOrderResponse = new OrderEdited(null, "OFVXHJ-KPQ3B-VS7ELA", null, null, 1, "OHYO67-6LP66-HMQ437", OrderEdited.Status.OK, null, null, null, null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        EditOrderParams params = EditOrderParams.builder().transactionId("OHYO67-6LP66-HMQ437").pair("XBTUSD").volume(BigDecimal.TWO).build();
        when(requester.execute(any(EditOrderEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(editOrderResponse);

        OrderEdited result = unit.editOrder(params);

        assertThat(result).isSameAs(editOrderResponse);
        verify(requester).execute(argThat((EditOrderEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_editOrder_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        EditOrderParams params = EditOrderParams.builder().transactionId("OHYO67-6LP66-HMQ437").pair("XBTUSD").build();

        assertThatThrownBy(() -> unit.editOrder(params)).isInstanceOf(IllegalStateException.class).hasMessageContaining("EditOrder");
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_cancelOrder_options_when_called() {
        OrderCancellation cancelOrderResponse = new OrderCancellation(1, false);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        CancelOrderParams params = CancelOrderParams.builder().clientOrderId("arb-20240509-00010").build();
        when(requester.execute(any(CancelOrderEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(cancelOrderResponse);

        OrderCancellation result = unit.cancelOrder(params);

        assertThat(result).isSameAs(cancelOrderResponse);
        verify(requester).execute(argThat((CancelOrderEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_cancelOrder_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        CancelOrderParams params = CancelOrderParams.builder().transactionId("OHYO67-6LP66-HMQ437").build();

        assertThatThrownBy(() -> unit.cancelOrder(params)).isInstanceOf(IllegalStateException.class).hasMessageContaining("CancelOrder");
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_cancelOrderBatch_options_when_called() {
        OrderCancellation cancelOrderBatchResponse = new OrderCancellation(2, false);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        CancelOrderBatchParams params = CancelOrderBatchParams.builder().transactionIds(List.of("OP5V2Y-RYKVL-ET3V3B", "OP5V2Y-7YKVL-ET3V3B")).build();
        when(requester.execute(any(CancelOrderBatchEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(cancelOrderBatchResponse);

        OrderCancellation result = unit.cancelOrderBatch(params);

        assertThat(result).isSameAs(cancelOrderBatchResponse);
        verify(requester).execute(argThat((CancelOrderBatchEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_cancelOrderBatch_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        CancelOrderBatchParams params = CancelOrderBatchParams.builder().userReferences(List.of(1234)).build();

        assertThatThrownBy(() -> unit.cancelOrderBatch(params)).isInstanceOf(IllegalStateException.class).hasMessageContaining("CancelOrderBatch");
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_cancelAllOrders_when_called() {
        OrderCancellation cancelAllResponse = new OrderCancellation(3, false);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(CancelAllOrdersEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(cancelAllResponse);

        OrderCancellation result = unit.cancelAllOrders();

        assertThat(result).isSameAs(cancelAllResponse);
        verify(requester).execute(argThat((CancelAllOrdersEndpoint endpoint) -> "CancelAll".equals(endpoint.getPath())), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_cancelAllOrders_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(unit::cancelAllOrders).isInstanceOf(IllegalStateException.class).hasMessageContaining("CancelAll");
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_cancelAllOrdersAfter_options_when_called() {
        DeadMansSwitch cancelAllOrdersAfterResponse = new DeadMansSwitch(Instant.parse("2023-03-24T17:41:56Z"), Instant.parse("2023-03-24T17:42:56Z"));
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        CancelAllOrdersAfterParams params = CancelAllOrdersAfterParams.builder().timeout(Duration.ofSeconds(60)).build();
        when(requester.execute(any(CancelAllOrdersAfterEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(cancelAllOrdersAfterResponse);

        DeadMansSwitch result = unit.cancelAllOrdersAfter(params);

        assertThat(result).isSameAs(cancelAllOrdersAfterResponse);
        verify(requester).execute(argThat((CancelAllOrdersAfterEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_cancelAllOrdersAfter_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        CancelAllOrdersAfterParams params = CancelAllOrdersAfterParams.builder().timeout(Duration.ZERO).build();

        assertThatThrownBy(() -> unit.cancelAllOrdersAfter(params)).isInstanceOf(IllegalStateException.class).hasMessageContaining("CancelAllOrdersAfter");
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_webSocketsToken_when_called() {
        WebSocketsToken webSocketsTokenResponse = new WebSocketsToken("1Dwc4lzSwNWOAwkMdqhssNNFhs1ed606d1WcF3XfEMw", Duration.ofMinutes(15));
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(WebSocketsTokenEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(webSocketsTokenResponse);

        WebSocketsToken result = unit.webSocketsToken();

        assertThat(result).isSameAs(webSocketsTokenResponse);
        verify(requester).execute(argThat((WebSocketsTokenEndpoint endpoint) -> "GetWebSocketsToken".equals(endpoint.getPath())), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_webSocketsToken_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(unit::webSocketsToken).isInstanceOf(IllegalStateException.class).hasMessageContaining("GetWebSocketsToken");
        verifyNoInteractions(requester);
    }
}
