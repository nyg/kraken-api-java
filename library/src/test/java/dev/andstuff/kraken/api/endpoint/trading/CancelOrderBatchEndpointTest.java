package dev.andstuff.kraken.api.endpoint.trading;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.trading.params.CancelOrderBatchParams;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderCancellation;

@ExtendWith(MockitoExtension.class)
class CancelOrderBatchEndpointTest {

    @Test
    void should_encode_all_identifiers_as_json_when_supplied() throws Exception {
        CancelOrderBatchEndpoint unit = new CancelOrderBatchEndpoint(CancelOrderBatchParams.builder()
                .transactionIds(List.of("OP5V2Y-RYKVL-ET3V3B", "OP5V2Y-7YKVL-ET3V3B")).userReferences(List.of(1234, 5678))
                .clientOrderIds(List.of("6d1b345e-2821-40e2-ad83-4ecb18a06876", "arb-20240509-00010")).build());
        JsonMapper mapper = JsonMapper.builder().build();

        String result = unit.encodedParamsWith("1695828490");

        assertThat(mapper.readTree(result)).isEqualTo(mapper.readTree("""
                {"orders":["OP5V2Y-RYKVL-ET3V3B","OP5V2Y-7YKVL-ET3V3B",1234,5678],"cl_ord_ids":["6d1b345e-2821-40e2-ad83-4ecb18a06876","arb-20240509-00010"],"nonce":1695828490}
                """));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/CancelOrderBatch");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/json");
    }

    @Test
    void should_omit_empty_identifier_lists_when_encoding_request() {
        CancelOrderBatchEndpoint unit = new CancelOrderBatchEndpoint(CancelOrderBatchParams.builder()
                .transactionIds(List.of()).clientOrderIds(List.of("arb-20240509-00010")).build());

        String result = unit.encodedParamsWith("123");

        assertThat(result).isEqualTo("{\"cl_ord_ids\":[\"arb-20240509-00010\"],\"nonce\":123}");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        CancelOrderBatchEndpoint unit = new CancelOrderBatchEndpoint(CancelOrderBatchParams.builder().transactionIds(List.of("OP5V2Y-RYKVL-ET3V3B")).build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/trading/CancelOrderBatch.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<OrderCancellation> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        OrderCancellation result = unit.unwrapResponse(response);

        assertThat(result.count()).isEqualTo(2);
    }
}
