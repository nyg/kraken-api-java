package dev.andstuff.kraken.api.endpoint.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.InputStream;
import java.net.URL;
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
import dev.andstuff.kraken.api.endpoint.account.params.OpenOrdersParams;
import dev.andstuff.kraken.api.endpoint.account.response.OpenOrders;
import dev.andstuff.kraken.api.endpoint.account.response.Order;
import dev.andstuff.kraken.api.endpoint.priv.RebaseMultiplier;

@ExtendWith(MockitoExtension.class)
class OpenOrdersEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        OpenOrdersEndpoint unit = new OpenOrdersEndpoint(OpenOrdersParams.builder().trades(false).userReference(0L).clientOrderId("id +/&=").consolidateTaker(false).withCursor(true).limit(100)
                .cursor("T1BFTjoxNjg4NjY2NTU5").rebaseMultiplier(RebaseMultiplier.BASE).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("trades", "false"),
                Map.entry("userref", "0"),
                Map.entry("cl_ord_id", "id +/&="),
                Map.entry("consolidate_taker", "false"),
                Map.entry("with_cursor", "true"),
                Map.entry("limit", "100"),
                Map.entry("cursor", "T1BFTjoxNjg4NjY2NTU5"),
                Map.entry("rebase_multiplier", "base")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/OpenOrders");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_omit_optional_values_when_not_supplied() {
        OpenOrdersEndpoint unit = new OpenOrdersEndpoint();

        assertThat(unit.encodedParamsWith("123")).isEqualTo("nonce=123");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        OpenOrdersEndpoint unit = new OpenOrdersEndpoint(OpenOrdersParams.builder().build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/account/OpenOrders.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<OpenOrders> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        OpenOrders result = unit.unwrapResponse(response);

        assertThat(result.open().get("OQCLML-BW3P3-BUCMWZ").executedVolume()).isEqualByComparingTo("0.37500000");
        assertThat(result.open().get("OQCLML-BW3P3-BUCMWZ").openTime()).isEqualTo(Instant.ofEpochSecond(1688666559L, 897400000L));
        assertThat(result.open().get("OQCLML-BW3P3-BUCMWZ").description().orderType()).isEqualTo(Order.OrderType.LIMIT);
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    void should_preserve_margin_and_unknown_enums_when_new_order_values_are_returned() throws Exception {
        OpenOrdersEndpoint unit = new OpenOrdersEndpoint();
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module())
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
        String json = """
                {"error":[],"result":{"open":{"order":{"margin":true,"status":"future-status","descr":{"ordertype":"future-type"},"future_field":123}}}}
                """;

        KrakenResponse<OpenOrders> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Order result = unit.unwrapResponse(response).open().get("order");

        assertThat(result.margin()).isTrue();
        assertThat(result.status()).isEqualTo(Order.Status.UNKNOWN);
        assertThat(result.description().orderType()).isEqualTo(Order.OrderType.UNKNOWN);
    }

    @Test
    void should_encode_wallet_in_query_when_wallet_is_selected() {
        // Given
        OpenOrdersEndpoint unit = new OpenOrdersEndpoint(OpenOrdersParams.builder().accountId("WX6V-JUKW-KKPB-QE36").build());

        // When
        URL result = unit.buildURL();

        // Then
        assertThat(result).hasPath("/0/private/OpenOrders").hasQuery("account_id=WX6V-JUKW-KKPB-QE36");
        assertThat(unit.encodedParamsWith("123")).isEqualTo("nonce=123");
    }

    @Test
    void should_reject_cursor_when_cursor_pagination_is_off() {
        // Given
        OpenOrdersEndpoint unit = new OpenOrdersEndpoint(OpenOrdersParams.builder().cursor("T1BFTjoxNjg4NjY2NTU5").build());

        // When
        Throwable result = catchThrowable(() -> unit.encodedParamsWith("123"));

        // Then
        assertThat(result).isInstanceOf(IllegalArgumentException.class).hasMessage("cursor requires withCursor to be true");
    }

    @Test
    void should_decode_order_details_and_next_cursor_when_reading_cursor_page() throws Exception {
        // Given
        OpenOrdersEndpoint unit = new OpenOrdersEndpoint(OpenOrdersParams.builder().withCursor(true).build());
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
        String json = """
                {"error":[],"result":{"open":{"OQCLML-BW3P3-BUCMWZ":{"status":"open","vol":"1.25000000","displayvol":"0.25000000","displayvolremain":"0.12500000",
                "ext_ord_id":"EXT-42","link_id":"OB5VMB-B4U2U-DK2WRW","reduce_only":true}},"cursor":{"next":"T1BFTjoxNjg4NjY2NTU5"}}}
                """;
        KrakenResponse<OpenOrders> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));

        // When
        OpenOrders result = unit.unwrapResponse(response);

        // Then
        assertThat(result.nextCursor()).isEqualTo("T1BFTjoxNjg4NjY2NTU5");
        assertThat(result.open().get("OQCLML-BW3P3-BUCMWZ")).satisfies(order -> {
            assertThat(order.displayVolume()).isEqualByComparingTo("0.25");
            assertThat(order.remainingDisplayVolume()).isEqualByComparingTo("0.125");
            assertThat(order.externalOrderId()).isEqualTo("EXT-42");
            assertThat(order.linkId()).isEqualTo("OB5VMB-B4U2U-DK2WRW");
            assertThat(order.reduceOnly()).isTrue();
        });
    }
}
