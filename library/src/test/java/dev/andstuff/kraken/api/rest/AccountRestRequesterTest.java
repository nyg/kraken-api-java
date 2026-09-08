package dev.andstuff.kraken.api.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.type.TypeFactory;

import dev.andstuff.kraken.api.endpoint.KrakenException;
import dev.andstuff.kraken.api.endpoint.account.CreditLinesEndpoint;
import dev.andstuff.kraken.api.endpoint.account.TradeVolumeEndpoint;
import dev.andstuff.kraken.api.endpoint.account.response.CreditLines;
import dev.andstuff.kraken.api.endpoint.account.response.TradeVolume;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountRestRequesterTest {

    @InjectMocks private DefaultKrakenRestRequester unit;
    @Mock private TradeVolumeEndpoint tradeVolume;
    @Mock private CreditLinesEndpoint creditLines;
    @Mock private HttpsURLConnection connection;
    @Mock private URL url;
    @Mock private KrakenCredentials credentials;
    @Mock private KrakenNonceGenerator nonceGenerator;

    @Test
    void should_sign_and_send_identical_json_when_requesting_trade_volume() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String body = "{\"nonce\":123,\"pair\":[{\"asset\":\"TSLAx/USD\",\"aclass\":\"equity_pair\"}]}";
        when(nonceGenerator.generate()).thenReturn("123");
        when(tradeVolume.encodedParamsWith("123")).thenReturn(body);
        when(tradeVolume.buildURL()).thenReturn(url);
        when(url.openConnection()).thenReturn(connection);
        when(connection.getURL()).thenReturn(url);
        when(tradeVolume.getHttpMethod()).thenReturn("POST");
        when(tradeVolume.getContentType()).thenReturn("application/json");
        when(credentials.getKey()).thenReturn("test-key");
        when(credentials.sign(url, "123", body)).thenReturn("test-signature");
        when(connection.getOutputStream()).thenReturn(output);
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("{\"error\":[],\"result\":{\"volume\":\"0.0000000000123456789\"}}".getBytes(StandardCharsets.UTF_8)));
        when(tradeVolume.wrappedResponseType(any())).thenReturn(new TradeVolumeEndpoint().wrappedResponseType(TypeFactory.createDefaultInstance()));
        when(tradeVolume.unwrapResponse(any())).thenCallRealMethod();

        TradeVolume result = unit.execute(tradeVolume, credentials, nonceGenerator);

        assertThat(result.volume()).isEqualByComparingTo("0.0000000000123456789");
        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(body);
        verify(connection).setRequestMethod("POST");
        verify(connection).addRequestProperty("Content-Type", "application/json");
        verify(connection).addRequestProperty("API-Key", "test-key");
        verify(connection).addRequestProperty("API-Sign", "test-signature");
        verify(credentials).sign(url, "123", body);
    }

    @Test
    void should_return_null_when_credit_lines_response_has_no_credit() throws Exception {
        when(nonceGenerator.generate()).thenReturn("123");
        when(creditLines.encodedParamsWith("123")).thenReturn("nonce=123");
        when(creditLines.buildURL()).thenReturn(url);
        when(url.openConnection()).thenReturn(connection);
        when(connection.getURL()).thenReturn(url);
        when(creditLines.getHttpMethod()).thenReturn("POST");
        when(creditLines.getContentType()).thenCallRealMethod();
        when(connection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("{\"error\":[],\"result\":null}".getBytes(StandardCharsets.UTF_8)));
        when(creditLines.wrappedResponseType(any())).thenReturn(new CreditLinesEndpoint().wrappedResponseType(TypeFactory.createDefaultInstance()));
        when(creditLines.unwrapResponse(any())).thenCallRealMethod();

        CreditLines result = unit.execute(creditLines, credentials, nonceGenerator);

        assertThat(result).isNull();
        verify(connection).addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    }

    @Test
    void should_propagate_errors_when_kraken_rejects_trade_volume() throws Exception {
        when(nonceGenerator.generate()).thenReturn("123");
        when(tradeVolume.encodedParamsWith("123")).thenReturn("{\"nonce\":123}");
        when(tradeVolume.buildURL()).thenReturn(url);
        when(url.openConnection()).thenReturn(connection);
        when(connection.getURL()).thenReturn(url);
        when(tradeVolume.getHttpMethod()).thenReturn("POST");
        when(tradeVolume.getContentType()).thenReturn("application/json");
        when(connection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("{\"error\":[\"EGeneral:Permission denied\"]}".getBytes(StandardCharsets.UTF_8)));
        when(tradeVolume.wrappedResponseType(any())).thenReturn(new TradeVolumeEndpoint().wrappedResponseType(TypeFactory.createDefaultInstance()));
        when(tradeVolume.unwrapResponse(any())).thenCallRealMethod();

        assertThatThrownBy(() -> unit.execute(tradeVolume, credentials, nonceGenerator)).isInstanceOf(KrakenException.class);
    }
}
