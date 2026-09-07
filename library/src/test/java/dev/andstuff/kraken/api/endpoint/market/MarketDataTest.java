package dev.andstuff.kraken.api.endpoint.market;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.endpoint.Endpoint;
import dev.andstuff.kraken.api.endpoint.KrakenException;
import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.market.params.GroupedOrderBookParams;
import dev.andstuff.kraken.api.endpoint.market.params.Level3OrderBookParams;
import dev.andstuff.kraken.api.endpoint.market.params.OhlcParams;
import dev.andstuff.kraken.api.endpoint.market.params.OrderBookParams;
import dev.andstuff.kraken.api.endpoint.market.params.RecentSpreadsParams;
import dev.andstuff.kraken.api.endpoint.market.params.RecentTradesParams;
import dev.andstuff.kraken.api.endpoint.market.response.GroupedOrderBook;
import dev.andstuff.kraken.api.endpoint.market.response.Level3OrderBook;
import dev.andstuff.kraken.api.endpoint.market.response.MaintenanceSchedule;
import dev.andstuff.kraken.api.endpoint.market.response.OhlcData;
import dev.andstuff.kraken.api.endpoint.market.response.OrderBook;
import dev.andstuff.kraken.api.endpoint.market.response.RecentSpreads;
import dev.andstuff.kraken.api.endpoint.market.response.RecentTrades;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import dev.andstuff.kraken.api.rest.KrakenNonceGenerator;
import dev.andstuff.kraken.api.rest.KrakenRestRequester;

class MarketDataTest {

    // Match DefaultKrakenRestRequester: bind directly from JSON, not an intermediate tree.
    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .addModules(new JavaTimeModule(), new Jdk8Module())
            .build();

    private static final String PAIR = "BTC/USD";

    @ParameterizedTest
    @MethodSource("publicRequests")
    void encodesEveryDocumentedPublicParameter(PublicEndpoint<?> endpoint, String path, Map<String, String> expected) {
        assertEquals("GET", endpoint.getHttpMethod());
        assertEquals("https", endpoint.buildURL().getProtocol());
        assertEquals("api.kraken.com", endpoint.buildURL().getHost());
        assertEquals("/0/public/" + path, endpoint.buildURL().getPath());
        assertEquals(expected, decode(endpoint.buildURL().getQuery()));
        assertTrue(Arrays.stream(KrakenAPI.Public.values()).anyMatch(value -> value.getPath().equals(path)));
    }

    static Stream<Arguments> publicRequests() {
        return Stream.of(
                Arguments.of(new OhlcEndpoint(OhlcParams.builder().pair(PAIR).interval(60).since(1688671200L)
                                .assetVersion(1).assetClass("tokenized_asset").build()), "OHLC",
                        Map.of("pair", PAIR, "interval", "60", "since", "1688671200", "assetVersion", "1", "asset_class", "tokenized_asset")),
                Arguments.of(new OrderBookEndpoint(OrderBookParams.builder().pair(PAIR).count(500)
                                .assetVersion(1).assetClass("tokenized_asset").build()), "Depth",
                        Map.of("pair", PAIR, "count", "500", "assetVersion", "1", "asset_class", "tokenized_asset")),
                Arguments.of(new RecentTradesEndpoint(RecentTradesParams.builder().pair(PAIR).since("1688671969993150842").count(2)
                                .assetVersion(1).assetClass("tokenized_asset").build()), "Trades",
                        Map.of("pair", PAIR, "since", "1688671969993150842", "count", "2", "assetVersion", "1", "asset_class", "tokenized_asset")),
                Arguments.of(new RecentSpreadsEndpoint(RecentSpreadsParams.builder().pair(PAIR).since(1688672106L)
                                .assetVersion(1).assetClass("tokenized_asset").build()), "Spread",
                        Map.of("pair", PAIR, "since", "1688672106", "assetVersion", "1", "asset_class", "tokenized_asset")),
                Arguments.of(new GroupedOrderBookEndpoint(GroupedOrderBookParams.builder().pair(PAIR).depth(25).grouping(1000).build()), "GroupedBook",
                        Map.of("pair", PAIR, "depth", "25", "grouping", "1000")),
                Arguments.of(new OhlcEndpoint(PAIR), "OHLC", Map.of("pair", PAIR)),
                Arguments.of(new OrderBookEndpoint(PAIR), "Depth", Map.of("pair", PAIR)),
                Arguments.of(new RecentTradesEndpoint(PAIR), "Trades", Map.of("pair", PAIR)),
                Arguments.of(new RecentSpreadsEndpoint(PAIR), "Spread", Map.of("pair", PAIR)),
                Arguments.of(new GroupedOrderBookEndpoint(PAIR), "GroupedBook", Map.of("pair", PAIR)),
                Arguments.of(new MaintenanceScheduleEndpoint(), "MaintenanceSchedule", Map.of()));
    }

