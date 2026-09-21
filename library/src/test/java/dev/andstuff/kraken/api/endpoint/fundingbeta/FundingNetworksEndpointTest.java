package dev.andstuff.kraken.api.endpoint.fundingbeta;

import static org.assertj.core.api.Assertions.assertThat;

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

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingNetworksParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingNetworks;

@ExtendWith(MockitoExtension.class)
class FundingNetworksEndpointTest {

    @Test
    void should_send_account_in_query_when_supplied() {
        FundingNetworksEndpoint unit = new FundingNetworksEndpoint(FundingNetworksParams.builder().accountId("AA12").build());

        URL result = unit.buildURL();

        assertThat(result.getFile()).isEqualTo("/funding/v1/networks?account_id=AA12");
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
    }

    @Test
    void should_send_no_query_when_using_defaults() {
        FundingNetworksEndpoint unit = new FundingNetworksEndpoint();

        URL result = unit.buildURL();

        assertThat(result.getFile()).isEqualTo("/funding/v1/networks");
        assertThat(unit.encodedBody()).isEmpty();
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        FundingNetworksEndpoint unit = new FundingNetworksEndpoint();
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/fundingbeta/ListFundingNetworks.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        FundingNetworks result = mapper.readValue(json, unit.getResponseType());

        assertThat(result.networkGroups()).singleElement().satisfies(group -> {
            assertThat(group.networkGroupId()).isEqualTo("f95acdb7-48fb-4441-b5b4-843d3bf60e61");
            assertThat(group.name()).isEqualTo("EVM");
            assertThat(group.networkIds()).containsExactly("2c6eeff9-bd5f-4c09-982d-a5855fa9f0dd", "bc7562cf-1e51-4308-b52b-d062aaa6aa3b", "473365c2-4c95-4bd9-b5c8-974fb91c7d6b");
        });
        assertThat(result.networks()).extracting(FundingNetworks.Network::name).containsExactly("Arbitrum Nova", "Arbitrum One", "Avalanche C Chain");
        assertThat(result.networks().getFirst().networkId()).isEqualTo("2c6eeff9-bd5f-4c09-982d-a5855fa9f0dd");
    }
}
