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
import javax.net.ssl.HttpsURLConnection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.andstuff.kraken.api.endpoint.KrakenException;
import dev.andstuff.kraken.api.endpoint.funding.CancelWithdrawalEndpoint;
import dev.andstuff.kraken.api.endpoint.funding.WithdrawEndpoint;
import dev.andstuff.kraken.api.endpoint.funding.params.WithdrawParams;

@ExtendWith(MockitoExtension.class)
class DefaultKrakenRestRequesterFundingTest {

    @Mock private DefaultKrakenRestRequester.ConnectionFactory connectionFactory;
    @Mock private HttpsURLConnection connection;
    @Mock private KrakenCredentials credentials;
    @Mock private KrakenNonceGenerator nonceGenerator;

    @Test
    void should_return_false_when_kraken_cannot_cancel_withdrawal() throws Exception {
        DefaultKrakenRestRequester unit = new DefaultKrakenRestRequester(connectionFactory);
        CancelWithdrawalEndpoint endpoint = new CancelWithdrawalEndpoint("XBT", "reference");
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
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("{\"error\":[],\"result\":false}".getBytes(StandardCharsets.UTF_8)));

        Boolean result = unit.execute(endpoint, credentials, nonceGenerator);

        assertThat(result).isFalse();
        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(body);
        verify(credentials).sign(url, "123", body);
        verify(connection).addRequestProperty("API-Key", "test-key");
        verify(connection).addRequestProperty("API-Sign", "test-signature");
    }

    @Test
    void should_propagate_errors_when_kraken_rejects_withdrawal() throws Exception {
        DefaultKrakenRestRequester unit = new DefaultKrakenRestRequester(connectionFactory);
        WithdrawEndpoint endpoint = new WithdrawEndpoint(WithdrawParams.builder()
                .asset("XBT").key("saved-key").amount(new BigDecimal("0.01")).build());
        when(nonceGenerator.generate()).thenReturn("123");
        when(connectionFactory.open(any(URL.class))).thenReturn(connection);
        when(connection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("{\"error\":[\"EFunding:Max fee exceeded\"]}".getBytes(StandardCharsets.UTF_8)));

        assertThatThrownBy(() -> unit.execute(endpoint, credentials, nonceGenerator)).isInstanceOf(KrakenException.class);
    }
}
