package dev.andstuff.kraken.api.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.andstuff.kraken.api.endpoint.KrakenException;
import dev.andstuff.kraken.api.endpoint.trading.AddOrderBatchEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.AddOrderEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.CancelAllOrdersEndpoint;
import dev.andstuff.kraken.api.endpoint.trading.params.AddOrderParams;
import dev.andstuff.kraken.api.endpoint.trading.params.BatchOrder;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderSide;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderType;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderAdded;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderBatchAdded;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderCancellation;

@ExtendWith(MockitoExtension.class)
class DefaultKrakenRestRequesterTradingTest {

    @Mock private DefaultKrakenRestRequester.ConnectionFactory connectionFactory;
    @Mock private HttpsURLConnection connection;
    @Mock private KrakenCredentials credentials;
    @Mock private KrakenNonceGenerator nonceGenerator;

    @Test
    void should_sign_and_send_identical_json_when_adding_order_batch() throws Exception {
        DefaultKrakenRestRequester unit = new DefaultKrakenRestRequester(connectionFactory);
        AddOrderBatchEndpoint endpoint = new AddOrderBatchEndpoint("BTC/USD", List.of(
                BatchOrder.builder().orderType(OrderType.LIMIT).side(OrderSide.BUY).volume(new BigDecimal("1.2")).price("40000").build(),
                BatchOrder.builder().orderType(OrderType.LIMIT).side(OrderSide.SELL).volume(new BigDecimal("1.2")).price("42000").build()));
        URL url = endpoint.buildURL();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String body = "{\"orders\":[{\"ordertype\":\"limit\",\"type\":\"buy\",\"volume\":\"1.2\",\"price\":\"40000\"},{\"ordertype\":\"limit\",\"type\":\"sell\",\"volume\":\"1.2\",\"price\":\"42000\"}],\"pair\":\"BTC/USD\",\"nonce\":123}";
        when(nonceGenerator.generate()).thenReturn("123");
        when(connectionFactory.open(any(URL.class))).thenReturn(connection);
        when(connection.getURL()).thenReturn(url);
        when(credentials.getKey()).thenReturn("test-key");
        when(credentials.sign(url, "123", body)).thenReturn("test-signature");
        when(connection.getOutputStream()).thenReturn(output);
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("{\"error\":[],\"result\":{\"orders\":[{\"txid\":\"65LRD3-AHGRA-YAH8E5\"},{\"txid\":\"OK8HFF-5J2PL-XLR17S\"}]}}".getBytes(StandardCharsets.UTF_8)));

        OrderBatchAdded result = unit.execute(endpoint, credentials, nonceGenerator);

        assertThat(result.orders()).extracting(OrderBatchAdded.AddedOrder::transactionId).containsExactly("65LRD3-AHGRA-YAH8E5", "OK8HFF-5J2PL-XLR17S");
        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(body);
        verify(connection).setRequestMethod("POST");
        verify(connection).addRequestProperty("Content-Type", "application/json");
        verify(connection).addRequestProperty("API-Key", "test-key");
        verify(connection).addRequestProperty("API-Sign", "test-signature");
        verify(credentials).sign(url, "123", body);
    }

    @Test
    void should_sign_and_send_form_body_when_adding_order() throws Exception {
        DefaultKrakenRestRequester unit = new DefaultKrakenRestRequester(connectionFactory);
        AddOrderEndpoint endpoint = new AddOrderEndpoint(AddOrderParams.builder()
                .pair("XBTUSD").side(OrderSide.BUY).orderType(OrderType.LIMIT).volume(new BigDecimal("1.25")).price("27500").validate(true).build());
        URL url = endpoint.buildURL();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String body = endpoint.encodedParamsWith("123");
        when(nonceGenerator.generate()).thenReturn("123");
        when(connectionFactory.open(any(URL.class))).thenReturn(connection);
        when(connection.getURL()).thenReturn(url);
        when(credentials.getKey()).thenReturn("test-key");
        when(credentials.sign(url, "123", body)).thenReturn("test-signature");
        when(connection.getOutputStream()).thenReturn(output);
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("{\"error\":[],\"result\":{\"descr\":{\"order\":\"buy 1.25000000 XBTUSD @ limit 27500.0\"}}}".getBytes(StandardCharsets.UTF_8)));

        OrderAdded result = unit.execute(endpoint, credentials, nonceGenerator);

        assertThat(result.description().order()).isEqualTo("buy 1.25000000 XBTUSD @ limit 27500.0");
        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(body);
        verify(connection).addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        verify(connection).addRequestProperty("API-Sign", "test-signature");
    }

    @Test
    void should_sign_nonce_only_body_when_cancelling_all_orders() throws Exception {
        DefaultKrakenRestRequester unit = new DefaultKrakenRestRequester(connectionFactory);
        CancelAllOrdersEndpoint endpoint = new CancelAllOrdersEndpoint();
        URL url = endpoint.buildURL();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        when(nonceGenerator.generate()).thenReturn("123");
        when(connectionFactory.open(any(URL.class))).thenReturn(connection);
        when(connection.getURL()).thenReturn(url);
        when(credentials.getKey()).thenReturn("test-key");
        when(credentials.sign(url, "123", "nonce=123")).thenReturn("test-signature");
        when(connection.getOutputStream()).thenReturn(output);
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("{\"error\":[],\"result\":{\"count\":4}}".getBytes(StandardCharsets.UTF_8)));

        OrderCancellation result = unit.execute(endpoint, credentials, nonceGenerator);

        assertThat(result.count()).isEqualTo(4);
        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo("nonce=123");
        verify(connection).addRequestProperty("API-Sign", "test-signature");
    }

    @Test
    void should_propagate_errors_when_kraken_rejects_order() throws Exception {
        DefaultKrakenRestRequester unit = new DefaultKrakenRestRequester(connectionFactory);
        AddOrderEndpoint endpoint = new AddOrderEndpoint("XBTUSD", OrderSide.SELL, OrderType.MARKET, new BigDecimal("1000"));
        when(nonceGenerator.generate()).thenReturn("123");
        when(connectionFactory.open(any(URL.class))).thenReturn(connection);
        when(connection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("{\"error\":[\"EOrder:Insufficient funds\"]}".getBytes(StandardCharsets.UTF_8)));

        assertThatThrownBy(() -> unit.execute(endpoint, credentials, nonceGenerator)).isInstanceOf(KrakenException.class)
                .extracting(exception -> ((KrakenException) exception).getErrors()).isEqualTo(List.of("EOrder:Insufficient funds"));
    }
}
