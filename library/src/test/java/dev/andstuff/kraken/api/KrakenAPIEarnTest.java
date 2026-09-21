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

import dev.andstuff.kraken.api.endpoint.earn.EarnAllocateEndpoint;
import dev.andstuff.kraken.api.endpoint.earn.EarnAllocateStatusEndpoint;
import dev.andstuff.kraken.api.endpoint.earn.EarnAllocationsEndpoint;
import dev.andstuff.kraken.api.endpoint.earn.EarnDeallocateEndpoint;
import dev.andstuff.kraken.api.endpoint.earn.EarnDeallocateStatusEndpoint;
import dev.andstuff.kraken.api.endpoint.earn.EarnStrategiesEndpoint;
import dev.andstuff.kraken.api.endpoint.earn.params.EarnAllocationParams;
import dev.andstuff.kraken.api.endpoint.earn.params.EarnAllocationsParams;
import dev.andstuff.kraken.api.endpoint.earn.params.EarnStatusParams;
import dev.andstuff.kraken.api.endpoint.earn.params.EarnStrategiesParams;
import dev.andstuff.kraken.api.endpoint.earn.params.LockType;
import dev.andstuff.kraken.api.endpoint.earn.response.AllocationStatus;
import dev.andstuff.kraken.api.endpoint.earn.response.EarnAllocations;
import dev.andstuff.kraken.api.endpoint.earn.response.EarnStrategies;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import dev.andstuff.kraken.api.rest.KrakenNonceGenerator;
import dev.andstuff.kraken.api.rest.KrakenRestRequester;

@ExtendWith(MockitoExtension.class)
class KrakenAPIEarnTest {

    @Mock private KrakenCredentials credentials;
    @Mock private KrakenNonceGenerator nonceGenerator;
    @Mock private KrakenRestRequester requester;

    @Test
    void should_route_earnStrategies_options_when_called() {
        EarnStrategies earnStrategiesResponse = new EarnStrategies(null, List.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        EarnStrategiesParams params = EarnStrategiesParams.builder().asset("DOT").lockTypes(List.of(LockType.FLEX)).build();
        when(requester.execute(any(EarnStrategiesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(earnStrategiesResponse);

        EarnStrategies result = unit.earnStrategies(params);

        assertThat(result).isSameAs(earnStrategiesResponse);
        verify(requester).execute(argThat((EarnStrategiesEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_earnStrategies_defaults_when_called() {
        EarnStrategies earnStrategiesResponse = new EarnStrategies(null, List.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(EarnStrategiesEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(earnStrategiesResponse);

        EarnStrategies result = unit.earnStrategies();

        assertThat(result).isSameAs(earnStrategiesResponse);
        verify(requester).execute(argThat((EarnStrategiesEndpoint endpoint) -> endpoint.encodedParamsWith("123").equals("nonce=123")), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_earnStrategies_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(unit::earnStrategies).isInstanceOf(IllegalStateException.class).hasMessageContaining("Earn/Strategies");
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_earnAllocations_options_when_called() {
        EarnAllocations earnAllocationsResponse = new EarnAllocations("EUR", null, BigDecimal.ZERO, BigDecimal.ZERO, null, List.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        EarnAllocationsParams params = EarnAllocationsParams.builder().convertedAsset("EUR").hideZeroAllocations(true).build();
        when(requester.execute(any(EarnAllocationsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(earnAllocationsResponse);

        EarnAllocations result = unit.earnAllocations(params);

        assertThat(result).isSameAs(earnAllocationsResponse);
        verify(requester).execute(argThat((EarnAllocationsEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_route_earnAllocations_defaults_when_called() {
        EarnAllocations earnAllocationsResponse = new EarnAllocations("USD", null, BigDecimal.ZERO, BigDecimal.ZERO, null, List.of());
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(EarnAllocationsEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(earnAllocationsResponse);

        EarnAllocations result = unit.earnAllocations();

        assertThat(result).isSameAs(earnAllocationsResponse);
        verify(requester).execute(argThat((EarnAllocationsEndpoint endpoint) -> endpoint.encodedParamsWith("123").equals("nonce=123")), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_earnAllocations_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(unit::earnAllocations).isInstanceOf(IllegalStateException.class).hasMessageContaining("Earn/Allocations");
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_earnAllocate_options_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        EarnAllocationParams params = EarnAllocationParams.of("ESRFUO3-Q62XD-WIOIL7", BigDecimal.ONE);
        when(requester.execute(any(EarnAllocateEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(true);

        boolean result = unit.earnAllocate(params);

        assertThat(result).isTrue();
        verify(requester).execute(argThat((EarnAllocateEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_earnAllocate_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        EarnAllocationParams params = EarnAllocationParams.of("ESRFUO3-Q62XD-WIOIL7", BigDecimal.ONE);

        assertThatThrownBy(() -> unit.earnAllocate(params)).isInstanceOf(IllegalStateException.class).hasMessageContaining("Earn/Allocate");
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_earnDeallocate_options_when_called() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        EarnAllocationParams params = EarnAllocationParams.of("ESRFUO3-Q62XD-WIOIL7", BigDecimal.ONE);
        when(requester.execute(any(EarnDeallocateEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(false);

        boolean result = unit.earnDeallocate(params);

        assertThat(result).isFalse();
        verify(requester).execute(argThat((EarnDeallocateEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_earnDeallocate_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);
        EarnAllocationParams params = EarnAllocationParams.of("ESRFUO3-Q62XD-WIOIL7", BigDecimal.ONE);

        assertThatThrownBy(() -> unit.earnDeallocate(params)).isInstanceOf(IllegalStateException.class).hasMessageContaining("Earn/Deallocate");
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_earnAllocateStatus_strategy_when_called() {
        AllocationStatus allocationStatusResponse = new AllocationStatus(true);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(EarnAllocateStatusEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(allocationStatusResponse);

        AllocationStatus result = unit.earnAllocateStatus("ESRFUO3-Q62XD-WIOIL7");

        assertThat(result).isSameAs(allocationStatusResponse);
        verify(requester).execute(argThat((EarnAllocateStatusEndpoint endpoint) -> ((EarnStatusParams) endpoint.getPostParams()).getStrategyId().equals("ESRFUO3-Q62XD-WIOIL7")),
                same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_earnAllocateStatus_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(() -> unit.earnAllocateStatus("ESRFUO3-Q62XD-WIOIL7")).isInstanceOf(IllegalStateException.class).hasMessageContaining("Earn/AllocateStatus");
        verifyNoInteractions(requester);
    }

    @Test
    void should_route_earnDeallocateStatus_strategy_when_called() {
        AllocationStatus allocationStatusResponse = new AllocationStatus(false);
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        when(requester.execute(any(EarnDeallocateStatusEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(allocationStatusResponse);

        AllocationStatus result = unit.earnDeallocateStatus("ESRFUO3-Q62XD-WIOIL7");

        assertThat(result).isSameAs(allocationStatusResponse);
        verify(requester).execute(argThat((EarnDeallocateStatusEndpoint endpoint) -> ((EarnStatusParams) endpoint.getPostParams()).getStrategyId().equals("ESRFUO3-Q62XD-WIOIL7")),
                same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_earnDeallocateStatus_when_credentials_are_missing() {
        KrakenAPI unit = new KrakenAPI(null, nonceGenerator, requester);

        assertThatThrownBy(() -> unit.earnDeallocateStatus("ESRFUO3-Q62XD-WIOIL7")).isInstanceOf(IllegalStateException.class).hasMessageContaining("Earn/DeallocateStatus");
        verifyNoInteractions(requester);
    }
}
