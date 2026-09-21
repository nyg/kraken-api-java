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
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.FundingDepositsParams;
import dev.andstuff.kraken.api.endpoint.fundingbeta.params.Scope;
import dev.andstuff.kraken.api.endpoint.fundingbeta.response.FundingDeposits;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;

@ExtendWith(MockitoExtension.class)
class FundingDepositsEndpointTest {

    @Test
    void should_encode_all_filters_when_supplied() {
        FundingDepositsEndpoint unit = new FundingDepositsEndpoint(FundingDepositsParams.builder()
                .asset(new Asset(AssetClass.CURRENCY, "USDC")).scope(Scope.networkGroup("f95acdb7")).cursor("c").limit(500)
                .startTime(Instant.parse("2026-07-01T00:00:00Z")).endTime(Instant.parse("2026-07-31T23:59:59.5Z"))
                .rebaseMultiplier(RebaseMultiplier.BASE).accountId("AA12").build());

        URL result = unit.buildURL();

        assertThat(result.getPath()).isEqualTo("/funding/v1/deposits");
        assertThat(result.getQuery()).isEqualTo("asset%5Bclass%5D=currency&asset%5Bname%5D=USDC&scope%5Bnetwork_group_id%5D=f95acdb7&cursor=c&limit=500"
                + "&start_time=2026-07-01T00%3A00%3A00Z&end_time=2026-07-31T23%3A59%3A59.500Z&rebase_multiplier=base&account_id=AA12");
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
    }

    @Test
    void should_send_no_query_when_using_defaults() {
        FundingDepositsEndpoint unit = new FundingDepositsEndpoint();

        URL result = unit.buildURL();

        assertThat(result.getFile()).isEqualTo("/funding/v1/deposits");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        FundingDepositsEndpoint unit = new FundingDepositsEndpoint();
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/fundingbeta/ListFundingDeposits.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        FundingDeposits result = mapper.readValue(json, unit.getResponseType());

        assertThat(result.nextCursor()).startsWith("gqNrZXm-RlREc1IxRC1oOTd3c1Y4RFJKUWpPQ0ZTTjVic0Nx");
        assertThat(result.deposits()).hasSize(2);
        assertThat(result.deposits().getFirst()).satisfies(deposit -> {
            assertThat(deposit.depositId()).isEqualTo("FTcQ4qW-fWGQbQwUfqdnZo4dsMn1ao");
            assertThat(deposit.methodId()).isEqualTo("3e7f8072-cc6d-4394-982a-5f4ca6ab27dd");
            assertThat(deposit.networkId()).isEqualTo("b336ce74-8d60-42b8-8714-b1095e06b711");
            assertThat(deposit.status()).isEqualTo(FundingDeposits.Status.SUCCESS);
            assertThat(deposit.amount().amount()).isEqualTo(new BigDecimal("20.00000000"));
            assertThat(deposit.fee().amount()).isEqualTo(new BigDecimal("0.00000000"));
            assertThat(deposit.createTime()).isEqualTo(Instant.parse("2026-07-01T08:31:33Z"));
        });
        assertThat(result.deposits().getLast().amount().amount()).isEqualTo(new BigDecimal("5555.0000000000"));
    }

    @Test
    void should_keep_missing_amounts_null_when_deposit_is_pending() throws Exception {
        FundingDepositsEndpoint unit = new FundingDepositsEndpoint();
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();

        FundingDeposits result = mapper.readValue("""
                {"deposits":[{"deposit_id":"FT1","status":"initial"},{"deposit_id":"FT2","status":"on_hold"}]}
                """, unit.getResponseType());

        assertThat(result.deposits()).extracting(FundingDeposits.Deposit::status).containsExactly(FundingDeposits.Status.INITIAL, FundingDeposits.Status.UNKNOWN);
        assertThat(result.deposits().getFirst().amount()).isNull();
        assertThat(result.deposits().getFirst().fee()).isNull();
    }
}
