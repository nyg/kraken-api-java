package dev.andstuff.kraken.api.endpoint.trading;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.EnumSet;
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
import dev.andstuff.kraken.api.endpoint.trading.params.AddOrderParams;
import dev.andstuff.kraken.api.endpoint.trading.params.AssetClass;
import dev.andstuff.kraken.api.endpoint.trading.params.ConditionalClose;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderFlag;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderSide;
import dev.andstuff.kraken.api.endpoint.trading.params.OrderType;
import dev.andstuff.kraken.api.endpoint.trading.params.SelfTradePrevention;
import dev.andstuff.kraken.api.endpoint.trading.params.TimeInForce;
import dev.andstuff.kraken.api.endpoint.trading.params.Trigger;
import dev.andstuff.kraken.api.endpoint.trading.response.OrderAdded;

@ExtendWith(MockitoExtension.class)
class AddOrderEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        AddOrderEndpoint unit = new AddOrderEndpoint(AddOrderParams.builder()
                .userReference(123456).clientOrderId("id +/&=").orderType(OrderType.TRAILING_STOP_LIMIT).side(OrderSide.SELL)
                .volume(new BigDecimal("0.0000000012300")).displayVolume(new BigDecimal("1E-10")).pair("XBT/USD").assetClass(AssetClass.TOKENIZED_ASSET)
                .price("+1.5%").price2("-0").trigger(Trigger.INDEX).leverage("5").reduceOnly(true).selfTradePrevention(SelfTradePrevention.CANCEL_BOTH)
                .orderFlags(EnumSet.of(OrderFlag.VIQC, OrderFlag.POST, OrderFlag.FCIQ)).timeInForce(TimeInForce.GTD).startTime("+60").expireTime("1695828490")
                .close(new ConditionalClose(OrderType.STOP_LOSS_LIMIT, "#5", "21000.0")).deadline(Instant.parse("2023-09-24T14:15:22.123Z"))
                .validate(true).broker("AA12 N84G RMQD 5SNN").build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("userref", "123456"),
                Map.entry("cl_ord_id", "id +/&="),
                Map.entry("ordertype", "trailing-stop-limit"),
                Map.entry("type", "sell"),
                Map.entry("volume", "0.0000000012300"),
                Map.entry("displayvol", "0.0000000001"),
                Map.entry("pair", "XBT/USD"),
                Map.entry("asset_class", "tokenized_asset"),
                Map.entry("price", "+1.5%"),
                Map.entry("price2", "-0"),
                Map.entry("trigger", "index"),
                Map.entry("leverage", "5"),
                Map.entry("reduce_only", "true"),
                Map.entry("stptype", "cancel-both"),
                Map.entry("oflags", "post,fciq,viqc"),
                Map.entry("timeinforce", "GTD"),
                Map.entry("starttm", "+60"),
                Map.entry("expiretm", "1695828490"),
                Map.entry("close[ordertype]", "stop-loss-limit"),
                Map.entry("close[price]", "#5"),
                Map.entry("close[price2]", "21000.0"),
                Map.entry("deadline", "2023-09-24T14:15:22.123Z"),
                Map.entry("validate", "true"),
                Map.entry("broker", "AA12 N84G RMQD 5SNN")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/AddOrder");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/x-www-form-urlencoded");
    }

    @Test
    void should_encode_required_options_when_using_convenience_constructor() {
        AddOrderEndpoint unit = new AddOrderEndpoint("XBTUSD", OrderSide.BUY, OrderType.MARKET, new BigDecimal("1.25"));

        String result = unit.encodedParamsWith("123");

        assertThat(result.split("&")).containsExactlyInAnyOrder("ordertype=market", "type=buy", "volume=1.25", "pair=XBTUSD", "nonce=123");
    }

    @Test
    void should_encode_plain_prices_when_supplied_as_decimals() {
        AddOrderEndpoint unit = new AddOrderEndpoint(AddOrderParams.builder().pair("XBTUSD").side(OrderSide.BUY).orderType(OrderType.STOP_LOSS_LIMIT)
                .volume(new BigDecimal("1.25")).price(new BigDecimal("2.75E+4")).price2(new BigDecimal("27000.50")).build());

        String result = unit.encodedParamsWith("123");

        assertThat(result.split("&")).contains("price=27500", "price2=27000.50");
    }

    @Test
    void should_omit_close_prices_when_not_supplied() {
        AddOrderEndpoint unit = new AddOrderEndpoint(AddOrderParams.builder().pair("XBTUSD").side(OrderSide.BUY).orderType(OrderType.LIMIT)
                .volume(new BigDecimal("1.25")).price("27500").close(new ConditionalClose(OrderType.TAKE_PROFIT, "30000")).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsEntry("close[ordertype]", "take-profit").containsEntry("close[price]", "30000").doesNotContainKey("close[price2]");
    }

    @Test
    void should_reject_missing_pair_when_building_parameters() {
        assertThatThrownBy(() -> AddOrderParams.builder().side(OrderSide.BUY).orderType(OrderType.MARKET).volume(BigDecimal.ONE).build())
                .isInstanceOf(NullPointerException.class).hasMessageContaining("pair");
    }

    @Test
    void should_reject_missing_side_when_building_parameters() {
        assertThatThrownBy(() -> AddOrderParams.builder().pair("XBTUSD").orderType(OrderType.MARKET).volume(BigDecimal.ONE).build())
                .isInstanceOf(NullPointerException.class).hasMessageContaining("side");
    }

    @Test
    void should_reject_missing_orderType_when_building_parameters() {
        assertThatThrownBy(() -> AddOrderParams.builder().pair("XBTUSD").side(OrderSide.BUY).volume(BigDecimal.ONE).build())
                .isInstanceOf(NullPointerException.class).hasMessageContaining("orderType");
    }

    @Test
    void should_reject_missing_volume_when_building_parameters() {
        assertThatThrownBy(() -> AddOrderParams.builder().pair("XBTUSD").side(OrderSide.BUY).orderType(OrderType.MARKET).build())
                .isInstanceOf(NullPointerException.class).hasMessageContaining("volume");
    }

    @Test
    void should_reject_missing_close_orderType_when_creating_conditional_close() {
        assertThatThrownBy(() -> new ConditionalClose(null, "30000", null)).isInstanceOf(NullPointerException.class).hasMessageContaining("orderType");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        AddOrderEndpoint unit = new AddOrderEndpoint("XBTUSD", OrderSide.BUY, OrderType.LIMIT, new BigDecimal("2.1234"));
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/trading/AddOrder.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<OrderAdded> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        OrderAdded result = unit.unwrapResponse(response);

        assertThat(result.description().order()).isEqualTo("buy 2.12340000 XBTUSD @ limit 25000.1 with 2:1 leverage");
        assertThat(result.description().close()).isEqualTo("close position @ stop loss 22000.0 -> limit 21000.0");
        assertThat(result.transactionIds()).containsExactly("OUF4EM-FRGI2-MQMWZD");
    }

    @Test
    void should_omit_transaction_ids_when_order_is_only_validated() throws Exception {
        AddOrderEndpoint unit = new AddOrderEndpoint("XBTUSD", OrderSide.BUY, OrderType.LIMIT, new BigDecimal("1.45"));
        JsonMapper mapper = JsonMapper.builder().addModules(new JavaTimeModule(), new Jdk8Module()).build();

        KrakenResponse<OrderAdded> response = mapper.readValue("{\"error\":[],\"result\":{\"descr\":{\"order\":\"buy 1.45000000 XBTUSD @ limit 27500.0\"}}}", unit.wrappedResponseType(mapper.getTypeFactory()));
        OrderAdded result = unit.unwrapResponse(response);

        assertThat(result.transactionIds()).isNull();
        assertThat(result.description().close()).isNull();
    }
}
