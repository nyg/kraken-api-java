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

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.DeleteFundingAddressParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingAddressDeleted;

@ExtendWith(MockitoExtension.class)
class DeleteFundingAddressEndpointTest {

    @Test
    void should_encode_address_in_path_without_body_when_deleting() {
        DeleteFundingAddressEndpoint unit = new DeleteFundingAddressEndpoint(DeleteFundingAddressParams.builder().addressId("AB7J4FF-BGM7G-V2JMIH").accountId("AA12").build());

        String result = unit.buildURL().getFile();

        assertThat(result).isEqualTo("/funding/v1/addresses/AB7J4FF-BGM7G-V2JMIH?account_id=AA12");
        assertThat(unit.getHttpMethod()).isEqualTo("DELETE");
        assertThat(unit.encodedBody()).isEmpty();
    }

    @Test
    void should_use_address_only_when_created_from_identifier() {
        DeleteFundingAddressEndpoint unit = new DeleteFundingAddressEndpoint("AB7J4FF-BGM7G-V2JMIH");

        String result = unit.buildURL().getFile();

        assertThat(result).isEqualTo("/funding/v1/addresses/AB7J4FF-BGM7G-V2JMIH");
    }

    @Test
    void should_reject_missing_address_when_building_parameters() {
        DeleteFundingAddressParams.DeleteFundingAddressParamsBuilder builder = DeleteFundingAddressParams.builder().accountId("AA12");

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("addressId");
    }

    @Test
    void should_deserialize_typed_response_when_reading_fixture() throws Exception {
        DeleteFundingAddressEndpoint unit = new DeleteFundingAddressEndpoint("AB1");
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/fundingbeta/DeleteFundingAddress.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        FundingAddressDeleted result = mapper.readValue(json, unit.getResponseType());

        assertThat(result.deleted()).isTrue();
    }
}
