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

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.AssetClass;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Direction;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingAssetsParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingAssets;

@ExtendWith(MockitoExtension.class)
class FundingAssetsEndpointTest {

    @Test
    void should_encode_direction_in_path_and_class_in_query_when_supplied() {
        FundingAssetsEndpoint unit = new FundingAssetsEndpoint(FundingAssetsParams.builder()
                .direction(Direction.WITHDRAW).assetClass(AssetClass.TOKENIZED_ASSET).accountId("AA12").build());

        URL result = unit.buildURL();

        assertThat(result.getFile()).isEqualTo("/funding/v1/assets/withdraw?asset_class=tokenized_asset&account_id=AA12");
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
    }

    @Test
    void should_send_no_query_when_only_direction_is_supplied() {
        FundingAssetsEndpoint unit = new FundingAssetsEndpoint(Direction.DEPOSIT);

        URL result = unit.buildURL();

        assertThat(result.getFile()).isEqualTo("/funding/v1/assets/deposit");
    }

    @Test
    void should_reject_missing_direction_when_building_parameters() {
        FundingAssetsParams.FundingAssetsParamsBuilder builder = FundingAssetsParams.builder();

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("direction");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        FundingAssetsEndpoint unit = new FundingAssetsEndpoint(Direction.DEPOSIT);
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/fundingbeta/ListFundingAssets.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        FundingAssets result = mapper.readValue(json, unit.getResponseType());

        assertThat(result.currency()).extracting(FundingAssets.AssetName::name).containsExactly("USDC", "USDT", "BTC", "ETH", "SOL");
        assertThat(result.tokenizedAsset()).extracting(FundingAssets.AssetName::name).containsExactly("TONXx", "NVDAx", "AAPLx");
    }
}
