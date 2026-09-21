package dev.andstuff.kraken.api.endpoint.fundingbeta;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

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
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingLimitsParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingWithdrawalLimits;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.MethodLimits;

@ExtendWith(MockitoExtension.class)
class FundingWithdrawalLimitsEndpointTest {

    @Test
    void should_encode_asset_in_path_and_preferred_asset_in_query_when_supplied() {
        FundingWithdrawalLimitsEndpoint unit = new FundingWithdrawalLimitsEndpoint(FundingLimitsParams.builder()
                .assetClass(AssetClass.TOKENIZED_ASSET).asset("AAPLx").preferredAsset(new Asset(AssetClass.CURRENCY, "USD")).build());

        URL result = unit.buildURL();

        assertThat(result.getFile()).isEqualTo("/funding/v1/limits/withdrawal/tokenized_asset/AAPLx?preferred_asset%5Bclass%5D=currency&preferred_asset%5Bname%5D=USD");
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        FundingWithdrawalLimitsEndpoint unit = new FundingWithdrawalLimitsEndpoint(FundingLimitsParams.builder().assetClass(AssetClass.CURRENCY).asset("USDC").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/fundingbeta/ListFundingWithdrawalLimits.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        FundingWithdrawalLimits result = mapper.readValue(json, unit.getResponseType());

        assertThat(result.availableBalance().amount()).isEqualTo(new BigDecimal("689005.55897887"));
        assertThat(result.withdrawalLimits()).singleElement().satisfies(limits -> {
            assertThat(limits.methodId()).isEqualTo("ec344850-a2bf-4fd8-b5c4-eb902a61cd03");
            assertThat(limits.maximumAmount().amount()).isEqualTo(new BigDecimal("500155.05091629"));
            assertThat(limits.maximumReason()).isEqualTo(MethodLimits.MaximumReason.LIMITS);
            assertThat(limits.limits()).singleElement().satisfies(windowLimit -> {
                assertThat(windowLimit.timeWindow()).isEqualTo(Duration.ofDays(1));
                assertThat(windowLimit.limit().limitType()).isEqualTo(MethodLimits.LimitType.EQUIV_AMOUNT_USD);
                assertThat(windowLimit.limit().remaining().amounts().policyAssetAmount().amount()).isEqualTo(new BigDecimal("499995.0013"));
                assertThat(windowLimit.limit().remaining().amounts().requestedAssetAmount().asset()).isEqualTo(new Asset(AssetClass.CURRENCY, "USDC"));
                assertThat(windowLimit.limit().maximum().amounts().usdAmount().amount()).isEqualTo(new BigDecimal("500000.0000"));
                assertThat(windowLimit.limit().used().amounts().requestedAssetAmount().amount()).isEqualTo(new BigDecimal("5.00030010"));
            });
        });
    }
}
