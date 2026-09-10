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
import java.util.Map;
import java.util.Optional;
import javax.net.ssl.HttpsURLConnection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.andstuff.kraken.api.endpoint.KrakenException;
import dev.andstuff.kraken.api.endpoint.account.AccountBalanceEndpoint;
import dev.andstuff.kraken.api.endpoint.account.CreditLinesEndpoint;
import dev.andstuff.kraken.api.endpoint.account.TradeVolumeEndpoint;
import dev.andstuff.kraken.api.endpoint.account.params.AccountBalanceParams;
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
        String body = "{\"pair\":[{\"asset\":\"TSLAx/USD\",\"aclass\":\"equity_pair\"}],\"nonce\":123}";
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

    @Test
    void should_sign_body_and_path_when_balance_selects_a_wallet() throws Exception {
        DefaultKrakenRestRequester unit = new DefaultKrakenRestRequester(connectionFactory);
        AccountBalanceEndpoint endpoint = new AccountBalanceEndpoint(AccountBalanceParams.builder().accountId("wallet +/&=").build());
        KrakenCredentials signingCredentials = new KrakenCredentials("public-example", "kQH5HW/8p1uGOVjbgWA7FunAmGO8lsSUXNsu3eow76sz84Q18fWxnyRzBHCd3pd5nE9qa99HAZtuZuj6F1huXg==");
        URL url = endpoint.buildURL();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        when(nonceGenerator.generate()).thenReturn("123");
        when(connectionFactory.open(any(URL.class))).thenReturn(connection);
        when(connection.getURL()).thenReturn(url);
        when(connection.getOutputStream()).thenReturn(output);
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("{\"error\":[],\"result\":{\"ZUSD\":\"1.25\"}}".getBytes(StandardCharsets.UTF_8)));

        Map<String, BigDecimal> result = unit.execute(endpoint, signingCredentials, nonceGenerator);

        assertThat(result.get("ZUSD")).isEqualByComparingTo("1.25");
        assertThat(url.getQuery()).isEqualTo("account_id=wallet+%2B%2F%26%3D");
        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo("nonce=123");
        verify(connection).addRequestProperty("API-Sign", "WRKuiZ89EQ2MHYfSRx7pHztnIMr+ivo8S8E5olq+GcpBVw97M1jKx1ElzpGgC36H/tn8Po28EwNryLvK+rl4ig==");
    }
}
