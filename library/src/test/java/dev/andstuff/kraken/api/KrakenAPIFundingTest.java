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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.andstuff.kraken.api.endpoint.funding.CancelWithdrawalEndpoint;
import dev.andstuff.kraken.api.endpoint.funding.DepositAddressesEndpoint;
import dev.andstuff.kraken.api.endpoint.funding.DepositMethodsEndpoint;
import dev.andstuff.kraken.api.endpoint.funding.DepositStatusEndpoint;
import dev.andstuff.kraken.api.endpoint.funding.WalletTransferEndpoint;
import dev.andstuff.kraken.api.endpoint.funding.WithdrawEndpoint;
import dev.andstuff.kraken.api.endpoint.funding.WithdrawalAddressesEndpoint;
import dev.andstuff.kraken.api.endpoint.funding.WithdrawalInfoEndpoint;
import dev.andstuff.kraken.api.endpoint.funding.WithdrawalMethodsEndpoint;
import dev.andstuff.kraken.api.endpoint.funding.WithdrawalStatusEndpoint;
import dev.andstuff.kraken.api.endpoint.funding.params.CancelWithdrawalParams;
import dev.andstuff.kraken.api.endpoint.funding.params.DepositAddressesParams;
import dev.andstuff.kraken.api.endpoint.funding.params.DepositMethodsParams;
import dev.andstuff.kraken.api.endpoint.funding.params.DepositStatusParams;
import dev.andstuff.kraken.api.endpoint.funding.params.DestinationWallet;
import dev.andstuff.kraken.api.endpoint.funding.params.SourceWallet;
import dev.andstuff.kraken.api.endpoint.funding.params.WalletTransferParams;
import dev.andstuff.kraken.api.endpoint.funding.params.WithdrawParams;
import dev.andstuff.kraken.api.endpoint.funding.params.WithdrawalAddressesParams;
import dev.andstuff.kraken.api.endpoint.funding.params.WithdrawalInfoParams;
import dev.andstuff.kraken.api.endpoint.funding.params.WithdrawalMethodsParams;
import dev.andstuff.kraken.api.endpoint.funding.params.WithdrawalStatusParams;
import dev.andstuff.kraken.api.endpoint.funding.response.DepositAddress;
import dev.andstuff.kraken.api.endpoint.funding.response.DepositMethod;
import dev.andstuff.kraken.api.endpoint.funding.response.DepositStatus;
import dev.andstuff.kraken.api.endpoint.funding.response.FundingReference;
import dev.andstuff.kraken.api.endpoint.funding.response.WithdrawalAddress;
import dev.andstuff.kraken.api.endpoint.funding.response.WithdrawalInfo;
import dev.andstuff.kraken.api.endpoint.funding.response.WithdrawalMethod;
import dev.andstuff.kraken.api.endpoint.funding.response.WithdrawalStatus;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import dev.andstuff.kraken.api.rest.KrakenNonceGenerator;
import dev.andstuff.kraken.api.rest.KrakenRestRequester;

@ExtendWith(MockitoExtension.class)
class KrakenAPIFundingTest {

    @Mock private KrakenCredentials credentials;
    @Mock private KrakenNonceGenerator nonceGenerator;
    @Mock private KrakenRestRequester requester;
    @Mock private List<DepositMethod> depositMethodsResponse;
    @Mock private List<DepositAddress> depositAddressesResponse;
    @Mock private List<WithdrawalMethod> withdrawalMethodsResponse;
    @Mock private List<WithdrawalAddress> withdrawalAddressesResponse;

