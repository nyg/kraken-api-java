package dev.andstuff.kraken.api.endpoint.fundingbeta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Asset;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.AssetClass;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingDepositAddressesParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Scope;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingDepositAddresses;

@ExtendWith(MockitoExtension.class)
class FundingDepositAddressesEndpointTest {

    @Test
    void should_encode_scope_and_asset_with_brackets_when_supplied() {
        FundingDepositAddressesEndpoint unit = new FundingDepositAddressesEndpoint(FundingDepositAddressesParams.builder()
                .scope(Scope.method("27ede8db-804b-4d91-8e25-46b7b9668730")).cursor("c").limit(20).asset(new Asset(AssetClass.CURRENCY, "USDC")).accountId("AA12").build());

        URL result = unit.buildURL();

        assertThat(result.getPath()).isEqualTo("/funding/v2/deposit/addresses");
        assertThat(result.getQuery()).isEqualTo("scope%5Bmethod_id%5D=27ede8db-804b-4d91-8e25-46b7b9668730&cursor=c&limit=20&asset%5Bclass%5D=currency&asset%5Bname%5D=USDC&account_id=AA12");
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
    }

    @Test
    void should_encode_network_scope_when_supplied() {
        FundingDepositAddressesEndpoint unit = new FundingDepositAddressesEndpoint(FundingDepositAddressesParams.builder().scope(Scope.network("d9d375da")).build());

        URL result = unit.buildURL();

        assertThat(result.getFile()).isEqualTo("/funding/v2/deposit/addresses?scope%5Bnetwork_id%5D=d9d375da");
    }

    @Test
    void should_reject_network_group_scope_when_encoding_request() {
        FundingDepositAddressesEndpoint unit = new FundingDepositAddressesEndpoint(FundingDepositAddressesParams.builder().scope(Scope.networkGroup("f95acdb7")).build());

        assertThatThrownBy(unit::buildURL).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("network group");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        FundingDepositAddressesEndpoint unit = new FundingDepositAddressesEndpoint();
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/fundingbeta/ListFundingClaimedAddresses.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        FundingDepositAddresses result = mapper.readValue(json, unit.getResponseType());

        assertThat(result.nextCursor()).isNull();
        assertThat(result.addresses()).singleElement().satisfies(address -> {
            assertThat(address.methodId()).isEqualTo("27ede8db-804b-4d91-8e25-46b7b9668730");
            assertThat(address.sharesAddressesWithMethodId()).isEqualTo("a001231f-488e-48c0-b36c-6e0d2c1ee247");
            assertThat(address.addressDetails().crypto().address()).isEqualTo("0x0d5cff23d40bcc6b98537c7ad5a839a247f546...");
        });
    }

    @Test
    void should_read_usage_times_when_supplied() throws Exception {
        FundingDepositAddressesEndpoint unit = new FundingDepositAddressesEndpoint();
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();

        FundingDepositAddresses result = mapper.readValue("""
                {"addresses":[{"method_id":"m","id":"ADDR1","last_used":"2026-08-12T10:10:30.123Z","expire_time":"2026-08-19T10:10:30Z","address_details":{"crypto":{"address":"r9cZA1","tag":"12345"}}}],"next_cursor":"next"}
                """, unit.getResponseType());

        assertThat(result.nextCursor()).isEqualTo("next");
        assertThat(result.addresses()).singleElement().satisfies(address -> {
            assertThat(address.id()).isEqualTo("ADDR1");
            assertThat(address.lastUsed()).isEqualTo(Instant.parse("2026-08-12T10:10:30.123Z"));
            assertThat(address.expireTime()).isEqualTo(Instant.parse("2026-08-19T10:10:30Z"));
            assertThat(address.addressDetails().crypto().tag()).isEqualTo("12345");
        });
    }
}
