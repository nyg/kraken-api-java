package dev.andstuff.kraken.api.endpoint.fundingbeta;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

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
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingWithdrawalStatus;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingWithdrawalsParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Scope;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingWithdrawals;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;

@ExtendWith(MockitoExtension.class)
class FundingWithdrawalsEndpointTest {

    @Test
    void should_encode_all_filters_when_supplied() {
        FundingWithdrawalsEndpoint unit = new FundingWithdrawalsEndpoint(FundingWithdrawalsParams.builder()
                .asset(new Asset(AssetClass.TOKENIZED_ASSET, null)).scope(Scope.network("n1")).status(FundingWithdrawalStatus.PENDING)
                .cursor("c").limit(20).startTime(Instant.parse("2026-08-01T00:00:00Z")).endTime(Instant.parse("2026-08-31T00:00:00Z"))
                .rebaseMultiplier(RebaseMultiplier.REBASED).accountId("AA12").build());

        URL result = unit.buildURL();

        assertThat(result.getPath()).isEqualTo("/funding/v1/withdrawals");
        assertThat(result.getQuery()).isEqualTo("asset%5Bclass%5D=tokenized_asset&scope%5Bnetwork_id%5D=n1&status=pending&cursor=c&limit=20"
                + "&start_time=2026-08-01T00%3A00%3A00Z&end_time=2026-08-31T00%3A00%3A00Z&rebase_multiplier=rebased&account_id=AA12");
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
    }

    @Test
    void should_send_no_query_when_using_defaults() {
        FundingWithdrawalsEndpoint unit = new FundingWithdrawalsEndpoint();

        URL result = unit.buildURL();

        assertThat(result.getFile()).isEqualTo("/funding/v1/withdrawals");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        FundingWithdrawalsEndpoint unit = new FundingWithdrawalsEndpoint();
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/fundingbeta/ListFundingWithdrawals.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        FundingWithdrawals result = mapper.readValue(json, unit.getResponseType());

        assertThat(result.nextCursor()).startsWith("gqNrZXm-RlRSeFJ2WS1PSHBXSUlrTkYwQU1tM0MzNDg2dkxF");
        assertThat(result.withdrawals()).hasSize(2);
        assertThat(result.withdrawals().getFirst()).satisfies(withdrawal -> {
            assertThat(withdrawal.withdrawalId()).isEqualTo("FTVZiTI-e02T84mm87JmibnObWNdnW");
            assertThat(withdrawal.amount().amount()).isEqualTo(new BigDecimal("4.00000000"));
            assertThat(withdrawal.fee().amount()).isEqualTo(new BigDecimal("1.00000000"));
            assertThat(withdrawal.methodId()).isEqualTo("d4ec4d52-b159-428e-ba64-f45455a978a1");
            assertThat(withdrawal.status()).isEqualTo(FundingWithdrawalStatus.PENDING);
            assertThat(withdrawal.createTime()).isEqualTo(Instant.parse("2026-08-12T10:10:30Z"));
            assertThat(withdrawal.addressId()).isEqualTo("ABR6SXP-SF6CY-VJMONY");
            assertThat(withdrawal.onchainTransaction()).isNull();
            assertThat(withdrawal.utxoVout()).isNull();
        });
        assertThat(result.withdrawals().getLast().amount().asset()).isEqualTo(new Asset(AssetClass.TOKENIZED_ASSET, "TSLAx"));
    }

    @Test
    void should_read_onchain_details_when_withdrawal_is_broadcast() throws Exception {
        FundingWithdrawalsEndpoint unit = new FundingWithdrawalsEndpoint();
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();

        FundingWithdrawals result = mapper.readValue("""
                {"withdrawals":[{"withdrawal_id":"FT1","status":"success","onchain_transaction":"7f3a","utxo_vout":"2"},{"withdrawal_id":"FT2","status":"on_hold"}]}
                """, unit.getResponseType());

        assertThat(result.withdrawals().getFirst().status()).isEqualTo(FundingWithdrawalStatus.SUCCESS);
        assertThat(result.withdrawals().getFirst().onchainTransaction()).isEqualTo("7f3a");
        assertThat(result.withdrawals().getFirst().utxoVout()).isEqualTo(2L);
        assertThat(result.withdrawals().getLast().status()).isEqualTo(FundingWithdrawalStatus.UNKNOWN);
    }
}