    @Test
    void encodesLevel3WithNonceAndFullBookDepth() {
        var endpoint = new Level3OrderBookEndpoint(Level3OrderBookParams.builder().pair("YFI/EUR").depth(0).build());
        assertEquals("POST", endpoint.getHttpMethod());
        assertEquals("https://api.kraken.com/0/private/Level3", endpoint.buildURL().toString());
        assertEquals(KrakenAPI.Private.LEVEL3.getPath(), endpoint.getPath());
        assertEquals(Map.of("pair", "YFI/EUR", "depth", "0", "nonce", "123"), decode(endpoint.encodedParamsWith("123")));
        assertEquals("124", decode(endpoint.encodedParamsWith("124")).get("nonce"));
        assertEquals(Map.of("pair", PAIR, "nonce", "123"), decode(new Level3OrderBookEndpoint(PAIR).encodedParamsWith("123")));
    }

    @Test
    void requiresThePairForEachParameterizedEndpoint() {
        assertThrows(NullPointerException.class, () -> OhlcParams.builder().build());
        assertThrows(NullPointerException.class, () -> OrderBookParams.builder().build());
        assertThrows(NullPointerException.class, () -> RecentTradesParams.builder().build());
        assertThrows(NullPointerException.class, () -> RecentSpreadsParams.builder().build());
        assertThrows(NullPointerException.class, () -> GroupedOrderBookParams.builder().build());
        assertThrows(NullPointerException.class, () -> Level3OrderBookParams.builder().build());
    }

    @Test
    void readsAllCandlePositionsAndReusesTheCommittedCursor() {
        OhlcData data = read(new OhlcEndpoint(PAIR), fixture("ohlc"));
        assertEquals(new OhlcData.Candle(1688671200, decimal("30306.1"), decimal("30306.2"), decimal("30305.7"),
                decimal("30305.7"), decimal("30306.1"), decimal("3.39243896"), 23), data.candles().get("XXBTZUSD").getFirst());
        assertEquals(2, data.candles().get("XXBTZUSD").size());
        assertEquals(1688671260L, data.candles().get("XXBTZUSD").getLast().time());
        assertEquals(1688672160L, data.last());
        assertEquals("1688672160", OhlcParams.builder().pair(PAIR).since(data.last()).build().toMap().get("since"));
    }

    @Test
    void readsDepthLevelsAndIgnoresUnknownBookProperties() {
        Map<String, OrderBook> books = read(new OrderBookEndpoint(PAIR), fixture("depth"));
        OrderBook book = books.get("XXBTZUSD");
        assertEquals(new OrderBook.Level(decimal("30384.10000"), decimal("2.059"), 1688671659), book.asks().getFirst());
        assertEquals(new OrderBook.Level(decimal("30297.00000"), decimal("1.115"), 1688671636), book.bids().getFirst());
        assertEquals(2, book.asks().size());
        assertEquals(2, book.bids().size());
    }

    @Test
    void readsTradeCodesAndPreservesDecimalTimeAndOpaqueCursor() {
        RecentTrades data = read(new RecentTradesEndpoint(PAIR), fixture("trades"));
        assertEquals(new RecentTrades.Trade(decimal("30243.40000"), decimal("0.34507674"), decimal("1688669597.8277369"),
                RecentTrades.Side.BUY, RecentTrades.OrderType.MARKET, "", 61044952), data.trades().get("XXBTZUSD").getFirst());
        assertEquals(RecentTrades.Side.SELL, data.trades().get("XXBTZUSD").getLast().side());
        assertEquals(RecentTrades.OrderType.LIMIT, data.trades().get("XXBTZUSD").getLast().orderType());
        assertEquals("1688671969993150842", data.last());
        assertEquals(data.last(), RecentTradesParams.builder().pair(PAIR).since(data.last()).build().toMap().get("since"));
    }

