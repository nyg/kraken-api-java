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

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.CreateFundingAddressParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Scope;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingAddressCreated;

@ExtendWith(MockitoExtension.class)
class CreateFundingAddressEndpointTest {

    @Test
    void should_encode_documented_json_body_when_creating_address() throws Exception {
        CreateFundingAddressEndpoint unit = new CreateFundingAddressEndpoint(CreateFundingAddressParams.builder()
                .scope(Scope.network("d9d375da-44b7-4be1-8a00-8b281acfe366")).address("0xBef7B36845cA31045E86D0B46DBCac4e6752")
                .name("Personal Wallet").description("My hardware wallet address").accountId("AA12").build());
        JsonMapper mapper = JsonMapper.builder().build();

        String result = unit.encodedBody();

        assertThat(mapper.readTree(result)).isEqualTo(mapper.readTree("""
                {"scope":{"network_id":"d9d375da-44b7-4be1-8a00-8b281acfe366"},"address_details":{"crypto":{"address":"0xBef7B36845cA31045E86D0B46DBCac4e6752"}},"name":"Personal Wallet","description":"My hardware wallet address"}
                """));
        assertThat(unit.buildURL().getFile()).isEqualTo("/funding/v1/addresses?account_id=AA12");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/json");
    }

    @Test
    void should_send_tag_and_memo_when_supplied() {
        CreateFundingAddressEndpoint unit = new CreateFundingAddressEndpoint(CreateFundingAddressParams.builder()
                .scope(Scope.method("m")).address("rAddress").tag("123").memo("note \"quoted\"").name("XRP").build());

        String result = unit.encodedBody();

        assertThat(result).isEqualTo("{\"scope\":{\"method_id\":\"m\"},\"address_details\":{\"crypto\":{\"address\":\"rAddress\",\"tag\":\"123\",\"memo\":\"note \\\"quoted\\\"\"}},\"name\":\"XRP\"}");
    }

    @Test
    void should_reject_missing_scope_when_building_parameters() {
        assertThatThrownBy(() -> CreateFundingAddressParams.builder().address("a").name("n").build()).isInstanceOf(NullPointerException.class).hasMessageContaining("scope");
    }

    @Test
    void should_reject_missing_address_when_building_parameters() {
        assertThatThrownBy(() -> CreateFundingAddressParams.builder().scope(Scope.method("m")).name("n").build()).isInstanceOf(NullPointerException.class).hasMessageContaining("address");
    }

    @Test
    void should_reject_missing_name_when_building_parameters() {
        assertThatThrownBy(() -> CreateFundingAddressParams.builder().scope(Scope.method("m")).address("a").build()).isInstanceOf(NullPointerException.class).hasMessageContaining("name");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        CreateFundingAddressEndpoint unit = new CreateFundingAddressEndpoint(CreateFundingAddressParams.builder().scope(Scope.method("m")).address("a").name("n").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/fundingbeta/CreateFundingAddress.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        FundingAddressCreated result = mapper.readValue(json, unit.getResponseType());

        assertThat(result.addressId()).isEqualTo("AB7J4FF-BGM7G-V2JMIH");
        assertThat(result.verified()).isTrue();
    }
}
