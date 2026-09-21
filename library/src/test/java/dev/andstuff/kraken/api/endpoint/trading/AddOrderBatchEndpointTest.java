package dev.andstuff.kraken.api.endpoint.trading;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.EnumSet;
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
import dev.andstuff.kraken.api.endpoint.trading.params.AddOrderBatchParams;
import dev.andstuff.kraken.api.endpoint.trading.params.AssetClass;
import dev.andstuff.kraken.api.endpoint.trading.params.BatchOrder;
import dev.andstuff.kraken.api.endpoint.trading.params.ConditionalClose;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderFlag;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderSide;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderType;
import dev.andstuff.kraken.api.endpoint.trading.params.SelfTradePrevention;
import dev.andstuff.kraken.api.endpoint.trading.params.TimeInForce;
import dev.andstuff.kraken.api.endpoint.trading.params.Trigger;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderBatchAdded;

@ExtendWith(MockitoExtension.class)
class AddOrderBatchEndpointTest {

    @Test
    void should_encode_all_options_as_json_when_supplied() throws Exception {
        AddOrderBatchEndpoint unit = new AddOrderBatchEndpoint(AddOrderBatchParams.builder()
                .orders(List.of(
                        BatchOrder.builder().userReference(123456).orderType(OrderType.STOP_LOSS_LIMIT).side(OrderSide.BUY)
                                .volume(new BigDecimal("1.2")).displayVolume(new BigDecimal("1E-10")).price(new BigDecimal("4.0E+4")).price2("-100")
                                .trigger(Trigger.INDEX).leverage("2").reduceOnly(false).selfTradePrevention(SelfTradePrevention.CANCEL_OLDEST)
                                .orderFlags(EnumSet.of(OrderFlag.FCIB, OrderFlag.POST)).timeInForce(TimeInForce.GTD).startTime("0").expireTime("+3600")
                                .close(new ConditionalClose(OrderType.STOP_LOSS_LIMIT, "37000", "36000")).build(),
                        BatchOrder.builder().clientOrderId("6d1b345e-2821-40e2-ad83-4ecb18a06876").orderType(OrderType.LIMIT).side(OrderSide.SELL)
                                .volume(new BigDecimal("1.2")).price("42000").price2(new BigDecimal("41000.0")).build()))
                .pair("BTC/USD").assetClass(AssetClass.TOKENIZED_ASSET).deadline(Instant.parse("2023-09-24T14:15:22Z"))
                .validate(true).broker("AA12 N84G RMQD 5SNN").build());
        JsonMapper mapper = JsonMapper.builder().build();

        String result = unit.encodedParamsWith("1695828490");

        assertThat(mapper.readTree(result)).isEqualTo(mapper.readTree("""
                {
                  "orders": [
                    {"userref": 123456, "ordertype": "stop-loss-limit", "type": "buy", "volume": "1.2", "displayvol": "0.0000000001",
                     "price": "40000", "price2": "-100", "trigger": "index", "leverage": "2", "reduce_only": false, "stptype": "cancel-oldest",
                     "oflags": "post,fcib", "timeinforce": "GTD", "starttm": "0", "expiretm": "+3600",
                     "close": {"ordertype": "stop-loss-limit", "price": "37000", "price2": "36000"}},
                    {"cl_ord_id": "6d1b345e-2821-40e2-ad83-4ecb18a06876", "ordertype": "limit", "type": "sell", "volume": "1.2",
                     "price": "42000", "price2": "41000.0"}
                  ],
                  "pair": "BTC/USD",
                  "asset_class": "tokenized_asset",
                  "deadline": "2023-09-24T14:15:22Z",
                  "validate": true,
                  "broker": "AA12 N84G RMQD 5SNN",
                  "nonce": 1695828490
                }
                """));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/AddOrderBatch");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/json");
    }

    @Test
    void should_encode_required_options_when_using_convenience_constructor() {
        AddOrderBatchEndpoint unit = new AddOrderBatchEndpoint("BTC/USD", List.of(
                BatchOrder.builder().orderType(OrderType.MARKET).side(OrderSide.BUY).volume(new BigDecimal("0.5")).build()));

        String result = unit.encodedParamsWith("123");

        assertThat(result).isEqualTo("{\"orders\":[{\"ordertype\":\"market\",\"type\":\"buy\",\"volume\":\"0.5\"}],\"pair\":\"BTC/USD\",\"nonce\":123}");
    }

    @Test
    void should_explain_invalid_nonce_when_encoding_json() {
        AddOrderBatchEndpoint unit = new AddOrderBatchEndpoint("BTC/USD", List.of(
                BatchOrder.builder().orderType(OrderType.MARKET).side(OrderSide.BUY).volume(new BigDecimal("0.5")).build()));

        assertThatThrownBy(() -> unit.encodedParamsWith("18446744073709551616")).isInstanceOf(IllegalStateException.class)
                .hasMessage("AddOrderBatchParams requires KrakenNonceGenerator to return an unsigned 64-bit integer in canonical decimal form");
    }

    @Test
    void should_reject_missing_pair_when_building_parameters() {
        AddOrderBatchParams.AddOrderBatchParamsBuilder builder = AddOrderBatchParams.builder().orders(List.of());

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("pair");
    }

    @Test
    void should_reject_missing_orders_when_building_parameters() {
        AddOrderBatchParams.AddOrderBatchParamsBuilder builder = AddOrderBatchParams.builder().pair("BTC/USD");

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("orders");
    }

    @Test
    void should_reject_missing_orderType_when_building_batch_order() {
        BatchOrder.BatchOrderBuilder builder = BatchOrder.builder().side(OrderSide.BUY).volume(BigDecimal.ONE);

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("orderType");
    }

    @Test
    void should_reject_missing_side_when_building_batch_order() {
        BatchOrder.BatchOrderBuilder builder = BatchOrder.builder().orderType(OrderType.MARKET).volume(BigDecimal.ONE);

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("side");
    }

    @Test
    void should_reject_missing_volume_when_building_batch_order() {
        BatchOrder.BatchOrderBuilder builder = BatchOrder.builder().orderType(OrderType.MARKET).side(OrderSide.BUY);

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("volume");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        AddOrderBatchEndpoint unit = new AddOrderBatchEndpoint("BTC/USD", List.of());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/trading/AddOrderBatch.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<OrderBatchAdded> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        OrderBatchAdded result = unit.unwrapResponse(response);

        assertThat(result.orders()).extracting(OrderBatchAdded.AddedOrder::transactionId).containsExactly("O5OR23-ADFAD-Y2G61C", "9K6KFS-5H3PL-XBRC7A");
        assertThat(result.orders().getFirst().description().close()).isEqualTo("close position @ stop loss 27000.0 -> limit 26000.0");
        assertThat(result.orders().getLast().description().order()).isEqualTo("sell 0.10500000 XBTUSD @ limit 36000.0");
        assertThat(result.orders().getLast().error()).isNull();
    }

    @Test
    void should_keep_rejected_order_when_batch_is_partially_processed() throws Exception {
        AddOrderBatchEndpoint unit = new AddOrderBatchEndpoint("BTC/USD", List.of());
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":{"orders":[{"txid":"OK8HFF-5J2PL-XLR17S","descr":{"order":"sell 0.14000000 XBTUSD @ limit 40000.0"}},{"error":"EOrder:Insufficient funds"}]}}
                """;

        KrakenResponse<OrderBatchAdded> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        OrderBatchAdded result = unit.unwrapResponse(response);

        assertThat(result.orders()).hasSize(2);
        assertThat(result.orders().getLast().error()).isEqualTo("EOrder:Insufficient funds");
        assertThat(result.orders().getLast().transactionId()).isNull();
    }
}
