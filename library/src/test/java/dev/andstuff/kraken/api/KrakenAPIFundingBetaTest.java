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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import dev.andstuff.kraken.api.endpoint.fundingbeta.ClaimFundingDepositAddressEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.CreateFundingAddressEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.CreateFundingWithdrawalEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.DeleteFundingAddressEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingAddressesEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingAssetsEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingBetaEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingDepositAddressesEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingDepositLimitsEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingDepositsEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingFeesEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingMethodsEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingNetworksEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingWithdrawalLimitsEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingWithdrawalsEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.UpdateFundingAddressEndpoint;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Asset;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.AssetAmount;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.AssetClass;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.ClaimFundingDepositAddressParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.CreateFundingAddressParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.CreateFundingWithdrawalParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.DeleteFundingAddressParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Direction;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingAddressesParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingAssetsParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingDepositAddressesParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingDepositsParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingFeesParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingLimitsParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingMethodsParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingNetworksParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingWithdrawalStatus;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingWithdrawalsParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Scope;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.UpdateFundingAddressParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.ClaimedFundingDepositAddress;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingAddressCreated;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingAddressDeleted;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingAddressUpdated;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingAddresses;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingAssets;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingDepositAddresses;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingDepositLimits;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingDeposits;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingFees;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingMethods;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingNetworks;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingWithdrawalCreated;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingWithdrawalLimits;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingWithdrawals;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import dev.andstuff.kraken.api.rest.KrakenNonceGenerator;
import dev.andstuff.kraken.api.rest.KrakenRestRequester;

@ExtendWith(MockitoExtension.class)
class KrakenAPIFundingBetaTest {

    @Mock private KrakenCredentials credentials;
    @Mock private KrakenNonceGenerator nonceGenerator;
    @Mock private KrakenRestRequester requester;