    @Test
    void preservesSubMicrosecondTradePrecisionAndLargeIdentifiers() {
        String json = fixture("trades").replace("1688669597.8277369", "1688669597.123456789")
                .replace("61044952", "61044952000000001");
        var trade = read(new RecentTradesEndpoint(PAIR), json).trades().get("XXBTZUSD").getFirst();
        assertEquals(decimal("1688669597.123456789"), trade.time());
        assertEquals(61044952000000001L, trade.tradeId());
    }

    @Test
    void toleratesFutureTradeCodes() {
        String json = fixture("trades").replace("\"b\"", "\"future-side\"").replace("\"m\"", "\"future-type\"");
        var trade = read(new RecentTradesEndpoint(PAIR), json).trades().get("XXBTZUSD").getFirst();
        assertEquals(RecentTrades.Side.UNKNOWN, trade.side());
        assertEquals(RecentTrades.OrderType.UNKNOWN, trade.orderType());
    }

    @Test
    void readsSpreadsAndReusesTheCursor() {
        RecentSpreads data = read(new RecentSpreadsEndpoint(PAIR), fixture("spread"));
        assertEquals(new RecentSpreads.Spread(1688671834, decimal("30292.10000"), decimal("30297.50000")),
                data.spreads().get("XXBTZUSD").getFirst());
        assertEquals(1688672106L, data.last());
        assertEquals("1688672106", RecentSpreadsParams.builder().pair(PAIR).since(data.last()).build().toMap().get("since"));
    }

    @Test
    void acceptsDisplayPairKeysForAllLegacyMarketResponses() {
        assertTrue(read(new OhlcEndpoint(PAIR), fixture("ohlc").replace("XXBTZUSD", PAIR)).candles().containsKey(PAIR));
        assertTrue(read(new OrderBookEndpoint(PAIR), fixture("depth").replace("XXBTZUSD", PAIR)).containsKey(PAIR));
        assertTrue(read(new RecentTradesEndpoint(PAIR), fixture("trades").replace("XXBTZUSD", PAIR)).trades().containsKey(PAIR));
        assertTrue(read(new RecentSpreadsEndpoint(PAIR), fixture("spread").replace("XXBTZUSD", PAIR)).spreads().containsKey(PAIR));
    }

    @Test
    void separatesCursorFromMultiplePairKeysRegardlessOfPropertyOrder() {
        String json = """
                {"error":[],"result":{"last":123,"BTC/USD":[],"ETH/USD":[]}}
                """;
        assertEquals(Map.of("BTC/USD", List.of(), "ETH/USD", List.of()), read(new OhlcEndpoint(PAIR), json).candles());
        assertEquals(Map.of("BTC/USD", List.of(), "ETH/USD", List.of()), read(new RecentSpreadsEndpoint(PAIR), json).spreads());
        var trades = read(new RecentTradesEndpoint(PAIR), json.replace(":123", ":\"001234567890123456789\""));
        assertEquals(Map.of("BTC/USD", List.of(), "ETH/USD", List.of()), trades.trades());
        assertEquals("001234567890123456789", trades.last());
    }

    @Test
    void readsGroupedQuantitiesAndGrouping() {
        GroupedOrderBook book = read(new GroupedOrderBookEndpoint(PAIR), fixture("grouped-book"));
        assertEquals(PAIR, book.pair());
        assertEquals(1000, book.grouping());
        assertEquals(new GroupedOrderBook.Level(decimal("90400.00000"), decimal("19.83057746")), book.bids().getFirst());
        assertEquals(new GroupedOrderBook.Level(decimal("90500.00000"), decimal("38.96185061")), book.asks().getFirst());
    }

    @Test
    void readsIndividualLevel3OrdersAndNanosecondTimestamps() {
        Level3OrderBook book = read(new Level3OrderBookEndpoint("YFI/EUR"), fixture("level3"));
        assertEquals("YFI/EUR", book.pair());
        assertEquals(new Level3OrderBook.Order(decimal("3062.00000"), decimal("0.29665800"), "O5KJU4-IEQTM-NDMS6W", 1765622008594292000L),
                book.bids().getFirst());
        assertEquals(2, book.bids().size());
        assertNotEquals(book.bids().getFirst().orderId(), book.bids().getLast().orderId());
        assertEquals(decimal("0.00278335"), book.asks().getFirst().quantity());
        assertEquals(1765622021013826600L, book.asks().getLast().timestamp());
    }

