package dev.andstuff.kraken.api.endpoint.fundingbeta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingAddressesParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Scope;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingAddresses;

@ExtendWith(MockitoExtension.class)
class FundingAddressesEndpointTest {

    @Test
    void should_encode_scope_with_brackets_when_supplied() {
        FundingAddressesEndpoint unit = new FundingAddressesEndpoint(FundingAddressesParams.builder()
                .scope(Scope.method("67b765fd-4efd-42dc-8ce7-63352971d566")).cursor("c").limit(20).accountId("AA12").build());

        URL result = unit.buildURL();

        assertThat(result.getFile()).isEqualTo("/funding/v1/addresses?scope%5Bmethod_id%5D=67b765fd-4efd-42dc-8ce7-63352971d566&cursor=c&limit=20&account_id=AA12");
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
        assertThat(unit.encodedBody()).isEmpty();
    }

    @Test
    void should_reject_scope_with_several_identifiers_when_encoding_request() {
        FundingAddressesEndpoint unit = new FundingAddressesEndpoint(FundingAddressesParams.builder().scope(new Scope("m", "n", null)).build());

        assertThatThrownBy(unit::buildURL).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("exactly one");
    }

    @Test
    void should_reject_null_identifier_when_creating_scope() {
        assertThatThrownBy(() -> Scope.network(null)).isInstanceOf(NullPointerException.class).hasMessageContaining("networkId");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        FundingAddressesEndpoint unit = new FundingAddressesEndpoint();
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/fundingbeta/ListFundingAddresses.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        FundingAddresses result = mapper.readValue(json, unit.getResponseType());

        assertThat(result.nextCursor()).isNull();
        assertThat(result.addresses()).extracting(FundingAddresses.Address::scope).containsExactly(
                Scope.network("d9d375da-44b7-4be1-8a00-8b281acfe366"),
                Scope.method("67b765fd-4efd-42dc-8ce7-63352971d566"),
                Scope.networkGroup("f95acdb7-48fb-4441-b5b4-843d3bf60e61"));
        assertThat(result.addresses().getFirst()).satisfies(address -> {
            assertThat(address.addressId()).isEqualTo("AB7J4FF-BGM7G-V2JMIH");
            assertThat(address.addressDetails().crypto().address()).isEqualTo("0xBef7B36845cA31045E86D0B46DBCac4e6752...");
            assertThat(address.name()).isEqualTo("Personal Wallet");
            assertThat(address.description()).isEqualTo("My Ethereum hardware wallet address");
            assertThat(address.modifier()).isNull();
            assertThat(address.verified()).isTrue();
        });
        assertThat(result.addresses().getLast().name()).isNull();
    }

    @Test
    void should_read_beneficiary_and_modifier_when_address_belongs_to_third_party() throws Exception {
        FundingAddressesEndpoint unit = new FundingAddressesEndpoint();
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();

        FundingAddresses result = mapper.readValue("""
                {"addresses":[{"address_id":"AB1","scope":{"method_id":"m"},"modifier":"third_party","verified":false,
                  "address_details":{"crypto":{"address":"bc1q","beneficiary":{"recipient":"other","typ":"business","name":"Acme","address_1":"1 Main St","postal_code":"10001","counterparty_vasp_name":"Exchange"}}}},
                 {"address_id":"AB2","scope":{"network_id":"n"},"modifier":"trusted","verified":true,
                  "address_details":{"fiat":{"masked_iban":"DE89****3000","intermediary_swift":"CHASUS33","beneficiary":{"recipient":"sender","last_name":"Doe"}}}}]}
                """, unit.getResponseType());

        assertThat(result.addresses().getFirst()).satisfies(address -> {
            assertThat(address.modifier()).isEqualTo(FundingAddresses.Modifier.THIRD_PARTY);
            assertThat(address.verified()).isFalse();
            assertThat(address.addressDetails().crypto().beneficiary().recipient()).isEqualTo("other");
            assertThat(address.addressDetails().crypto().beneficiary().type()).isEqualTo("business");
            assertThat(address.addressDetails().crypto().beneficiary().address1()).isEqualTo("1 Main St");
            assertThat(address.addressDetails().crypto().beneficiary().postalCode()).isEqualTo("10001");
            assertThat(address.addressDetails().crypto().beneficiary().counterpartyVaspName()).isEqualTo("Exchange");
        });
        assertThat(result.addresses().getLast()).satisfies(address -> {
            assertThat(address.modifier()).isEqualTo(FundingAddresses.Modifier.UNKNOWN);
            assertThat(address.addressDetails().fiat().maskedIban()).isEqualTo("DE89****3000");
            assertThat(address.addressDetails().fiat().intermediarySwift()).isEqualTo("CHASUS33");
            assertThat(address.addressDetails().fiat().beneficiary().lastName()).isEqualTo("Doe");
        });
    }
}
