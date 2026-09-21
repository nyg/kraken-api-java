package dev.andstuff.kraken.api.endpoint.trading;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
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
import dev.andstuff.kraken.api.endpoint.trading.params.AssetClass;
import dev.andstuff.kraken.api.endpoint.trading.params.EditOrderParams;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderFlag;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderEdited;

@ExtendWith(MockitoExtension.class)
class EditOrderEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        EditOrderEndpoint unit = new EditOrderEndpoint(EditOrderParams.builder()
                .transactionId("OHYO67-6LP66-HMQ437").userReference(42).volume(new BigDecimal("1.2500")).displayVolume(new BigDecimal("1E-1"))
                .pair("XBTUSD").assetClass(AssetClass.TOKENIZED_ASSET).price("#10").price2(new BigDecimal("26500.0"))
                .orderFlags(Set.of(OrderFlag.POST)).deadline(Instant.parse("2023-09-24T14:15:22Z")).cancelResponse(true).validate(false).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("txid", "OHYO67-6LP66-HMQ437"),
                Map.entry("userref", "42"),
                Map.entry("volume", "1.2500"),
                Map.entry("displayvol", "0.1"),
                Map.entry("pair", "XBTUSD"),
                Map.entry("asset_class", "tokenized_asset"),
                Map.entry("price", "#10"),
                Map.entry("price2", "26500.0"),
                Map.entry("oflags", "post"),
                Map.entry("deadline", "2023-09-24T14:15:22Z"),
                Map.entry("cancel_response", "true"),
                Map.entry("validate", "false")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/EditOrder");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_send_original_user_reference_as_transaction_id_when_supplied() {
        EditOrderEndpoint unit = new EditOrderEndpoint(EditOrderParams.builder().originalUserReference(1234).pair("XBTUSD").price(new BigDecimal("2.75E+4")).build());

        String result = unit.encodedParamsWith("123");

        assertThat(result.split("&")).containsExactlyInAnyOrder("txid=1234", "pair=XBTUSD", "price=27500", "nonce=123");
    }

    @Test
    void should_reject_request_when_no_order_identifier_is_supplied() {
        EditOrderEndpoint unit = new EditOrderEndpoint(EditOrderParams.builder().pair("XBTUSD").build());

        assertThatThrownBy(() -> unit.encodedParamsWith("123")).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Specify exactly one of transactionId or originalUserReference");
    }

    @Test
    void should_reject_request_when_both_order_identifiers_are_supplied() {
        EditOrderEndpoint unit = new EditOrderEndpoint(EditOrderParams.builder().transactionId("OHYO67-6LP66-HMQ437").originalUserReference(1234).pair("XBTUSD").build());

        assertThatThrownBy(() -> unit.encodedParamsWith("123")).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Specify exactly one of transactionId or originalUserReference");
    }

    @Test
    void should_reject_missing_pair_when_building_parameters() {
        assertThatThrownBy(() -> EditOrderParams.builder().transactionId("OHYO67-6LP66-HMQ437").build()).isInstanceOf(NullPointerException.class).hasMessageContaining("pair");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        EditOrderEndpoint unit = new EditOrderEndpoint(EditOrderParams.builder().transactionId("OHYO67-6LP66-HMQ437").pair("XBTUSD").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/trading/EditOrder.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<OrderEdited> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        OrderEdited result = unit.unwrapResponse(response);

        assertThat(result.status()).isEqualTo(OrderEdited.Status.OK);
        assertThat(result.transactionId()).isEqualTo("OFVXHJ-KPQ3B-VS7ELA");
        assertThat(result.originalTransactionId()).isEqualTo("OHYO67-6LP66-HMQ437");
        assertThat(result.volume()).isEqualByComparingTo("0.0003");
        assertThat(result.price()).isEqualByComparingTo("19500");
        assertThat(result.price2()).isEqualByComparingTo("32500");
        assertThat(result.ordersCancelled()).isEqualTo(1);
        assertThat(result.description().order()).isEqualTo("buy 0.00030000 XXBTZGBP @ limit 19500.0");
    }

    @Test
    void should_parse_failed_edit_when_status_is_capitalized() throws Exception {
        EditOrderEndpoint unit = new EditOrderEndpoint(EditOrderParams.builder().transactionId("OHYO67-6LP66-HMQ437").pair("XBTUSD").build());
        JsonMapper mapper = JsonMapper.builder().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS).addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":{"status":"Err","error_message":"Order not found","newuserref":"42","olduserref":7,"orders_cancelled":0}}
                """;

        KrakenResponse<OrderEdited> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        OrderEdited result = unit.unwrapResponse(response);

        assertThat(result.status()).isEqualTo(OrderEdited.Status.ERR);
        assertThat(result.errorMessage()).isEqualTo("Order not found");
        assertThat(result.newUserReference()).isEqualTo(42L);
        assertThat(result.oldUserReference()).isEqualTo(7L);
        assertThat(result.ordersCancelled()).isZero();
    }

    @Test
    void should_map_unknown_status_when_kraken_adds_a_value() throws Exception {
        EditOrderEndpoint unit = new EditOrderEndpoint(EditOrderParams.builder().transactionId("OHYO67-6LP66-HMQ437").pair("XBTUSD").build());
        JsonMapper mapper = JsonMapper.builder().enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE).addModules(new JavaTimeModule(), new Jdk8Module()).build();

        KrakenResponse<OrderEdited> response = mapper.readValue("{\"error\":[],\"result\":{\"status\":\"pending\"}}", unit.wrappedResponseType(mapper.getTypeFactory()));

        assertThat(unit.unwrapResponse(response).status()).isEqualTo(OrderEdited.Status.UNKNOWN);
    }
}
