package dev.andstuff.kraken.api.endpoint.subaccount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
import dev.andstuff.kraken.api.endpoint.subaccount.params.AccountTransferParams;
import dev.andstuff.kraken.api.endpoint.subaccount.params.AssetClass;
import dev.andstuff.kraken.api.endpoint.subaccount.response.AccountTransfer;

@ExtendWith(MockitoExtension.class)
class AccountTransferEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        AccountTransferEndpoint unit = new AccountTransferEndpoint(AccountTransferParams.builder().asset("AAPLx").assetClass(AssetClass.TOKENIZED_ASSET)
                .amount(new BigDecimal("0.0000000012300")).from("ABCD 1234 EFGH 5678").to("IJKL 0987 MNOP 6543").build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("asset", "AAPLx"),
                Map.entry("asset_class", "tokenized_asset"),
                Map.entry("amount", "0.0000000012300"),
                Map.entry("from", "ABCD 1234 EFGH 5678"),
                Map.entry("to", "IJKL 0987 MNOP 6543")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/AccountTransfer");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/x-www-form-urlencoded");
    }

    @Test
    void should_default_to_currency_class_when_asset_class_is_omitted() {
        AccountTransferEndpoint unit = new AccountTransferEndpoint(AccountTransferParams.builder().asset("XBT").amount(new BigDecimal("1E-8"))
                .from("ABCD 1234 EFGH 5678").to("IJKL 0987 MNOP 6543").build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsEntry("asset_class", "currency").containsEntry("amount", "0.00000001");
    }

    @Test
    void should_reject_missing_asset_when_building_parameters() {
        assertThatThrownBy(() -> AccountTransferParams.builder().amount(BigDecimal.ONE).from("ABCD 1234 EFGH 5678").to("IJKL 0987 MNOP 6543").build())
                .isInstanceOf(NullPointerException.class).hasMessageContaining("asset");
    }

    @Test
    void should_reject_missing_amount_when_building_parameters() {
        assertThatThrownBy(() -> AccountTransferParams.builder().asset("XBT").from("ABCD 1234 EFGH 5678").to("IJKL 0987 MNOP 6543").build())
                .isInstanceOf(NullPointerException.class).hasMessageContaining("amount");
    }

    @Test
    void should_reject_missing_source_when_building_parameters() {
        assertThatThrownBy(() -> AccountTransferParams.builder().asset("XBT").amount(BigDecimal.ONE).to("IJKL 0987 MNOP 6543").build())
                .isInstanceOf(NullPointerException.class).hasMessageContaining("from");
    }

    @Test
    void should_reject_missing_destination_when_building_parameters() {
        assertThatThrownBy(() -> AccountTransferParams.builder().asset("XBT").amount(BigDecimal.ONE).from("ABCD 1234 EFGH 5678").build())
                .isInstanceOf(NullPointerException.class).hasMessageContaining("to");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        AccountTransferEndpoint unit = new AccountTransferEndpoint(AccountTransferParams.builder().asset("XBT").amount(BigDecimal.ONE)
                .from("ABCD 1234 EFGH 5678").to("IJKL 0987 MNOP 6543").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/subaccount/AccountTransfer.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<AccountTransfer> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        AccountTransfer result = unit.unwrapResponse(response);

        assertThat(result).isEqualTo(new AccountTransfer("TOH3AS2-LPCWR8-JDQGEU", AccountTransfer.Status.COMPLETE));
    }

    @Test
    void should_read_pending_status_when_transfer_is_not_complete() throws Exception {
        AccountTransferEndpoint unit = new AccountTransferEndpoint(AccountTransferParams.builder().asset("XBT").amount(BigDecimal.ONE)
                .from("ABCD 1234 EFGH 5678").to("IJKL 0987 MNOP 6543").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":{"transfer_id":"TOH3AS2-LPCWR8-JDQGEU","status":"pending"}}
                """;

        KrakenResponse<AccountTransfer> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        AccountTransfer result = unit.unwrapResponse(response);

        assertThat(result.status()).isEqualTo(AccountTransfer.Status.PENDING);
    }

    @Test
    void should_fall_back_to_unknown_status_when_kraken_adds_a_transfer_status() throws Exception {
        AccountTransferEndpoint unit = new AccountTransferEndpoint(AccountTransferParams.builder().asset("XBT").amount(BigDecimal.ONE)
                .from("ABCD 1234 EFGH 5678").to("IJKL 0987 MNOP 6543").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":{"transfer_id":"TOH3AS2-LPCWR8-JDQGEU","status":"future-status"}}
                """;

        KrakenResponse<AccountTransfer> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        AccountTransfer result = unit.unwrapResponse(response);

        assertThat(result.status()).isEqualTo(AccountTransfer.Status.UNKNOWN);
    }
}