    @Test
    void readsEveryMaintenanceEventField() {
        var event = read(new MaintenanceScheduleEndpoint(), fixture("maintenance-schedule")).events().getFirst();
        assertEquals(new MaintenanceSchedule.Event(21, "Scheduled Maintenance - Website",
                Instant.parse("2026-05-11T09:00:00Z"), Instant.parse("2026-05-11T10:00:00Z"), 1740,
                MaintenanceSchedule.Phase.APPROACHING_30M, List.of(MaintenanceSchedule.Service.SPOT_TRADING),
                MaintenanceSchedule.OrderSubmission.ALLOWED, MaintenanceSchedule.RecommendedAction.REDUCE_ACTIVITY,
                Instant.parse("2026-05-11T08:55:00Z"), "https://status.kraken.com/incidents/b7k2r9wqmn41"), event);
    }

    @Test
    void toleratesFutureMaintenanceValuesAndAnAbsentCancellationDeadline() {
        String json = fixture("maintenance-schedule").replace("approaching_30m", "future-phase")
                .replace("spot_trading", "future-service").replace("allowed", "future-guidance")
                .replace("reduce_activity", "future-action")
                .replace("\"cancel_before_utc\": \"2026-05-11T08:55:00Z\",", "\"future_field\": true,");
        var event = read(new MaintenanceScheduleEndpoint(), json).events().getFirst();
        assertEquals(MaintenanceSchedule.Phase.UNKNOWN, event.phase());
        assertEquals(List.of(MaintenanceSchedule.Service.UNKNOWN), event.affectedServices());
        assertEquals(MaintenanceSchedule.OrderSubmission.UNKNOWN, event.orderSubmission());
        assertEquals(MaintenanceSchedule.RecommendedAction.UNKNOWN, event.recommendedAction());
        assertNull(event.cancelBefore());
    }

    @Test
    void handlesEmptyBooksSchedulesAndTimeSeries() {
        assertTrue(read(new MaintenanceScheduleEndpoint(), "{\"error\":[],\"result\":{\"events\":[]}}").events().isEmpty());
        assertTrue(read(new OrderBookEndpoint(PAIR), "{\"error\":[],\"result\":{\"BTC/USD\":{\"asks\":[],\"bids\":[]}}}").get(PAIR).bids().isEmpty());
        assertTrue(read(new GroupedOrderBookEndpoint(PAIR), "{\"error\":[],\"result\":{\"pair\":\"BTC/USD\",\"grouping\":1,\"asks\":[],\"bids\":[]}}").asks().isEmpty());
        assertTrue(read(new Level3OrderBookEndpoint(PAIR), "{\"error\":[],\"result\":{\"pair\":\"BTC/USD\",\"asks\":[],\"bids\":[]}}").bids().isEmpty());
        String json = "{\"error\":[],\"result\":{\"BTC/USD\":[],\"last\":123}}";
        assertTrue(read(new OhlcEndpoint(PAIR), json).candles().get(PAIR).isEmpty());
        assertTrue(read(new RecentTradesEndpoint(PAIR), json).trades().get(PAIR).isEmpty());
        assertTrue(read(new RecentSpreadsEndpoint(PAIR), json).spreads().get(PAIR).isEmpty());
    }

    @ParameterizedTest
    @MethodSource("publicFacadeCalls")
    void facadeRoutesEveryPublicOverloadThroughTheConfiguredRequester(String path, String fixture, Class<?> type, Function<KrakenAPI, ?> call) {
        var requester = new FixtureRequester(fixture(fixture));
        Object result = call.apply(new KrakenAPI(null, requester));
        assertInstanceOf(type, result);
        assertEquals(path, requester.endpoint.getPath());
        assertInstanceOf(PublicEndpoint.class, requester.endpoint);
    }

    static Stream<Arguments> publicFacadeCalls() {
        return Stream.of(
                facade("OHLC", "ohlc", OhlcData.class, api -> api.ohlc(PAIR)),
                facade("OHLC", "ohlc", OhlcData.class, api -> api.ohlc(OhlcParams.builder().pair(PAIR).build())),
                facade("Depth", "depth", Map.class, api -> api.orderBook(PAIR)),
                facade("Depth", "depth", Map.class, api -> api.orderBook(OrderBookParams.builder().pair(PAIR).build())),
                facade("Trades", "trades", RecentTrades.class, api -> api.recentTrades(PAIR)),
                facade("Trades", "trades", RecentTrades.class, api -> api.recentTrades(RecentTradesParams.builder().pair(PAIR).build())),
                facade("Spread", "spread", RecentSpreads.class, api -> api.recentSpreads(PAIR)),
                facade("Spread", "spread", RecentSpreads.class, api -> api.recentSpreads(RecentSpreadsParams.builder().pair(PAIR).build())),
                facade("GroupedBook", "grouped-book", GroupedOrderBook.class, api -> api.groupedOrderBook(PAIR)),
                facade("GroupedBook", "grouped-book", GroupedOrderBook.class, api -> api.groupedOrderBook(GroupedOrderBookParams.builder().pair(PAIR).build())),
                facade("MaintenanceSchedule", "maintenance-schedule", MaintenanceSchedule.class, KrakenAPI::maintenanceSchedule));
    }

