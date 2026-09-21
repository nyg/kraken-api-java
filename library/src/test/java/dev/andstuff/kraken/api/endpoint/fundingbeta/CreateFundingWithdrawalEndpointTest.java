package dev.andstuff.kraken.api.endpoint.fundingbeta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.math.BigDecimal;
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
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.AssetAmount;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.AssetClass;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.CreateFundingWithdrawalParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Scope;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingWithdrawalCreated;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;

@ExtendWith(MockitoExtension.class)
class CreateFundingWithdrawalEndpointTest {

    @Test
    void should_encode_documented_json_body_when_using_quoted_fee() throws Exception {
        CreateFundingWithdrawalEndpoint unit = new CreateFundingWithdrawalEndpoint(CreateFundingWithdrawalParams.builder()
                .scope(Scope.method("d4ec4d52-b159-428e-ba64-f45455a978a1")).addressId("ABR6SXP-SF6CY-VJMONY")
                .amount(new AssetAmount(new Asset(AssetClass.CURRENCY, "USDC"), new BigDecimal("5")))
                .withdrawalFeeToken("YOUR_WITHDRAWAL_FEE_TOKEN").feeIncluded(true).accountId("AA12").build());
        JsonMapper mapper = JsonMapper.builder().build();

        String result = unit.encodedBody();

        assertThat(mapper.readTree(result)).isEqualTo(mapper.readTree("""
                {"scope":{"method_id":"d4ec4d52-b159-428e-ba64-f45455a978a1"},"address_id":"ABR6SXP-SF6CY-VJMONY","amount":{"asset_amount":{"asset":{"class":"currency","name":"USDC"},"amount":"5"}},"fee":{"quoted_fee":{"token":"YOUR_WITHDRAWAL_FEE_TOKEN"},"fee_included":true}}
                """));
        assertThat(unit.buildURL().getFile()).isEqualTo("/funding/v1/withdrawals?account_id=AA12");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/json");
    }

    @Test
    void should_encode_capped_current_fee_and_rebase_multiplier_when_supplied() throws Exception {
        CreateFundingWithdrawalEndpoint unit = new CreateFundingWithdrawalEndpoint(CreateFundingWithdrawalParams.builder()
                .scope(Scope.network("n1")).addressId("AB1")
                .amount(new AssetAmount(new Asset(AssetClass.TOKENIZED_ASSET, "TSLAx"), new BigDecimal("1.30000000")))
                .maxFee(new AssetAmount(new Asset(AssetClass.TOKENIZED_ASSET, "TSLAx"), new BigDecimal("0.013")))
                .feeIncluded(false).rebaseMultiplier(RebaseMultiplier.BASE).expectedAddress("0xabc").build());
        JsonMapper mapper = JsonMapper.builder().build();

        String result = unit.encodedBody();

        assertThat(mapper.readTree(result)).isEqualTo(mapper.readTree("""
                {"scope":{"network_id":"n1"},"address_id":"AB1",
                 "amount":{"asset_amount":{"asset":{"class":"tokenized_asset","name":"TSLAx"},"amount":"1.30000000"},"rebase_multiplier":"base"},
                 "fee":{"current_fee":{"max_fee":{"asset_amount":{"asset":{"class":"tokenized_asset","name":"TSLAx"},"amount":"0.013"},"rebase_multiplier":"base"}},"fee_included":false},
                 "expected_address":"0xabc"}
                """));
    }

    @Test
    void should_send_uncapped_current_fee_when_only_fee_inclusion_is_supplied() {
        CreateFundingWithdrawalEndpoint unit = new CreateFundingWithdrawalEndpoint(CreateFundingWithdrawalParams.builder()
                .scope(Scope.method("m")).addressId("AB1").amount(new AssetAmount(new Asset(AssetClass.CURRENCY, "BTC"), new BigDecimal("0.01"))).feeIncluded(true).build());

        String result = unit.encodedBody();

        assertThat(result).endsWith(",\"fee\":{\"current_fee\":{},\"fee_included\":true}}");
    }

    @Test
    void should_omit_fee_when_no_fee_option_is_supplied() {
        CreateFundingWithdrawalEndpoint unit = new CreateFundingWithdrawalEndpoint(CreateFundingWithdrawalParams.builder()
                .scope(Scope.method("m")).addressId("AB1").amount(new AssetAmount(new Asset(AssetClass.CURRENCY, "BTC"), new BigDecimal("0.01"))).build());

        String result = unit.encodedBody();

        assertThat(result).isEqualTo("{\"scope\":{\"method_id\":\"m\"},\"address_id\":\"AB1\",\"amount\":{\"asset_amount\":{\"asset\":{\"class\":\"currency\",\"name\":\"BTC\"},\"amount\":\"0.01\"}}}");
    }

