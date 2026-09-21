package dev.andstuff.kraken.api.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.andstuff.kraken.api.endpoint.KrakenException;
import dev.andstuff.kraken.api.endpoint.fundingbeta.ClaimFundingDepositAddressEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.CreateFundingAddressEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.DeleteFundingAddressEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingFeesEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingNetworksEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.ClaimFundingDepositAddressParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.CreateFundingAddressParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingFeesParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Scope;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingAddressCreated;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingAddressDeleted;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingFees;

@ExtendWith(MockitoExtension.class)
class DefaultKrakenRestRequesterFundingBetaTest {

    @Mock private DefaultKrakenRestRequester.ConnectionFactory connectionFactory;
    @Mock private HttpsURLConnection connection;
    @Mock private KrakenCredentials credentials;
    @Mock private KrakenNonceGenerator nonceGenerator;
    @Mock(answer = Answers.CALLS_REAL_METHODS) private KrakenRestRequester legacyRequester;

    @Test
    void should_sign_path_with_query_and_send_nonce_header_when_querying_without_body() throws Exception {
        DefaultKrakenRestRequester unit = new DefaultKrakenRestRequester(connectionFactory);
        KrakenCredentials documentedCredentials = new KrakenCredentials("test-key", "kQH5HW/8p1uGOVjbgWA7FunAmGO8lsSUXNsu3eow76sz84Q18fWxnyRzBHCd3pd5nE9qa99HAZtuZuj6F1huXg==");
        FundingFeesEndpoint endpoint = new FundingFeesEndpoint(FundingFeesParams.builder()
                .methodId("d4ec4d52-b159-428e-ba64-f45455a978a1").amount(new BigDecimal("5")).feeIncluded(true).build());
        URL url = endpoint.buildURL();
        when(nonceGenerator.generate()).thenReturn("1616492376594");
        when(connectionFactory.open(url)).thenReturn(connection);
        when(connection.getURL()).thenReturn(url);
        when(connection.getResponseCode()).thenReturn(200);
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json; charset=utf-8");
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("""
                {"fee":{"asset":{"class":"currency","name":"USDC"},"amount":"1.00000000"},"withdrawal_fee_token":"token"}
                """.getBytes(StandardCharsets.UTF_8)));

        FundingFees result = unit.execute(endpoint, documentedCredentials, nonceGenerator);

