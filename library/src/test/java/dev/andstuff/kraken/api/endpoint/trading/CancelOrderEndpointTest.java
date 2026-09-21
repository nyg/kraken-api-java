package dev.andstuff.kraken.api.endpoint.trading;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.trading.params.CancelOrderParams;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderCancellation;

@ExtendWith(MockitoExtension.class)
class CancelOrderEndpointTest {

    @Test
    void should_encode_transaction_id_when_using_convenience_constructor() {
        CancelOrderEndpoint unit = new CancelOrderEndpoint("OHYO67-6LP66-HMQ437");

        String result = unit.encodedParamsWith("123");

        assertThat(result.split("&")).containsExactlyInAnyOrder("txid=OHYO67-6LP66-HMQ437", "nonce=123");
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/CancelOrder");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_send_user_reference_as_transaction_id_when_supplied() {
        CancelOrderEndpoint unit = new CancelOrderEndpoint(CancelOrderParams.builder().userReference(1234).build());

        String result = unit.encodedParamsWith("123");

        assertThat(result.split("&")).containsExactlyInAnyOrder("txid=1234", "nonce=123");
    }

    @Test
    void should_encode_client_order_id_when_supplied() {
        CancelOrderEndpoint unit = new CancelOrderEndpoint(CancelOrderParams.builder().clientOrderId("arb-20240509-00010").build());

        String result = unit.encodedParamsWith("123");

        assertThat(result.split("&")).containsExactlyInAnyOrder("cl_ord_id=arb-20240509-00010", "nonce=123");
    }

    @Test
    void should_reject_request_when_no_order_identifier_is_supplied() {
        CancelOrderEndpoint unit = new CancelOrderEndpoint(CancelOrderParams.builder().build());

        assertThatThrownBy(() -> unit.encodedParamsWith("123")).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Specify exactly one of transactionId, userReference or clientOrderId");
    }

    @Test
    void should_reject_request_when_several_order_identifiers_are_supplied() {
        CancelOrderEndpoint unit = new CancelOrderEndpoint(CancelOrderParams.builder().transactionId("OHYO67-6LP66-HMQ437").userReference(1234).build());

        assertThatThrownBy(() -> unit.encodedParamsWith("123")).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Specify exactly one of transactionId, userReference or clientOrderId");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        CancelOrderEndpoint unit = new CancelOrderEndpoint("OHYO67-6LP66-HMQ437");
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/trading/CancelOrder.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<OrderCancellation> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        OrderCancellation result = unit.unwrapResponse(response);

        assertThat(result.count()).isEqualTo(1);
        assertThat(result.pending()).isFalse();
    }

    @Test
    void should_report_pending_cancellation_when_kraken_has_not_completed_it() throws Exception {
        CancelOrderEndpoint unit = new CancelOrderEndpoint("OHYO67-6LP66-HMQ437");
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module()).build();

        KrakenResponse<OrderCancellation> response = mapper.readValue("{\"error\":[],\"result\":{\"count\":0,\"pending\":true}}", unit.wrappedResponseType(mapper.getTypeFactory()));
        OrderCancellation result = unit.unwrapResponse(response);

        assertThat(result.count()).isZero();
        assertThat(result.pending()).isTrue();
    }
}
