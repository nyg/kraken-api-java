package dev.andstuff.kraken.api.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import javax.net.ssl.HttpsURLConnection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.andstuff.kraken.api.endpoint.KrakenException;
import dev.andstuff.kraken.api.endpoint.account.CreditLinesEndpoint;
import dev.andstuff.kraken.api.endpoint.account.TradeVolumeEndpoint;
import dev.andstuff.kraken.api.endpoint.account.params.TradeVolumeParams;
import dev.andstuff.kraken.api.endpoint.account.response.CreditLines;
import dev.andstuff.kraken.api.endpoint.account.response.TradeVolume;

@ExtendWith(MockitoExtension.class)
class DefaultKrakenRestRequesterAccountTest {

    @Mock private DefaultKrakenRestRequester.ConnectionFactory connectionFactory;
    @Mock private HttpsURLConnection connection;
    @Mock private KrakenCredentials credentials;
    @Mock private KrakenNonceGenerator nonceGenerator;

    @Test
    void should_sign_and_send_identical_json_when_requesting_trade_volume() throws Exception {
        DefaultKrakenRestRequester unit = new DefaultKrakenRestRequester(connectionFactory);
        TradeVolumeEndpoint endpoint = new TradeVolumeEndpoint(TradeVolumeParams.builder()
                .pairsWithClass(List.of(new TradeVolumeParams.Pair("TSLAx/USD", "equity_pair"))).build());
        URL url = endpoint.buildURL();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String body = "{\"nonce\":123,\"pair\":[{\"asset\":\"TSLAx/USD\",\"aclass\":\"equity_pair\"}]}";
        when(nonceGenerator.generate()).thenReturn("123");
        when(connectionFactory.open(any(URL.class))).thenReturn(connection);
        when(connection.getURL()).thenReturn(url);
        when(credentials.getKey()).thenReturn("test-key");
        when(credentials.sign(url, "123", body)).thenReturn("test-signature");
        when(connection.getOutputStream()).thenReturn(output);
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("{\"error\":[],\"result\":{\"volume\":\"0.0000000000123456789\"}}".getBytes(StandardCharsets.UTF_8)));

        TradeVolume result = unit.execute(endpoint, credentials, nonceGenerator);

        assertThat(result.volume()).isEqualByComparingTo("0.0000000000123456789");
        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(body);
        verify(connection).setRequestMethod("POST");
        verify(connection).addRequestProperty("Content-Type", "application/json");
        verify(connection).addRequestProperty("API-Key", "test-key");
        verify(connection).addRequestProperty("API-Sign", "test-signature");
        verify(credentials).sign(url, "123", body);
    }

    @Test
    void should_return_empty_when_credit_lines_response_has_no_credit() throws Exception {
        DefaultKrakenRestRequester unit = new DefaultKrakenRestRequester(connectionFactory);
        CreditLinesEndpoint endpoint = new CreditLinesEndpoint();
        when(nonceGenerator.generate()).thenReturn("123");
        when(connectionFactory.open(any(URL.class))).thenReturn(connection);
        when(connection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("{\"error\":[],\"result\":null}".getBytes(StandardCharsets.UTF_8)));

        Optional<CreditLines> result = unit.execute(endpoint, credentials, nonceGenerator);

        assertThat(result).isEmpty();
        verify(connection).addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    }

    @Test
    void should_propagate_errors_when_kraken_rejects_trade_volume() throws Exception {
        DefaultKrakenRestRequester unit = new DefaultKrakenRestRequester(connectionFactory);
        TradeVolumeEndpoint endpoint = new TradeVolumeEndpoint();
        when(nonceGenerator.generate()).thenReturn("123");
        when(connectionFactory.open(any(URL.class))).thenReturn(connection);
        when(connection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("{\"error\":[\"EGeneral:Permission denied\"]}".getBytes(StandardCharsets.UTF_8)));

        assertThatThrownBy(() -> unit.execute(endpoint, credentials, nonceGenerator)).isInstanceOf(KrakenException.class);
    }
}
