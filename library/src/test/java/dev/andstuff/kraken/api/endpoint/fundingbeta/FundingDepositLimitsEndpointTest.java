package dev.andstuff.kraken.api.endpoint.fundingbeta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingDepositLimits;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.MethodLimits;

@ExtendWith(MockitoExtension.class)
class FundingDepositLimitsEndpointTest {

    @Test
    void should_encode_asset_in_path_and_preferred_asset_in_query_when_supplied() {
        FundingDepositLimitsEndpoint unit = new FundingDepositLimitsEndpoint(FundingLimitsParams.builder()
                .assetClass(AssetClass.CURRENCY).asset("BTC").preferredAsset(new Asset(null, "USD")).accountId("AA12").build());

        URL result = unit.buildURL();

        assertThat(result.getFile()).isEqualTo("/funding/v1/limits/deposit/currency/BTC?preferred_asset%5Bname%5D=USD&account_id=AA12");
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
    }

    @Test
    void should_reject_missing_asset_class_when_building_parameters() {
        assertThatThrownBy(() -> FundingLimitsParams.builder().asset("BTC").build()).isInstanceOf(NullPointerException.class).hasMessageContaining("assetClass");
    }

    @Test
    void should_reject_missing_asset_when_building_parameters() {
        assertThatThrownBy(() -> FundingLimitsParams.builder().assetClass(AssetClass.CURRENCY).build()).isInstanceOf(NullPointerException.class).hasMessageContaining("asset");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        FundingDepositLimitsEndpoint unit = new FundingDepositLimitsEndpoint(FundingLimitsParams.builder().assetClass(AssetClass.CURRENCY).asset("USDC").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/fundingbeta/ListFundingDepositLimits.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        FundingDepositLimits result = mapper.readValue(json, unit.getResponseType());

        assertThat(result.depositLimits()).hasSize(2);
        assertThat(result.depositLimits().getFirst().methodId()).isEqualTo("742656ee-ecc8-4287-a301-5a472ca16c24");
        assertThat(result.depositLimits().getFirst().maximumAmount().amount()).isEqualTo(new BigDecimal("100032.01024328"));
        assertThat(result.depositLimits().getFirst().maximumReason()).isNull();
        assertThat(result.depositLimits().getFirst().limits()).isEmpty();
        assertThat(result.depositLimits().getLast().maximumAmount()).isNull();
    }

    @Test
    void should_read_counts_and_amounts_when_limits_have_both_kinds() throws Exception {
        FundingDepositLimitsEndpoint unit = new FundingDepositLimitsEndpoint(FundingLimitsParams.builder().assetClass(AssetClass.CURRENCY).asset("EUR").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();

        FundingDepositLimits result = mapper.readValue("""
                {"deposit_limits":[{"method_id":"m","limits":[
                  {"time_window":"3600","limit":{"limit_type":"attempt","remaining":"4","maximum":"5","used":"1"}},
                  {"time_window":"2592000","limit":{"limit_type":"equiv_amount_eur","remaining":{"policy_asset_amount":{"asset":{"class":"currency","name":"EUR"},"amount":"900.5"}},"maximum":{"policy_asset_amount":{"asset":{"class":"currency","name":"EUR"},"amount":"1000"},"usd_amount":{"asset":{"class":"currency","name":"USD"},"amount":"1080.25"}}}},
                  {"time_window":"60","limit":{"limit_type":"velocity","remaining":"1","maximum":"2"}}]}]}
                """, unit.getResponseType());

        MethodLimits limits = result.depositLimits().getFirst();
        assertThat(limits.limits()).extracting(MethodLimits.TimeWindowLimit::timeWindow).containsExactly(Duration.ofHours(1), Duration.ofDays(30), Duration.ofMinutes(1));
        assertThat(limits.limits().getFirst().limit().limitType()).isEqualTo(MethodLimits.LimitType.ATTEMPT);
        assertThat(limits.limits().getFirst().limit().remaining().count()).isEqualTo(4L);
        assertThat(limits.limits().getFirst().limit().maximum().count()).isEqualTo(5L);
        assertThat(limits.limits().getFirst().limit().used().count()).isEqualTo(1L);
        assertThat(limits.limits().get(1).limit().limitType()).isEqualTo(MethodLimits.LimitType.EQUIV_AMOUNT_EUR);
        assertThat(limits.limits().get(1).limit().remaining().count()).isNull();
        assertThat(limits.limits().get(1).limit().remaining().amounts().policyAssetAmount().amount()).isEqualTo(new BigDecimal("900.5"));
        assertThat(limits.limits().get(1).limit().maximum().amounts().usdAmount().amount()).isEqualTo(new BigDecimal("1080.25"));
        assertThat(limits.limits().get(1).limit().used()).isNull();
        assertThat(limits.limits().getLast().limit().limitType()).isEqualTo(MethodLimits.LimitType.UNKNOWN);
    }
}
