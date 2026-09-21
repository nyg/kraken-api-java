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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.andstuff.kraken.api.endpoint.subaccount.AccountTransferEndpoint;
import dev.andstuff.kraken.api.endpoint.subaccount.CreateSubaccountEndpoint;
import dev.andstuff.kraken.api.endpoint.subaccount.params.AccountTransferParams;
import dev.andstuff.kraken.api.endpoint.subaccount.params.CreateSubaccountParams;
import dev.andstuff.kraken.api.endpoint.subaccount.response.AccountTransfer;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import dev.andstuff.kraken.api.rest.KrakenNonceGenerator;
import dev.andstuff.kraken.api.rest.KrakenRestRequester;

@ExtendWith(MockitoExtension.class)
class KrakenAPISubaccountTest {

    @Mock private KrakenCredentials credentials;
    @Mock private KrakenNonceGenerator nonceGenerator;
    @Mock private KrakenRestRequester requester;

    @Test
    void should_route_createSubaccount_options_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        CreateSubaccountParams params = CreateSubaccountParams.of("abc123", "abc123@example.com");
        when(requester.execute(any(CreateSubaccountEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(true);

        boolean result = unit.createSubaccount(params);

        assertThat(result).isTrue();
        verify(requester).execute(argThat((CreateSubaccountEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_createSubaccount_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        CreateSubaccountParams params = CreateSubaccountParams.of("abc123", "abc123@example.com");

        assertThatThrownBy(() -> unit.createSubaccount(params)).isInstanceOf(IllegalStateException.class).hasMessageContaining("CreateSubaccount");
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_accountTransfer_options_when_called() {
        AccountTransfer accountTransferResponse = new AccountTransfer("TOH3AS2-LPCWR8-JDQGEU", AccountTransfer.Status.PENDING);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        AccountTransferParams params = AccountTransferParams.builder().asset("XBT").amount(BigDecimal.ONE).from("ABCD 1234 EFGH 5678").to("IJKL 0987 MNOP 6543").build();
        when(requester.execute(any(AccountTransferEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(accountTransferResponse);

        AccountTransfer result = unit.accountTransfer(params);

        assertThat(result).isSameAs(accountTransferResponse);
        verify(requester).execute(argThat((AccountTransferEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_accountTransfer_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        AccountTransferParams params = AccountTransferParams.builder().asset("XBT").amount(BigDecimal.ONE).from("ABCD 1234 EFGH 5678").to("IJKL 0987 MNOP 6543").build();

        assertThatThrownBy(() -> unit.accountTransfer(params)).isInstanceOf(IllegalStateException.class).hasMessageContaining("AccountTransfer");
        verifyNoInteractions(requester);
    }
}
