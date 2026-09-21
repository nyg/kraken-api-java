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

import dev.andstuff.kraken.api.endpoint.fundingbeta.params.AssetClass;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingFeesParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingFees;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;

@ExtendWith(MockitoExtension.class)
class FundingFeesEndpointTest {

    @Test
    void should_encode_method_in_path_and_options_in_query_when_supplied() {
        FundingFeesEndpoint unit = new FundingFeesEndpoint(FundingFeesParams.builder()
                .methodId("d4ec4d52-b159-428e-ba64-f45455a978a1").amount(new BigDecimal("0.0000000012300")).feeIncluded(true)
                .withdrawalFeeToken("token +/&=").rebaseMultiplier(RebaseMultiplier.BASE).accountId("AA12 3456").build());

        URL result = unit.buildURL();

        assertThat(result.getPath()).isEqualTo("/funding/v1/fees/d4ec4d52-b159-428e-ba64-f45455a978a1");
        assertThat(result.getQuery()).isEqualTo("amount=0.0000000012300&fee_included=true&withdrawal_fee_token=token+%2B%2F%26%3D&rebase_multiplier=base&account_id=AA12+3456");
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
        assertThat(unit.encodedBody()).isEmpty();
    }

    @Test
    void should_percent_encode_method_when_building_path() {
        FundingFeesEndpoint unit = new FundingFeesEndpoint(FundingFeesParams.builder().methodId("a b/c").amount(BigDecimal.ONE).build());

        URL result = unit.buildURL();

        assertThat(result.getFile()).isEqualTo("/funding/v1/fees/a%20b%2Fc?amount=1");
    }

    @Test
    void should_reject_missing_method_when_building_parameters() {
        FundingFeesParams.FundingFeesParamsBuilder builder = FundingFeesParams.builder().amount(BigDecimal.ONE);

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("methodId");
    }

    @Test
    void should_reject_missing_amount_when_building_parameters() {
        FundingFeesParams.FundingFeesParamsBuilder builder = FundingFeesParams.builder().methodId("d4ec4d52-b159-428e-ba64-f45455a978a1");

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("amount");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        FundingFeesEndpoint unit = new FundingFeesEndpoint(FundingFeesParams.builder().methodId("d4ec4d52-b159-428e-ba64-f45455a978a1").amount(new BigDecimal("5")).build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/fundingbeta/CalculateFundingFees.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        FundingFees result = mapper.readValue(json, unit.getResponseType());

        assertThat(result.fee().asset().assetClass()).isEqualTo(AssetClass.CURRENCY);
        assertThat(result.fee().asset().name()).isEqualTo("USDC");
        assertThat(result.fee().amount()).isEqualByComparingTo("1");
        assertThat(result.grossAmount().amount()).isEqualTo(new BigDecimal("5.00000000"));
        assertThat(result.netAmount().amount()).isEqualTo(new BigDecimal("4.00000000"));
        assertThat(result.feeDetails().baseFee().amount()).isEqualTo(new BigDecimal("1.00000000"));
        assertThat(result.feeDetails().feePercentage()).isZero();
        assertThat(result.withdrawalFeeToken()).startsWith("AAAAAAAAAAHG33Wc1eES6QpeGgMok_gUnlZC7Y5niezI2");
    }
}
