package dev.andstuff.kraken.api.endpoint.funding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.funding.params.AssetClass;
import dev.andstuff.kraken.api.endpoint.funding.params.WithdrawalStatusParams;
import dev.andstuff.kraken.api.endpoint.funding.response.Withdrawal;
import dev.andstuff.kraken.api.endpoint.funding.response.WithdrawalStatus;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;

@ExtendWith(MockitoExtension.class)
class WithdrawalStatusEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        WithdrawalStatusEndpoint unit = new WithdrawalStatusEndpoint(WithdrawalStatusParams.builder().asset("id +/&=").assetClass(AssetClass.TOKENIZED_ASSET).method("id +/&=").start("1700000000").end("1700000000").cursor("id +/&=").limit(0).rebaseMultiplier(RebaseMultiplier.BASE).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("asset", "id +/&="),
                Map.entry("aclass", "tokenized_asset"),
                Map.entry("method", "id +/&="),
                Map.entry("start", "1700000000"),
                Map.entry("end", "1700000000"),
                Map.entry("cursor", "id +/&="),
                Map.entry("limit", "0"),
                Map.entry("rebase_multiplier", "base")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/WithdrawStatus");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_omit_optional_values_when_not_supplied() {
        WithdrawalStatusEndpoint unit = new WithdrawalStatusEndpoint();

        assertThat(unit.encodedParamsWith("123")).isEqualTo("nonce=123");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        WithdrawalStatusEndpoint unit = new WithdrawalStatusEndpoint(WithdrawalStatusParams.builder().build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/funding/WithdrawStatus.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<WithdrawalStatus> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        WithdrawalStatus result = response.result().orElseThrow();

        assertThat(result.withdrawals()).hasSize(2);
        assertThat(result.nextCursor()).isNull();
        assertThat(result.withdrawals().getFirst().amount()).isEqualByComparingTo("0.72485000");
        assertThat(result.withdrawals().getLast().statusProp()).isEqualTo(Withdrawal.StatusProp.CANCELED);
        assertThat(result.withdrawals().getFirst().time()).isEqualTo(Instant.ofEpochSecond(1688014586L));
    }

    @Test
    void should_request_first_page_when_pagination_is_enabled() {
        WithdrawalStatusEndpoint unit = new WithdrawalStatusEndpoint(WithdrawalStatusParams.builder().cursor(true).limit(2).build());

        assertThat(unit.encodedParamsWith("123")).contains("cursor=true", "limit=2", "nonce=123");
    }

    @Test
    void should_disable_pagination_when_explicitly_requested() {
        WithdrawalStatusEndpoint unit = new WithdrawalStatusEndpoint(WithdrawalStatusParams.builder().cursor(false).build());

        assertThat(unit.encodedParamsWith("123")).contains("cursor=false");
    }

    @Test
    void should_preserve_cursor_and_precision_when_reading_a_page() throws Exception {
        WithdrawalStatusEndpoint unit = new WithdrawalStatusEndpoint();
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module())
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
        String json = """
                {"error":[],"result":{"withdrawals":[{"amount":"0.0000000000123456789","time":4102444800,"status":"future-status","status-prop":"future-property","future_field":{"nested":true}}],"next_cursor":"opaque +/=cursor","future_page_field":[]}}
                """;

        KrakenResponse<WithdrawalStatus> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        WithdrawalStatus result = response.result().orElseThrow();

        assertThat(result.nextCursor()).isEqualTo("opaque +/=cursor");
        assertThat(result.withdrawals().getFirst().amount()).isEqualByComparingTo("0.0000000000123456789");
        assertThat(result.withdrawals().getFirst().time()).isEqualTo(Instant.ofEpochSecond(4102444800L));
        assertThat(result.withdrawals().getFirst().status()).isEqualTo(Withdrawal.Status.UNKNOWN);
        assertThat(result.withdrawals().getFirst().statusProp()).isEqualTo(Withdrawal.StatusProp.UNKNOWN);
    }

    @Test
    void should_accept_singular_field_when_returned_in_a_page() throws Exception {
        WithdrawalStatusEndpoint unit = new WithdrawalStatusEndpoint();
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":{"withdrawal":[{"refid":"reference"}],"next_cursor":""}}
                """;

        KrakenResponse<WithdrawalStatus> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        WithdrawalStatus result = response.result().orElseThrow();

        assertThat(result.nextCursor()).isEmpty();
        assertThat(result.withdrawals().getFirst().referenceId()).isEqualTo("reference");
    }

    @Test
    void should_return_empty_transactions_when_last_page_is_empty() throws Exception {
        WithdrawalStatusEndpoint unit = new WithdrawalStatusEndpoint();
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":{"withdrawals":[],"next_cursor":null}}
                """;

        KrakenResponse<WithdrawalStatus> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        WithdrawalStatus result = response.result().orElseThrow();

        assertThat(result.withdrawals()).isEmpty();
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    void should_reject_malformed_page_when_transactions_are_missing() {
        WithdrawalStatusEndpoint unit = new WithdrawalStatusEndpoint();
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module()).build();

        assertThatThrownBy(() -> mapper.readValue("{\"error\":[],\"result\":{\"next_cursor\":\"next\"}}", unit.wrappedResponseType(mapper.getTypeFactory())))
                .isInstanceOf(com.fasterxml.jackson.databind.JsonMappingException.class);
    }
}
