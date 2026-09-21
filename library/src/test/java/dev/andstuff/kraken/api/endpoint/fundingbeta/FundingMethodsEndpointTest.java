package dev.andstuff.kraken.api.endpoint.fundingbeta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.math.BigDecimal;
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

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Asset;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.AssetClass;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Direction;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingMethodsParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingMethods;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;

@ExtendWith(MockitoExtension.class)
class FundingMethodsEndpointTest {

    @Test
    void should_encode_asset_filter_with_brackets_when_supplied() {
        FundingMethodsEndpoint unit = new FundingMethodsEndpoint(FundingMethodsParams.builder()
                .direction(Direction.WITHDRAW).asset(new Asset(AssetClass.TOKENIZED_ASSET, "AAPLx")).rebaseMultiplier(RebaseMultiplier.REBASED)
                .limit(50).cursor("next page").accountId("AA12").build());

        URL result = unit.buildURL();

        assertThat(result.getPath()).isEqualTo("/funding/v1/methods/withdraw");
        assertThat(result.getQuery()).isEqualTo("asset%5Bclass%5D=tokenized_asset&asset%5Bname%5D=AAPLx&rebase_multiplier=rebased&limit=50&cursor=next+page&account_id=AA12");
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
    }

    @Test
    void should_send_class_only_when_asset_name_is_omitted() {
        FundingMethodsEndpoint unit = new FundingMethodsEndpoint(FundingMethodsParams.builder().direction(Direction.DEPOSIT).asset(new Asset(AssetClass.CURRENCY, null)).build());

        URL result = unit.buildURL();

        assertThat(result.getFile()).isEqualTo("/funding/v1/methods/deposit?asset%5Bclass%5D=currency");
    }

    @Test
    void should_send_no_query_when_only_direction_is_supplied() {
        FundingMethodsEndpoint unit = new FundingMethodsEndpoint(Direction.DEPOSIT);

        URL result = unit.buildURL();

        assertThat(result.getFile()).isEqualTo("/funding/v1/methods/deposit");
    }

    @Test
    void should_reject_missing_direction_when_building_parameters() {
        FundingMethodsParams.FundingMethodsParamsBuilder builder = FundingMethodsParams.builder().limit(10);

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("direction");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        FundingMethodsEndpoint unit = new FundingMethodsEndpoint(Direction.DEPOSIT);
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/fundingbeta/ListFundingMethods.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        FundingMethods result = mapper.readValue(json, unit.getResponseType());

        assertThat(result.nextCursor()).isNull();
        assertThat(result.methods()).singleElement().satisfies(method -> {
            assertThat(method.asset()).isEqualTo(new Asset(AssetClass.CURRENCY, "USDC"));
            assertThat(method.methodId()).isEqualTo("27ede8db-804b-4d91-8e25-46b7b9668730");
            assertThat(method.methodName()).isEqualTo("Standard");
            assertThat(method.minimumAmount()).isEqualTo(new BigDecimal("2"));
            assertThat(method.maximumAmount()).isNull();
            assertThat(method.fees().base().amount()).isEqualTo(new BigDecimal("0.00000000"));
            assertThat(method.fees().included()).isTrue();
            assertThat(method.network().networkName()).isEqualTo("Ethereum");
            assertThat(method.network().contractAddress()).isEqualTo("0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48");
            assertThat(method.network().onChainAssetSymbol()).isEqualTo("USDC");
            assertThat(method.deposit().addressGeneration().status()).isEqualTo(FundingMethods.AddressGeneration.Status.LIMITED);
            assertThat(method.deposit().addressGeneration().limit()).isEqualTo(5);
            assertThat(method.deposit().sharesAddressesWithMethodId()).isEqualTo("a001231f-488e-48c0-b36c-6e0d2c1ee247");
            assertThat(method.withdrawal()).isNull();
        });
    }

    @Test
    void should_fall_back_to_unknown_when_address_generation_status_is_new() throws Exception {
        FundingMethodsEndpoint unit = new FundingMethodsEndpoint(Direction.WITHDRAW);
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();

        FundingMethods result = mapper.readValue("""
                {"methods":[{"method_id":"m","deposit":{"address_generation":{"status":"on_request"}},"withdrawal":{}}],"next_cursor":"abc"}
                """, unit.getResponseType());

        assertThat(result.nextCursor()).isEqualTo("abc");
        assertThat(result.methods().getFirst().deposit().addressGeneration().status()).isEqualTo(FundingMethods.AddressGeneration.Status.UNKNOWN);
        assertThat(result.methods().getFirst().withdrawal()).isEmpty();
    }
}
