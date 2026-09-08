package dev.andstuff.kraken.api.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.type.TypeFactory;

import dev.andstuff.kraken.api.endpoint.KrakenException;
import dev.andstuff.kraken.api.endpoint.funding.CancelWithdrawalEndpoint;
import dev.andstuff.kraken.api.endpoint.funding.WithdrawEndpoint;
import dev.andstuff.kraken.api.endpoint.funding.params.WithdrawParams;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FundingRestRequesterTest {

    @InjectMocks private DefaultKrakenRestRequester unit;
    @Mock(answer = Answers.CALLS_REAL_METHODS) private CancelWithdrawalEndpoint cancellation;
    @Mock(answer = Answers.CALLS_REAL_METHODS) private WithdrawEndpoint withdrawal;
    @Mock private WithdrawParams withdrawalParams;
    @Mock private HttpsURLConnection connection;
    @Mock private URL url;
    @Mock private KrakenCredentials credentials;
    @Mock private KrakenNonceGenerator nonceGenerator;

    @Test
    void should_return_false_when_kraken_cannot_cancel_withdrawal() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String body = "asset=XBT&refid=reference&nonce=123";
        when(nonceGenerator.generate()).thenReturn("123");
        doReturn(body).when(cancellation).encodedParamsWith("123");
        doReturn(url).when(cancellation).buildURL();
        when(url.openConnection()).thenReturn(connection);
        when(connection.getURL()).thenReturn(url);
        doReturn("POST").when(cancellation).getHttpMethod();
        when(credentials.getKey()).thenReturn("test-key");
        when(credentials.sign(url, "123", body)).thenReturn("test-signature");
        when(connection.getOutputStream()).thenReturn(output);
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("{\"error\":[],\"result\":false}".getBytes(StandardCharsets.UTF_8)));
        doReturn(new CancelWithdrawalEndpoint("XBT", "reference").wrappedResponseType(TypeFactory.createDefaultInstance())).when(cancellation).wrappedResponseType(any());

        Boolean result = unit.execute(cancellation, credentials, nonceGenerator);

        assertThat(result).isFalse();
        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(body);
        verify(credentials).sign(url, "123", body);
        verify(connection).addRequestProperty("API-Key", "test-key");
        verify(connection).addRequestProperty("API-Sign", "test-signature");
    }

    @Test
    void should_propagate_errors_when_kraken_rejects_withdrawal() throws Exception {
        when(nonceGenerator.generate()).thenReturn("123");
        doReturn("asset=XBT&key=saved-key&amount=0.01&nonce=123").when(withdrawal).encodedParamsWith("123");
        doReturn(url).when(withdrawal).buildURL();
        when(url.openConnection()).thenReturn(connection);
        when(connection.getURL()).thenReturn(url);
        doReturn("POST").when(withdrawal).getHttpMethod();
        when(connection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("{\"error\":[\"EFunding:Max fee exceeded\"]}".getBytes(StandardCharsets.UTF_8)));
        doReturn(new WithdrawEndpoint(withdrawalParams).wrappedResponseType(TypeFactory.createDefaultInstance())).when(withdrawal).wrappedResponseType(any());

        assertThatThrownBy(() -> unit.execute(withdrawal, credentials, nonceGenerator)).isInstanceOf(KrakenException.class);
    }
}