        assertThat(result.fee().amount()).isEqualTo(new BigDecimal("1.00000000"));
        assertThat(result.withdrawalFeeToken()).isEqualTo("token");
        verify(connection).setRequestMethod("GET");
        verify(connection).addRequestProperty("API-Key", "test-key");
        verify(connection).addRequestProperty("API-Nonce", "1616492376594");
        verify(connection).addRequestProperty("API-Sign", "+EH4mm7c+g2d6XOdsx+0rElDQTXFVxuzLIYLgZJwEe80Wi1TMWKDRoQA1dIXD/QFCGEwtvrp32APAoXMXT2k3w==");
        verify(connection, never()).setDoOutput(anyBoolean());
        verify(connection, never()).addRequestProperty(eq("Content-Type"), anyString());
    }

    @Test
    void should_sign_and_send_identical_json_when_querying_with_body() throws Exception {
        DefaultKrakenRestRequester unit = new DefaultKrakenRestRequester(connectionFactory);
        CreateFundingAddressEndpoint endpoint = new CreateFundingAddressEndpoint(CreateFundingAddressParams.builder()
                .scope(Scope.network("d9d375da-44b7-4be1-8a00-8b281acfe366")).address("0xBef7B36845cA31045E86D0B46DBCac4e6752").name("Personal Wallet").build());
        URL url = endpoint.buildURL();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String body = "{\"scope\":{\"network_id\":\"d9d375da-44b7-4be1-8a00-8b281acfe366\"},\"address_details\":{\"crypto\":{\"address\":\"0xBef7B36845cA31045E86D0B46DBCac4e6752\"}},\"name\":\"Personal Wallet\"}";
        when(nonceGenerator.generate()).thenReturn("123");
        when(connectionFactory.open(url)).thenReturn(connection);
        when(connection.getURL()).thenReturn(url);
        when(credentials.getKey()).thenReturn("test-key");
        when(credentials.sign("/funding/v1/addresses", "123", body)).thenReturn("test-signature");
        when(connection.getOutputStream()).thenReturn(output);
        when(connection.getResponseCode()).thenReturn(200);
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("{\"address_id\":\"AB7J4FF-BGM7G-V2JMIH\",\"verified\":true}".getBytes(StandardCharsets.UTF_8)));

        FundingAddressCreated result = unit.execute(endpoint, credentials, nonceGenerator);

        assertThat(result.addressId()).isEqualTo("AB7J4FF-BGM7G-V2JMIH");
        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(body);
        verify(connection).setRequestMethod("POST");
        verify(connection).setDoOutput(true);
        verify(connection).addRequestProperty("Content-Type", "application/json");
        verify(connection).addRequestProperty("API-Key", "test-key");
        verify(connection).addRequestProperty("API-Sign", "test-signature");
        verify(connection).addRequestProperty("API-Nonce", "123");
    }

    @Test
    void should_send_delete_without_body_when_deleting_address() throws Exception {
        DefaultKrakenRestRequester unit = new DefaultKrakenRestRequester(connectionFactory);
        DeleteFundingAddressEndpoint endpoint = new DeleteFundingAddressEndpoint("AB7J4FF-BGM7G-V2JMIH");
        URL url = endpoint.buildURL();
        when(nonceGenerator.generate()).thenReturn("123");
        when(connectionFactory.open(url)).thenReturn(connection);
        when(connection.getURL()).thenReturn(url);
        when(credentials.sign("/funding/v1/addresses/AB7J4FF-BGM7G-V2JMIH", "123", "")).thenReturn("test-signature");
        when(connection.getResponseCode()).thenReturn(200);
        when(connection.getHeaderField("Content-Type")).thenReturn("application/json");
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("{\"result\":true}".getBytes(StandardCharsets.UTF_8)));

        FundingAddressDeleted result = unit.execute(endpoint, credentials, nonceGenerator);

        assertThat(result.deleted()).isTrue();
        verify(connection).setRequestMethod("DELETE");
        verify(connection).addRequestProperty("API-Sign", "test-signature");
        verify(connection, never()).getOutputStream();
    }

    @Test
    void should_raise_status_and_body_when_kraken_answers_with_http_error() throws Exception {
        DefaultKrakenRestRequester unit = new DefaultKrakenRestRequester(connectionFactory);
        ClaimFundingDepositAddressEndpoint endpoint = new ClaimFundingDepositAddressEndpoint(ClaimFundingDepositAddressParams.builder().methodId("27ede8db-804b-4d91-8e25-46b7b9668730").build());
        when(nonceGenerator.generate()).thenReturn("123");
        when(connectionFactory.open(any(URL.class))).thenReturn(connection);
        when(connection.getURL()).thenReturn(endpoint.buildURL());
        when(connection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(connection.getResponseCode()).thenReturn(409);
        when(connection.getErrorStream()).thenReturn(new ByteArrayInputStream("{\"error\":\"TooManyDepositAddresses\"}".getBytes(StandardCharsets.UTF_8)));

        assertThatThrownBy(() -> unit.execute(endpoint, credentials, nonceGenerator))
                .isInstanceOfSatisfying(KrakenException.class, e -> assertThat(e.getErrors()).containsExactly("HTTP 409 {\"error\":\"TooManyDepositAddresses\"}"));
    }

    @Test
    void should_raise_status_only_when_http_error_has_no_body() throws Exception {
        DefaultKrakenRestRequester unit = new DefaultKrakenRestRequester(connectionFactory);
        FundingNetworksEndpoint endpoint = new FundingNetworksEndpoint();
        when(nonceGenerator.generate()).thenReturn("123");
        when(connectionFactory.open(any(URL.class))).thenReturn(connection);
        when(connection.getURL()).thenReturn(endpoint.buildURL());
        when(connection.getResponseCode()).thenReturn(401);

        assertThatThrownBy(() -> unit.execute(endpoint, credentials, nonceGenerator))
                .isInstanceOfSatisfying(KrakenException.class, e -> assertThat(e.getErrors()).containsExactly("HTTP 401"));
    }

    @Test
    void should_reject_response_when_content_type_is_not_json() throws Exception {
        DefaultKrakenRestRequester unit = new DefaultKrakenRestRequester(connectionFactory);
        FundingNetworksEndpoint endpoint = new FundingNetworksEndpoint();
        when(nonceGenerator.generate()).thenReturn("123");
        when(connectionFactory.open(any(URL.class))).thenReturn(connection);
        when(connection.getURL()).thenReturn(endpoint.buildURL());
        when(connection.getResponseCode()).thenReturn(200);
        when(connection.getHeaderField("Content-Type")).thenReturn("text/html");

        assertThatThrownBy(() -> unit.execute(endpoint, credentials, nonceGenerator)).isInstanceOf(IllegalStateException.class).hasMessage("Unsupported HTTP Content-Type");
    }

    @Test
    void should_reject_funding_endpoint_when_requester_predates_funding_beta() {
        FundingNetworksEndpoint endpoint = new FundingNetworksEndpoint();

        assertThatThrownBy(() -> legacyRequester.execute(endpoint, credentials, nonceGenerator)).isInstanceOf(UnsupportedOperationException.class).hasMessageContaining("Funding (Beta)");
    }
}