    @Test
    void should_route_depositMethods_options_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        DepositMethodsParams params = DepositMethodsParams.builder().asset("id +/&=").build();
        when(requester.execute(any(DepositMethodsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(depositMethodsResponse);

        List<DepositMethod> result = unit.depositMethods(params);

        assertThat(result).isSameAs(depositMethodsResponse);
        verify(requester).execute(argThat((DepositMethodsEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_depositMethods_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        DepositMethodsParams params = DepositMethodsParams.builder().asset("id +/&=").build();

        assertThatThrownBy(() -> unit.depositMethods(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_depositAddresses_options_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        DepositAddressesParams params = DepositAddressesParams.builder().asset("id +/&=").method("id +/&=").build();
        when(requester.execute(any(DepositAddressesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(depositAddressesResponse);

        List<DepositAddress> result = unit.depositAddresses(params);

        assertThat(result).isSameAs(depositAddressesResponse);
        verify(requester).execute(argThat((DepositAddressesEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_depositAddresses_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        DepositAddressesParams params = DepositAddressesParams.builder().asset("id +/&=").method("id +/&=").build();

        assertThatThrownBy(() -> unit.depositAddresses(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_depositStatus_options_when_called() {
        DepositStatus depositStatusResponse = new DepositStatus(List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        DepositStatusParams params = DepositStatusParams.builder().build();
        when(requester.execute(any(DepositStatusEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(depositStatusResponse);

        DepositStatus result = unit.depositStatus(params);

        assertThat(result).isSameAs(depositStatusResponse);
        verify(requester).execute(argThat((DepositStatusEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_depositStatus_defaults_when_called() {
        DepositStatus depositStatusResponse = new DepositStatus(List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(DepositStatusEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(depositStatusResponse);

        DepositStatus result = unit.depositStatus();

        assertThat(result).isSameAs(depositStatusResponse);
        verify(requester).execute(any(DepositStatusEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_depositStatus_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(unit::depositStatus).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_withdrawalMethods_options_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        WithdrawalMethodsParams params = WithdrawalMethodsParams.builder().build();
        when(requester.execute(any(WithdrawalMethodsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(withdrawalMethodsResponse);

        List<WithdrawalMethod> result = unit.withdrawalMethods(params);

        assertThat(result).isSameAs(withdrawalMethodsResponse);
        verify(requester).execute(argThat((WithdrawalMethodsEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_withdrawalMethods_defaults_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(WithdrawalMethodsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(withdrawalMethodsResponse);

        List<WithdrawalMethod> result = unit.withdrawalMethods();

        assertThat(result).isSameAs(withdrawalMethodsResponse);
        verify(requester).execute(any(WithdrawalMethodsEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_withdrawalMethods_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(unit::withdrawalMethods).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_withdrawalAddresses_options_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        WithdrawalAddressesParams params = WithdrawalAddressesParams.builder().build();
        when(requester.execute(any(WithdrawalAddressesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(withdrawalAddressesResponse);

        List<WithdrawalAddress> result = unit.withdrawalAddresses(params);

        assertThat(result).isSameAs(withdrawalAddressesResponse);
        verify(requester).execute(argThat((WithdrawalAddressesEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_withdrawalAddresses_defaults_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(WithdrawalAddressesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(withdrawalAddressesResponse);

        List<WithdrawalAddress> result = unit.withdrawalAddresses();

        assertThat(result).isSameAs(withdrawalAddressesResponse);
        verify(requester).execute(any(WithdrawalAddressesEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_withdrawalAddresses_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(unit::withdrawalAddresses).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_withdrawalInfo_options_when_called() {
        WithdrawalInfo withdrawalInfoResponse = new WithdrawalInfo(null, null, null, null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        WithdrawalInfoParams params = WithdrawalInfoParams.builder().asset("id +/&=").key("id +/&=").amount(new BigDecimal("0.0000000012300")).build();
        when(requester.execute(any(WithdrawalInfoEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(withdrawalInfoResponse);

        WithdrawalInfo result = unit.withdrawalInfo(params);

        assertThat(result).isSameAs(withdrawalInfoResponse);
        verify(requester).execute(argThat((WithdrawalInfoEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_withdrawalInfo_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        WithdrawalInfoParams params = WithdrawalInfoParams.builder().asset("id +/&=").key("id +/&=").amount(new BigDecimal("0.0000000012300")).build();

        assertThatThrownBy(() -> unit.withdrawalInfo(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_withdraw_options_when_called() {
        FundingReference withdrawResponse = new FundingReference(null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        WithdrawParams params = WithdrawParams.builder().asset("id +/&=").key("id +/&=").amount(new BigDecimal("0.0000000012300")).build();
        when(requester.execute(any(WithdrawEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(withdrawResponse);

        FundingReference result = unit.withdraw(params);

        assertThat(result).isSameAs(withdrawResponse);
        verify(requester).execute(argThat((WithdrawEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_withdraw_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        WithdrawParams params = WithdrawParams.builder().asset("id +/&=").key("id +/&=").amount(new BigDecimal("0.0000000012300")).build();

        assertThatThrownBy(() -> unit.withdraw(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_withdrawalStatus_options_when_called() {
        WithdrawalStatus withdrawalStatusResponse = new WithdrawalStatus(List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        WithdrawalStatusParams params = WithdrawalStatusParams.builder().build();
        when(requester.execute(any(WithdrawalStatusEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(withdrawalStatusResponse);

        WithdrawalStatus result = unit.withdrawalStatus(params);

        assertThat(result).isSameAs(withdrawalStatusResponse);
        verify(requester).execute(argThat((WithdrawalStatusEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_withdrawalStatus_defaults_when_called() {
        WithdrawalStatus withdrawalStatusResponse = new WithdrawalStatus(List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(WithdrawalStatusEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(withdrawalStatusResponse);

        WithdrawalStatus result = unit.withdrawalStatus();

        assertThat(result).isSameAs(withdrawalStatusResponse);
        verify(requester).execute(any(WithdrawalStatusEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_withdrawalStatus_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(unit::withdrawalStatus).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_cancelWithdrawal_options_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        CancelWithdrawalParams params = CancelWithdrawalParams.builder().asset("id +/&=").referenceId("id +/&=").build();
        when(requester.execute(any(CancelWithdrawalEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(Boolean.FALSE);

        Boolean result = unit.cancelWithdrawal(params);

        assertThat(result).isSameAs(Boolean.FALSE);
        verify(requester).execute(argThat((CancelWithdrawalEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_cancelWithdrawal_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        CancelWithdrawalParams params = CancelWithdrawalParams.builder().asset("id +/&=").referenceId("id +/&=").build();

        assertThatThrownBy(() -> unit.cancelWithdrawal(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_walletTransfer_options_when_called() {
        FundingReference walletTransferResponse = new FundingReference(null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        WalletTransferParams params = WalletTransferParams.builder().asset("id +/&=").sourceWallet(SourceWallet.SPOT_WALLET).destinationWallet(DestinationWallet.FUTURES_WALLET).amount(new BigDecimal("0.0000000012300")).build();
        when(requester.execute(any(WalletTransferEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(walletTransferResponse);

        FundingReference result = unit.walletTransfer(params);

        assertThat(result).isSameAs(walletTransferResponse);
        verify(requester).execute(argThat((WalletTransferEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_walletTransfer_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        WalletTransferParams params = WalletTransferParams.builder().asset("id +/&=").sourceWallet(SourceWallet.SPOT_WALLET).destinationWallet(DestinationWallet.FUTURES_WALLET).amount(new BigDecimal("0.0000000012300")).build();

        assertThatThrownBy(() -> unit.walletTransfer(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

}