    @Test
    void should_route_fundingFees_options_when_called() {
        FundingFees response = new FundingFees(null, null, null, null, "token");
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        FundingFeesParams params = FundingFeesParams.builder().methodId("d4ec4d52-b159-428e-ba64-f45455a978a1").amount(new BigDecimal("5")).build();
        when(requester.execute(any(FundingFeesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingFees result = unit.fundingFees(params);

        assertThat(result).isSameAs(response);
        verify(requester).execute(argThat((FundingFeesEndpoint endpoint) -> endpoint.getParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_fundingFees_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        FundingFeesParams params = FundingFeesParams.builder().methodId("d4ec4d52-b159-428e-ba64-f45455a978a1").amount(new BigDecimal("5")).build();

        assertThatThrownBy(() -> unit.fundingFees(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_fundingMethods_options_when_called() {
        FundingMethods response = new FundingMethods(List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        FundingMethodsParams params = FundingMethodsParams.builder().direction(Direction.WITHDRAW).limit(10).build();
        when(requester.execute(any(FundingMethodsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingMethods result = unit.fundingMethods(params);

        assertThat(result).isSameAs(response);
        verify(requester).execute(argThat((FundingMethodsEndpoint endpoint) -> endpoint.getParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_fundingMethods_direction_when_called() {
        FundingMethods response = new FundingMethods(List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(FundingMethodsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingMethods result = unit.fundingMethods(Direction.DEPOSIT);

        assertThat(result).isSameAs(response);
        verify(requester).execute(argThat((FundingMethodsEndpoint endpoint) -> ((FundingMethodsParams) endpoint.getParams()).getDirection() == Direction.DEPOSIT), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_fundingMethods_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        FundingMethodsParams params = FundingMethodsParams.builder().direction(Direction.WITHDRAW).limit(10).build();

        assertThatThrownBy(() -> unit.fundingMethods(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_fundingAssets_options_when_called() {
        FundingAssets response = new FundingAssets(List.of(), List.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        FundingAssetsParams params = FundingAssetsParams.builder().direction(Direction.DEPOSIT).assetClass(AssetClass.CURRENCY).build();
        when(requester.execute(any(FundingAssetsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingAssets result = unit.fundingAssets(params);

        assertThat(result).isSameAs(response);
        verify(requester).execute(argThat((FundingAssetsEndpoint endpoint) -> endpoint.getParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_fundingAssets_direction_when_called() {
        FundingAssets response = new FundingAssets(List.of(), List.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(FundingAssetsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingAssets result = unit.fundingAssets(Direction.WITHDRAW);

        assertThat(result).isSameAs(response);
        verify(requester).execute(argThat((FundingAssetsEndpoint endpoint) -> ((FundingAssetsParams) endpoint.getParams()).getDirection() == Direction.WITHDRAW), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_fundingAssets_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        FundingAssetsParams params = FundingAssetsParams.builder().direction(Direction.DEPOSIT).assetClass(AssetClass.CURRENCY).build();

        assertThatThrownBy(() -> unit.fundingAssets(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_fundingNetworks_options_when_called() {
        FundingNetworks response = new FundingNetworks(List.of(), List.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        FundingNetworksParams params = FundingNetworksParams.builder().accountId("AA12").build();
        when(requester.execute(any(FundingNetworksEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingNetworks result = unit.fundingNetworks(params);

        assertThat(result).isSameAs(response);
        verify(requester).execute(argThat((FundingNetworksEndpoint endpoint) -> endpoint.getParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_fundingNetworks_defaults_when_called() {
        FundingNetworks response = new FundingNetworks(List.of(), List.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(FundingNetworksEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingNetworks result = unit.fundingNetworks();

        assertThat(result).isSameAs(response);
        verify(requester).execute(any(FundingNetworksEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_fundingNetworks_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        FundingNetworksParams params = FundingNetworksParams.builder().accountId("AA12").build();

        assertThatThrownBy(() -> unit.fundingNetworks(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_fundingDepositLimits_options_when_called() {
        FundingDepositLimits response = new FundingDepositLimits(List.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        FundingLimitsParams params = FundingLimitsParams.builder().assetClass(AssetClass.CURRENCY).asset("BTC").build();
        when(requester.execute(any(FundingDepositLimitsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingDepositLimits result = unit.fundingDepositLimits(params);

        assertThat(result).isSameAs(response);
        verify(requester).execute(argThat((FundingDepositLimitsEndpoint endpoint) -> endpoint.getParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_fundingDepositLimits_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        FundingLimitsParams params = FundingLimitsParams.builder().assetClass(AssetClass.CURRENCY).asset("BTC").build();

        assertThatThrownBy(() -> unit.fundingDepositLimits(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_fundingWithdrawalLimits_options_when_called() {
        FundingWithdrawalLimits response = new FundingWithdrawalLimits(null, List.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        FundingLimitsParams params = FundingLimitsParams.builder().assetClass(AssetClass.CURRENCY).asset("BTC").build();
        when(requester.execute(any(FundingWithdrawalLimitsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingWithdrawalLimits result = unit.fundingWithdrawalLimits(params);

        assertThat(result).isSameAs(response);
        verify(requester).execute(argThat((FundingWithdrawalLimitsEndpoint endpoint) -> endpoint.getParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_fundingWithdrawalLimits_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        FundingLimitsParams params = FundingLimitsParams.builder().assetClass(AssetClass.CURRENCY).asset("BTC").build();

        assertThatThrownBy(() -> unit.fundingWithdrawalLimits(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_claimFundingDepositAddress_options_when_called() {
        ClaimedFundingDepositAddress response = new ClaimedFundingDepositAddress(null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        ClaimFundingDepositAddressParams params = ClaimFundingDepositAddressParams.builder().methodId("27ede8db-804b-4d91-8e25-46b7b9668730").build();
        when(requester.execute(any(ClaimFundingDepositAddressEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        ClaimedFundingDepositAddress result = unit.claimFundingDepositAddress(params);

        assertThat(result).isSameAs(response);
        verify(requester).execute(argThat((ClaimFundingDepositAddressEndpoint endpoint) -> endpoint.getParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_claimFundingDepositAddress_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        ClaimFundingDepositAddressParams params = ClaimFundingDepositAddressParams.builder().methodId("27ede8db-804b-4d91-8e25-46b7b9668730").build();

        assertThatThrownBy(() -> unit.claimFundingDepositAddress(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_fundingDepositAddresses_options_when_called() {
        FundingDepositAddresses response = new FundingDepositAddresses(List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        FundingDepositAddressesParams params = FundingDepositAddressesParams.builder().scope(Scope.method("m")).build();
        when(requester.execute(any(FundingDepositAddressesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingDepositAddresses result = unit.fundingDepositAddresses(params);

        assertThat(result).isSameAs(response);
        verify(requester).execute(argThat((FundingDepositAddressesEndpoint endpoint) -> endpoint.getParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_fundingDepositAddresses_defaults_when_called() {
        FundingDepositAddresses response = new FundingDepositAddresses(List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(FundingDepositAddressesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingDepositAddresses result = unit.fundingDepositAddresses();

        assertThat(result).isSameAs(response);
        verify(requester).execute(any(FundingDepositAddressesEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_fundingDepositAddresses_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        FundingDepositAddressesParams params = FundingDepositAddressesParams.builder().scope(Scope.method("m")).build();

        assertThatThrownBy(() -> unit.fundingDepositAddresses(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_fundingDeposits_options_when_called() {
        FundingDeposits response = new FundingDeposits(List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        FundingDepositsParams params = FundingDepositsParams.builder().limit(20).build();
        when(requester.execute(any(FundingDepositsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingDeposits result = unit.fundingDeposits(params);

        assertThat(result).isSameAs(response);
        verify(requester).execute(argThat((FundingDepositsEndpoint endpoint) -> endpoint.getParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_fundingDeposits_defaults_when_called() {
        FundingDeposits response = new FundingDeposits(List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(FundingDepositsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingDeposits result = unit.fundingDeposits();

        assertThat(result).isSameAs(response);
        verify(requester).execute(any(FundingDepositsEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_fundingDeposits_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        FundingDepositsParams params = FundingDepositsParams.builder().limit(20).build();

        assertThatThrownBy(() -> unit.fundingDeposits(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_fundingAddresses_options_when_called() {
        FundingAddresses response = new FundingAddresses(List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        FundingAddressesParams params = FundingAddressesParams.builder().limit(20).build();
        when(requester.execute(any(FundingAddressesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingAddresses result = unit.fundingAddresses(params);

        assertThat(result).isSameAs(response);
        verify(requester).execute(argThat((FundingAddressesEndpoint endpoint) -> endpoint.getParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_fundingAddresses_defaults_when_called() {
        FundingAddresses response = new FundingAddresses(List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(FundingAddressesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingAddresses result = unit.fundingAddresses();

        assertThat(result).isSameAs(response);
        verify(requester).execute(any(FundingAddressesEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_fundingAddresses_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        FundingAddressesParams params = FundingAddressesParams.builder().limit(20).build();

        assertThatThrownBy(() -> unit.fundingAddresses(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_createFundingAddress_options_when_called() {
        FundingAddressCreated response = new FundingAddressCreated("AB7J4FF-BGM7G-V2JMIH", true);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        CreateFundingAddressParams params = CreateFundingAddressParams.builder().scope(Scope.network("n")).address("0xBef7").name("Wallet").build();
        when(requester.execute(any(CreateFundingAddressEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingAddressCreated result = unit.createFundingAddress(params);

        assertThat(result).isSameAs(response);
        verify(requester).execute(argThat((CreateFundingAddressEndpoint endpoint) -> endpoint.getParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_createFundingAddress_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        CreateFundingAddressParams params = CreateFundingAddressParams.builder().scope(Scope.network("n")).address("0xBef7").name("Wallet").build();

        assertThatThrownBy(() -> unit.createFundingAddress(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_updateFundingAddress_options_when_called() {
        FundingAddressUpdated response = new FundingAddressUpdated(true);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        UpdateFundingAddressParams params = UpdateFundingAddressParams.builder().addressId("AB7J4FF-BGM7G-V2JMIH").name("Wallet").build();
        when(requester.execute(any(UpdateFundingAddressEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingAddressUpdated result = unit.updateFundingAddress(params);

        assertThat(result).isSameAs(response);
        verify(requester).execute(argThat((UpdateFundingAddressEndpoint endpoint) -> endpoint.getParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_updateFundingAddress_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        UpdateFundingAddressParams params = UpdateFundingAddressParams.builder().addressId("AB7J4FF-BGM7G-V2JMIH").name("Wallet").build();

        assertThatThrownBy(() -> unit.updateFundingAddress(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_fundingWithdrawals_options_when_called() {
        FundingWithdrawals response = new FundingWithdrawals(List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        FundingWithdrawalsParams params = FundingWithdrawalsParams.builder().status(FundingWithdrawalStatus.PENDING).build();
        when(requester.execute(any(FundingWithdrawalsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingWithdrawals result = unit.fundingWithdrawals(params);

        assertThat(result).isSameAs(response);
        verify(requester).execute(argThat((FundingWithdrawalsEndpoint endpoint) -> endpoint.getParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_fundingWithdrawals_defaults_when_called() {
        FundingWithdrawals response = new FundingWithdrawals(List.of(), null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(FundingWithdrawalsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingWithdrawals result = unit.fundingWithdrawals();

        assertThat(result).isSameAs(response);
        verify(requester).execute(any(FundingWithdrawalsEndpoint.class), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_fundingWithdrawals_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        FundingWithdrawalsParams params = FundingWithdrawalsParams.builder().status(FundingWithdrawalStatus.PENDING).build();

        assertThatThrownBy(() -> unit.fundingWithdrawals(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_createFundingWithdrawal_options_when_called() {
        FundingWithdrawalCreated response = new FundingWithdrawalCreated("FTVZiTI-e02T84mm87JmibnObWNdnW", null, null, null, null);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        CreateFundingWithdrawalParams params = CreateFundingWithdrawalParams.builder().scope(Scope.method("m")).addressId("AB1").amount(new AssetAmount(new Asset(AssetClass.CURRENCY, "USDC"), new BigDecimal("5"))).build();
        when(requester.execute(any(CreateFundingWithdrawalEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(response);

        FundingWithdrawalCreated result = unit.createFundingWithdrawal(params);

        assertThat(result).isSameAs(response);
        verify(requester).execute(argThat((CreateFundingWithdrawalEndpoint endpoint) -> endpoint.getParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_createFundingWithdrawal_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        CreateFundingWithdrawalParams params = CreateFundingWithdrawalParams.builder().scope(Scope.method("m")).addressId("AB1").amount(new AssetAmount(new Asset(AssetClass.CURRENCY, "USDC"), new BigDecimal("5"))).build();

        assertThatThrownBy(() -> unit.createFundingWithdrawal(params)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_unwrap_deletion_result_when_deleting_address_by_identifier() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(DeleteFundingAddressEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(new FundingAddressDeleted(true));

        boolean result = unit.deleteFundingAddress("AB7J4FF-BGM7G-V2JMIH");

        assertThat(result).isTrue();
        verify(requester).execute(argThat((DeleteFundingAddressEndpoint endpoint) -> endpoint.getPath().equals("v1/addresses/AB7J4FF-BGM7G-V2JMIH")), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_unwrap_deletion_result_when_deleting_address_with_options() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        DeleteFundingAddressParams params = DeleteFundingAddressParams.builder().addressId("AB1").accountId("AA12").build();
        when(requester.execute(any(DeleteFundingAddressEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(new FundingAddressDeleted(false));

        boolean result = unit.deleteFundingAddress(params);

        assertThat(result).isFalse();
        verify(requester).execute(argThat((DeleteFundingAddressEndpoint endpoint) -> endpoint.getParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_deleteFundingAddress_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(() -> unit.deleteFundingAddress("AB1")).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_custom_funding_endpoint_when_queried() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        FundingBetaEndpoint<JsonNode> endpoint = new FundingBetaEndpoint<>("GET", "v1/networks", new TypeReference<>() {});
        JsonNode response = JsonNodeFactory.instance.objectNode();
        when(requester.execute(same(endpoint), same(credentials), same(nonceGenerator))).thenReturn(response);

        JsonNode result = unit.query(endpoint);

        assertThat(result).isSameAs(response);
    }

    @Test
    void should_name_path_when_custom_funding_endpoint_lacks_credentials() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        FundingBetaEndpoint<JsonNode> endpoint = new FundingBetaEndpoint<>("GET", "v1/networks", new TypeReference<>() {});

        assertThatThrownBy(() -> unit.query(endpoint)).isInstanceOf(IllegalStateException.class).hasMessageContaining("v1/networks");
        verifyNoInteractions(requester);
    }
}