    @Test
    void should_reject_token_and_maximum_fee_when_both_are_supplied() {
        CreateFundingWithdrawalEndpoint unit = new CreateFundingWithdrawalEndpoint(CreateFundingWithdrawalParams.builder()
                .scope(Scope.method("m")).addressId("AB1").amount(new AssetAmount(new Asset(AssetClass.CURRENCY, "BTC"), BigDecimal.ONE))
                .withdrawalFeeToken("token").maxFee(new AssetAmount(new Asset(AssetClass.CURRENCY, "BTC"), BigDecimal.ONE)).feeIncluded(true).build());

        assertThatThrownBy(unit::encodedBody).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("at most one");
    }

    @Test
    void should_reject_fee_token_when_fee_inclusion_is_missing() {
        CreateFundingWithdrawalEndpoint unit = new CreateFundingWithdrawalEndpoint(CreateFundingWithdrawalParams.builder()
                .scope(Scope.method("m")).addressId("AB1").amount(new AssetAmount(new Asset(AssetClass.CURRENCY, "BTC"), BigDecimal.ONE)).withdrawalFeeToken("token").build());

        assertThatThrownBy(unit::encodedBody).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("feeIncluded");
    }

    @Test
    void should_reject_network_group_scope_when_encoding_request() {
        CreateFundingWithdrawalEndpoint unit = new CreateFundingWithdrawalEndpoint(CreateFundingWithdrawalParams.builder()
                .scope(Scope.networkGroup("g")).addressId("AB1").amount(new AssetAmount(new Asset(AssetClass.CURRENCY, "BTC"), BigDecimal.ONE)).build());

        assertThatThrownBy(unit::encodedBody).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("network group");
    }

    @Test
    void should_reject_amount_without_asset_name_when_encoding_request() {
        CreateFundingWithdrawalEndpoint unit = new CreateFundingWithdrawalEndpoint(CreateFundingWithdrawalParams.builder()
                .scope(Scope.method("m")).addressId("AB1").amount(new AssetAmount(new Asset(AssetClass.CURRENCY, null), BigDecimal.ONE)).build());

        assertThatThrownBy(unit::encodedBody).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("class and a name");
    }

    @Test
    void should_reject_missing_amount_when_building_parameters() {
        CreateFundingWithdrawalParams.CreateFundingWithdrawalParamsBuilder builder = CreateFundingWithdrawalParams.builder().scope(Scope.method("m")).addressId("AB1");

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("amount");
    }

    @Test
    void should_reject_missing_address_when_building_parameters() {
        CreateFundingWithdrawalParams.CreateFundingWithdrawalParamsBuilder builder = CreateFundingWithdrawalParams.builder().scope(Scope.method("m")).amount(new AssetAmount(new Asset(AssetClass.CURRENCY, "BTC"), BigDecimal.ONE));

        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class).hasMessageContaining("addressId");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        CreateFundingWithdrawalEndpoint unit = new CreateFundingWithdrawalEndpoint(CreateFundingWithdrawalParams.builder()
                .scope(Scope.method("m")).addressId("AB1").amount(new AssetAmount(new Asset(AssetClass.CURRENCY, "USDC"), new BigDecimal("5"))).build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/fundingbeta/CreateFundingWithdrawal.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        FundingWithdrawalCreated result = mapper.readValue(json, unit.getResponseType());

        assertThat(result.withdrawalId()).isEqualTo("FTVZiTI-e02T84mm87JmibnObWNdnW");
        assertThat(result.netAmount().assetAmount().amount()).isEqualTo(new BigDecimal("4.00000000"));
        assertThat(result.grossAmount().assetAmount().amount()).isEqualTo(new BigDecimal("5.00000000"));
        assertThat(result.fee().assetAmount().amount()).isEqualTo(new BigDecimal("1.00000000"));
        assertThat(result.fee().assetAmount().asset()).isEqualTo(new Asset(AssetClass.CURRENCY, "USDC"));
        assertThat(result.fee().rebaseMultiplier()).isNull();
        assertThat(result.approvalRequestId()).isNull();
    }
}
