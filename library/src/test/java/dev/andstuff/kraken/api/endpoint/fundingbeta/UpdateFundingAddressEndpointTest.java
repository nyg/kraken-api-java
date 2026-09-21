package dev.andstuff.kraken.api.endpoint.fundingbeta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.UpdateFundingAddressParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingAddressUpdated;

@ExtendWith(MockitoExtension.class)
class UpdateFundingAddressEndpointTest {

    @Test
    void should_encode_address_in_path_and_changes_in_body_when_supplied() {
        UpdateFundingAddressEndpoint unit = new UpdateFundingAddressEndpoint(UpdateFundingAddressParams.builder()
                .addressId("AB7J4FF-BGM7G-V2JMIH").name("Personal Wallet").description("My hardware wallet address").accountId("AA12").build());

        String result = unit.encodedBody();

        assertThat(result).isEqualTo("{\"name\":\"Personal Wallet\",\"description\":\"My hardware wallet address\"}");
        assertThat(unit.buildURL().getFile()).isEqualTo("/funding/v1/addresses/AB7J4FF-BGM7G-V2JMIH?account_id=AA12");
        assertThat(unit.getHttpMethod()).isEqualTo("PUT");
    }

    @Test
    void should_send_only_description_when_name_is_omitted() {
        UpdateFundingAddressEndpoint unit = new UpdateFundingAddressEndpoint(UpdateFundingAddressParams.builder().addressId("AB1").description("new").build());

        String result = unit.encodedBody();

        assertThat(result).isEqualTo("{\"description\":\"new\"}");
    }

    @Test
    void should_reject_missing_address_when_building_parameters() {
        UpdateFundingAddressParams.UpdateFundingAddressParamsBuilder builder = UpdateFundingAddressParams.builder().name("n");

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("addressId");
    }

    @Test
    void should_deserialize_typed_response_when_reading_fixture() throws Exception {
        UpdateFundingAddressEndpoint unit = new UpdateFundingAddressEndpoint(UpdateFundingAddressParams.builder().addressId("AB1").name("n").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/fundingbeta/UpdateFundingAddress.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        FundingAddressUpdated result = mapper.readValue(json, unit.getResponseType());

        assertThat(result.verified()).isTrue();
    }
}