    @Test
    void level3FacadePassesConfiguredCredentialsAndNonceGenerator() {
        var requester = new FixtureRequester(fixture("level3"));
        var credentials = new KrakenCredentials("test-key", "c2VjcmV0");
        KrakenNonceGenerator nonce = () -> "987654321";
        KrakenAPI api = new KrakenAPI(credentials, nonce, requester);
        assertEquals("YFI/EUR", api.level3OrderBook("YFI/EUR").pair());
        assertSame(credentials, requester.credentials);
        assertSame(nonce, requester.nonceGenerator);
        assertEquals(Map.of("pair", "YFI/EUR", "nonce", "987654321"), decode(requester.postBody));
        api.level3OrderBook(Level3OrderBookParams.builder().pair("YFI/EUR").depth(0).build());
        assertEquals(Map.of("pair", "YFI/EUR", "depth", "0", "nonce", "987654321"), decode(requester.postBody));
        assertEquals("Level3", requester.endpoint.getPath());
    }

    @Test
    void rejectsLevel3WithoutCredentialsBeforeCallingTheRequester() {
        var requester = new FixtureRequester(fixture("level3"));
        KrakenAPI api = new KrakenAPI(null, requester);
        assertTrue(assertThrows(IllegalStateException.class, () -> api.level3OrderBook(PAIR)).getMessage().contains("Level3"));
        assertThrows(IllegalStateException.class, () -> api.level3OrderBook(Level3OrderBookParams.builder().pair(PAIR).build()));
        assertNull(requester.endpoint);
    }

    @Test
    void preservesKrakenErrors() {
        var requester = new FixtureRequester("{\"error\":[\"EQuery:Unknown asset pair\"]}");
        var api = new KrakenAPI(null, requester);
        var error = assertThrows(KrakenException.class, () -> api.ohlc(PAIR));
        assertEquals(List.of("EQuery:Unknown asset pair"), error.getErrors());
    }

    private static Arguments facade(String path, String fixture, Class<?> type, Function<KrakenAPI, ?> call) {
        return Arguments.of(path, fixture, type, call);
    }

    private static BigDecimal decimal(String value) {
        return new BigDecimal(value);
    }

    private static Map<String, String> decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) return Map.of();
        return Arrays.stream(encoded.split("&"))
                .map(entry -> entry.split("=", 2))
                .collect(Collectors.toMap(entry -> URLDecoder.decode(entry[0], StandardCharsets.UTF_8),
                        entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)));
    }

    private static String fixture(String name) {
        try (InputStream input = MarketDataTest.class.getResourceAsStream("/market/" + name + ".json")) {
            assertNotNull(input, "Missing fixture " + name);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private static <T> T read(Endpoint<T> endpoint, String json) {
        try {
            KrakenResponse<T> response = MAPPER.readValue(json, endpoint.wrappedResponseType(MAPPER.getTypeFactory()));
            return response.result().orElseThrow(() -> new KrakenException(response.error()));
        }
        catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private static final class FixtureRequester implements KrakenRestRequester {
        private final String json;
        private Endpoint<?> endpoint;
        private KrakenCredentials credentials;
        private KrakenNonceGenerator nonceGenerator;
        private String postBody;

        private FixtureRequester(String json) {
            this.json = json;
        }

        @Override
        public <T> T execute(PublicEndpoint<T> endpoint) {
            this.endpoint = endpoint;
            return read(endpoint, json);
        }

        @Override
        public <T> T execute(PrivateEndpoint<T> endpoint, KrakenCredentials credentials, KrakenNonceGenerator nonceGenerator) {
            this.endpoint = endpoint;
            this.credentials = credentials;
            this.nonceGenerator = nonceGenerator;
            this.postBody = endpoint.encodedParamsWith(nonceGenerator.generate());
            return read(endpoint, json);
        }
    }
}
