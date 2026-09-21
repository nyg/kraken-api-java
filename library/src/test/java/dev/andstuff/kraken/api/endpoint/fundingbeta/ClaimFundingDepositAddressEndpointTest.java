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

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.ClaimFundingDepositAddressParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.ClaimedFundingDepositAddress;

@ExtendWith(MockitoExtension.class)
class ClaimFundingDepositAddressEndpointTest {

    @Test
    void should_send_method_in_json_body_when_claiming_address() {
        ClaimFundingDepositAddressEndpoint unit = new ClaimFundingDepositAddressEndpoint(ClaimFundingDepositAddressParams.builder()
                .methodId("27ede8db-804b-4d91-8e25-46b7b9668730").accountId("AA12").build());

        URL result = unit.buildURL();

        assertThat(result.getFile()).isEqualTo("/funding/v1/deposit/address?account_id=AA12");
        assertThat(unit.getHttpMethod()).isEqualTo("PUT");
        assertThat(unit.getContentType()).isEqualTo("application/json");
        assertThat(unit.encodedBody()).isEqualTo("{\"method_id\":\"27ede8db-804b-4d91-8e25-46b7b9668730\"}");
    }

    @Test
    void should_reject_missing_method_when_building_parameters() {
        assertThatThrownBy(() -> ClaimFundingDepositAddressParams.builder().accountId("AA12").build()).isInstanceOf(NullPointerException.class).hasMessageContaining("methodId");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        ClaimFundingDepositAddressEndpoint unit = new ClaimFundingDepositAddressEndpoint(ClaimFundingDepositAddressParams.builder().methodId("27ede8db-804b-4d91-8e25-46b7b9668730").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/fundingbeta/ClaimFundingDepositAddress.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        ClaimedFundingDepositAddress result = mapper.readValue(json, unit.getResponseType());

        assertThat(result.addressDetails().crypto().address()).isEqualTo("AikyzpZZcNAeovd6XnyEaEe6YB1tPKkXZF37rUWD...");
        assertThat(result.addressDetails().fiat()).isNull();
    }

    @Test
    void should_read_bank_details_when_claiming_fiat_address() throws Exception {
        ClaimFundingDepositAddressEndpoint unit = new ClaimFundingDepositAddressEndpoint(ClaimFundingDepositAddressParams.builder().methodId("method").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();

        ClaimedFundingDepositAddress result = mapper.readValue("""
                {"address_details":{"fiat":{"iban":"DE89370400440532013000","bic":"COBADEFFXXX","name_on_account":"Payward Ltd.","bank_address":"Frankfurt","memo":"REF123"}}}
                """, unit.getResponseType());

        assertThat(result.addressDetails().crypto()).isNull();
        assertThat(result.addressDetails().fiat().iban()).isEqualTo("DE89370400440532013000");
        assertThat(result.addressDetails().fiat().bic()).isEqualTo("COBADEFFXXX");
        assertThat(result.addressDetails().fiat().nameOnAccount()).isEqualTo("Payward Ltd.");
        assertThat(result.addressDetails().fiat().bankAddress()).isEqualTo("Frankfurt");
        assertThat(result.addressDetails().fiat().memo()).isEqualTo("REF123");
    }
}
