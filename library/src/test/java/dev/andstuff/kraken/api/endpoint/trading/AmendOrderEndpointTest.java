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
import dev.andstuff.kraken.api.endpoint.trading.params.AmendOrderParams;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderAmended;

@ExtendWith(MockitoExtension.class)
class AmendOrderEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        AmendOrderEndpoint unit = new AmendOrderEndpoint(AmendOrderParams.builder()
                .transactionId("OHYO67-6LP66-HMQ437").orderQuantity(new BigDecimal("1.2500")).displayQuantity(new BigDecimal("1E-1"))
                .limitPrice("+50").triggerPrice("-1.5%").pair("TSLAx/USD").postOnly(true).deadline(Instant.parse("2023-09-24T14:15:22Z")).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("txid", "OHYO67-6LP66-HMQ437"),
                Map.entry("order_qty", "1.2500"),
                Map.entry("display_qty", "0.1"),
                Map.entry("limit_price", "+50"),
                Map.entry("trigger_price", "-1.5%"),
                Map.entry("pair", "TSLAx/USD"),
                Map.entry("post_only", "true"),
                Map.entry("deadline", "2023-09-24T14:15:22Z")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/AmendOrder");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_encode_client_order_id_and_decimal_prices_when_supplied() {
        AmendOrderEndpoint unit = new AmendOrderEndpoint(AmendOrderParams.builder()
                .clientOrderId("6d1b345e-2821-40e2-ad83-4ecb18a06876").limitPrice(new BigDecimal("2.75E+4")).triggerPrice(new BigDecimal("27000.10")).build());

        String result = unit.encodedParamsWith("123");

        assertThat(result.split("&")).containsExactlyInAnyOrder(
                "cl_ord_id=6d1b345e-2821-40e2-ad83-4ecb18a06876", "limit_price=27500", "trigger_price=27000.10", "nonce=123");
    }

    @Test
    void should_reject_request_when_no_order_identifier_is_supplied() {
        AmendOrderEndpoint unit = new AmendOrderEndpoint(AmendOrderParams.builder().orderQuantity(BigDecimal.ONE).build());

        assertThatThrownBy(() -> unit.encodedParamsWith("123")).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Specify exactly one of transactionId or clientOrderId");
    }

    @Test
    void should_reject_request_when_both_order_identifiers_are_supplied() {
        AmendOrderEndpoint unit = new AmendOrderEndpoint(AmendOrderParams.builder().transactionId("OHYO67-6LP66-HMQ437").clientOrderId("arb-20240509-00010").build());

        assertThatThrownBy(() -> unit.encodedParamsWith("123")).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Specify exactly one of transactionId or clientOrderId");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        AmendOrderEndpoint unit = new AmendOrderEndpoint(AmendOrderParams.builder().clientOrderId("6d1b345e-2821-40e2-ad83-4ecb18a06876").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/trading/AmendOrder.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<OrderAmended> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        OrderAmended result = unit.unwrapResponse(response);

        assertThat(result.amendId()).isEqualTo("TEZA4R-DSDGT-IJBOJK");
    }
}
