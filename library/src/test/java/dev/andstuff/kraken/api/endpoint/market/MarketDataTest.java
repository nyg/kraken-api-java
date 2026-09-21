package dev.andstuff.kraken.api.endpoint.market;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.endpoint.KrakenException;
import dev.andstuff.kraken.api.endpoint.market.params.AssetPairParams;
import dev.andstuff.kraken.api.endpoint.market.params.GroupedOrderBookParams;
import dev.andstuff.kraken.api.endpoint.market.params.Level3OrderBookParams;
import dev.andstuff.kraken.api.endpoint.market.params.OhlcParams;
import dev.andstuff.kraken.api.endpoint.market.params.OrderBookParams;
import dev.andstuff.kraken.api.endpoint.market.params.RecentSpreadsParams;
import dev.andstuff.kraken.api.endpoint.market.params.RecentTradesParams;
import dev.andstuff.kraken.api.endpoint.market.response.AssetInfo;
import dev.andstuff.kraken.api.endpoint.market.response.AssetPairs;
import dev.andstuff.kraken.api.endpoint.market.response.GroupedOrderBook;
import dev.andstuff.kraken.api.endpoint.market.response.Level3OrderBook;
import dev.andstuff.kraken.api.endpoint.market.response.MaintenanceSchedule;
import dev.andstuff.kraken.api.endpoint.market.response.OhlcData;
import dev.andstuff.kraken.api.endpoint.market.response.OrderBook;
import dev.andstuff.kraken.api.endpoint.market.response.RecentSpreads;
import dev.andstuff.kraken.api.endpoint.market.response.RecentTrades;
import dev.andstuff.kraken.api.endpoint.market.response.ServerTime;
import dev.andstuff.kraken.api.endpoint.market.response.SystemStatus;
import dev.andstuff.kraken.api.endpoint.market.response.Ticker;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import dev.andstuff.kraken.api.rest.KrakenNonceGenerator;
import dev.andstuff.kraken.api.rest.KrakenRestRequester;

@ExtendWith(MockitoExtension.class)
class MarketDataTest {

    @Mock
    private KrakenRestRequester requester;

    @Mock
    private KrakenCredentials credentials;

    @Mock
    private KrakenNonceGenerator nonceGenerator;

    @Test
    void should_route_ohlc_through_configured_requester_when_using_default_options() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        OhlcData expected = new OhlcData(Map.of(), 123L);
        when(requester.execute(any(OhlcEndpoint.class))).thenReturn(expected);

        OhlcData result = unit.ohlc("BTC/USD");

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((OhlcEndpoint endpoint) -> endpoint.buildURL().getQuery().equals("pair=BTC%2FUSD")));
    }

    @Test
    void should_route_ohlc_through_configured_requester_when_using_custom_options() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        OhlcParams params = OhlcParams.builder().pair("BTC/USD").interval(60).build();
        OhlcData expected = new OhlcData(Map.of(), 123L);
        when(requester.execute(any(OhlcEndpoint.class))).thenReturn(expected);

        OhlcData result = unit.ohlc(params);

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((OhlcEndpoint endpoint) -> Arrays.stream(endpoint.buildURL().getQuery().split("&"))
                        .map(entry -> entry.split("=", 2))
                        .collect(Collectors.toMap(entry -> entry[0], entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)))
                        .equals(params.toMap())));
    }

    @Test
    void should_route_order_book_through_configured_requester_when_using_default_options() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        Map<String, OrderBook> expected = Map.of("BTC/USD", new OrderBook(List.of(), List.of()));
        when(requester.execute(any(OrderBookEndpoint.class))).thenReturn(expected);

        Map<String, OrderBook> result = unit.orderBook("BTC/USD");

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((OrderBookEndpoint endpoint) -> endpoint.buildURL().getQuery().equals("pair=BTC%2FUSD")));
    }

    @Test
    void should_route_order_book_through_configured_requester_when_using_custom_options() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        OrderBookParams params = OrderBookParams.builder().pair("BTC/USD").count(10).build();
        Map<String, OrderBook> expected = Map.of("BTC/USD", new OrderBook(List.of(), List.of()));
        when(requester.execute(any(OrderBookEndpoint.class))).thenReturn(expected);

        Map<String, OrderBook> result = unit.orderBook(params);

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((OrderBookEndpoint endpoint) -> Arrays.stream(endpoint.buildURL().getQuery().split("&"))
                        .map(entry -> entry.split("=", 2))
                        .collect(Collectors.toMap(entry -> entry[0], entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)))
                        .equals(params.toMap())));
    }

    @Test
    void should_route_recent_trades_through_configured_requester_when_using_default_options() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        RecentTrades expected = new RecentTrades(Map.of(), "123");
        when(requester.execute(any(RecentTradesEndpoint.class))).thenReturn(expected);

        RecentTrades result = unit.recentTrades("BTC/USD");

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((RecentTradesEndpoint endpoint) -> endpoint.buildURL().getQuery().equals("pair=BTC%2FUSD")));
    }

    @Test
    void should_route_recent_trades_through_configured_requester_when_using_custom_options() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        RecentTradesParams params = RecentTradesParams.builder().pair("BTC/USD").count(10).build();
        RecentTrades expected = new RecentTrades(Map.of(), "123");
        when(requester.execute(any(RecentTradesEndpoint.class))).thenReturn(expected);

        RecentTrades result = unit.recentTrades(params);

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((RecentTradesEndpoint endpoint) -> Arrays.stream(endpoint.buildURL().getQuery().split("&"))
                        .map(entry -> entry.split("=", 2))
                        .collect(Collectors.toMap(entry -> entry[0], entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)))
                        .equals(params.toMap())));
    }

    @Test
    void should_route_recent_spreads_through_configured_requester_when_using_default_options() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        RecentSpreads expected = new RecentSpreads(Map.of(), 123L);
        when(requester.execute(any(RecentSpreadsEndpoint.class))).thenReturn(expected);

        RecentSpreads result = unit.recentSpreads("BTC/USD");

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((RecentSpreadsEndpoint endpoint) -> endpoint.buildURL().getQuery().equals("pair=BTC%2FUSD")));
    }

    @Test
    void should_route_recent_spreads_through_configured_requester_when_using_custom_options() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        RecentSpreadsParams params = RecentSpreadsParams.builder().pair("BTC/USD").since(123L).build();
        RecentSpreads expected = new RecentSpreads(Map.of(), 123L);
        when(requester.execute(any(RecentSpreadsEndpoint.class))).thenReturn(expected);

        RecentSpreads result = unit.recentSpreads(params);

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((RecentSpreadsEndpoint endpoint) -> Arrays.stream(endpoint.buildURL().getQuery().split("&"))
                        .map(entry -> entry.split("=", 2))
                        .collect(Collectors.toMap(entry -> entry[0], entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)))
                        .equals(params.toMap())));
    }

    @Test
    void should_route_grouped_order_book_through_configured_requester_when_using_default_options() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        GroupedOrderBook expected = new GroupedOrderBook("BTC/USD", 1000, List.of(), List.of());
        when(requester.execute(any(GroupedOrderBookEndpoint.class))).thenReturn(expected);

        GroupedOrderBook result = unit.groupedOrderBook("BTC/USD");

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((GroupedOrderBookEndpoint endpoint) -> endpoint.buildURL().getQuery().equals("pair=BTC%2FUSD")));
    }

    @Test
    void should_route_grouped_order_book_through_configured_requester_when_using_custom_options() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        GroupedOrderBookParams params = GroupedOrderBookParams.builder().pair("BTC/USD").grouping(1000).build();
        GroupedOrderBook expected = new GroupedOrderBook("BTC/USD", 1000, List.of(), List.of());
        when(requester.execute(any(GroupedOrderBookEndpoint.class))).thenReturn(expected);

        GroupedOrderBook result = unit.groupedOrderBook(params);

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((GroupedOrderBookEndpoint endpoint) -> Arrays.stream(endpoint.buildURL().getQuery().split("&"))
                        .map(entry -> entry.split("=", 2))
                        .collect(Collectors.toMap(entry -> entry[0], entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)))
                        .equals(params.toMap())));
    }

    @Test
    void should_route_maintenance_schedule_through_configured_requester_when_querying_events() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        MaintenanceSchedule expected = new MaintenanceSchedule(List.of());
        when(requester.execute(any(MaintenanceScheduleEndpoint.class))).thenReturn(expected);

        MaintenanceSchedule result = unit.maintenanceSchedule();

        assertThat(result).isSameAs(expected);
        verify(requester).execute(any(MaintenanceScheduleEndpoint.class));
    }

    @Test
    void should_pass_credentials_and_nonce_generator_when_querying_level3_with_default_options() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        Level3OrderBook expected = new Level3OrderBook("YFI/EUR", List.of(), List.of());
        when(requester.execute(any(Level3OrderBookEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(expected);

        Level3OrderBook result = unit.level3OrderBook("YFI/EUR");

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((Level3OrderBookEndpoint endpoint) -> {
            Level3OrderBookParams params = (Level3OrderBookParams) endpoint.getPostParams();
            return params.getPair().equals("YFI/EUR") && params.getDepth() == null;
        }), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_preserve_parameters_and_authentication_when_querying_level3_with_custom_options() {
        KrakenAPI unit = new KrakenAPI(credentials, nonceGenerator, requester);
        Level3OrderBookParams params = Level3OrderBookParams.builder().pair("YFI/EUR").depth(0).build();
        Level3OrderBook expected = new Level3OrderBook("YFI/EUR", List.of(), List.of());
        when(requester.execute(any(Level3OrderBookEndpoint.class), same(credentials), same(nonceGenerator))).thenReturn(expected);

        Level3OrderBook result = unit.level3OrderBook(params);

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((Level3OrderBookEndpoint endpoint) -> endpoint.getPostParams() == params), same(credentials), same(nonceGenerator));
    }

    @Test
    void should_reject_level3_before_requesting_when_credentials_are_absent() {
        KrakenAPI unit = new KrakenAPI(null, requester);

        assertThatThrownBy(() -> unit.level3OrderBook("BTC/USD"))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("Level3");
        verifyNoInteractions(requester);
    }

    @Test
    void should_reject_custom_level3_before_requesting_when_credentials_are_absent() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        Level3OrderBookParams params = Level3OrderBookParams.builder().pair("BTC/USD").depth(10).build();

        assertThatThrownBy(() -> unit.level3OrderBook(params))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("Level3");
        verifyNoInteractions(requester);
    }

    @Test
    void should_propagate_kraken_error_when_requester_rejects_pair() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        KrakenException error = new KrakenException(List.of("EQuery:Unknown asset pair"));
        when(requester.execute(any(OhlcEndpoint.class))).thenThrow(error);

        assertThatThrownBy(() -> unit.ohlc("BTC/USD")).isSameAs(error);
        verify(requester).execute(any(OhlcEndpoint.class));
    }

    @Test
    void should_route_server_time_through_configured_requester_when_querying_time() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        ServerTime expected = new ServerTime(1688669448L, "Thu, 06 Jul 23 18:50:48 +0000");
        when(requester.execute(any(ServerTimeEndpoint.class))).thenReturn(expected);

        ServerTime result = unit.serverTime();

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((ServerTimeEndpoint endpoint) -> endpoint.buildURL().getQuery() == null));
    }

    @Test
    void should_route_system_status_through_configured_requester_when_querying_status() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        SystemStatus expected = new SystemStatus(SystemStatus.Description.ONLINE, Instant.parse("2023-07-06T18:52:00Z"), List.of(), List.of());
        when(requester.execute(any(SystemStatusEndpoint.class))).thenReturn(expected);

        SystemStatus result = unit.systemStatus();

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((SystemStatusEndpoint endpoint) -> endpoint.buildURL().getQuery() == null));
    }

    @Test
    void should_route_asset_info_through_configured_requester_when_using_default_class() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        Map<String, AssetInfo> expected = Map.of();
        when(requester.execute(any(AssetInfoEndpoint.class))).thenReturn(expected);

        Map<String, AssetInfo> result = unit.assetInfo(List.of("XBT", "ETH"));

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((AssetInfoEndpoint endpoint) -> Arrays.stream(endpoint.buildURL().getQuery().split("&"))
                        .map(entry -> entry.split("=", 2))
                        .collect(Collectors.toMap(entry -> entry[0], entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)))
                        .equals(Map.of("asset", "XBT,ETH", "aclass", "currency"))));
    }

    @Test
    void should_route_asset_info_through_configured_requester_when_using_custom_class() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        Map<String, AssetInfo> expected = Map.of();
        when(requester.execute(any(AssetInfoEndpoint.class))).thenReturn(expected);

        Map<String, AssetInfo> result = unit.assetInfo(List.of("AAPLx"), "tokenized_asset");

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((AssetInfoEndpoint endpoint) -> Arrays.stream(endpoint.buildURL().getQuery().split("&"))
                        .map(entry -> entry.split("=", 2))
                        .collect(Collectors.toMap(entry -> entry[0], entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)))
                        .equals(Map.of("asset", "AAPLx", "aclass", "tokenized_asset"))));
    }

    @Test
    void should_route_asset_pairs_through_configured_requester_when_requesting_all_pairs() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        AssetPairs expected = new AssetPairs(Map.of());
        when(requester.execute(any(AssetPairEndpoint.class))).thenReturn(expected);

        AssetPairs result = unit.assetPairs();

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((AssetPairEndpoint endpoint) -> endpoint.buildURL().getQuery() == null));
    }

    @Test
    void should_route_asset_pairs_through_configured_requester_when_requesting_specific_pairs() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        AssetPairs expected = new AssetPairs(Map.of());
        when(requester.execute(any(AssetPairEndpoint.class))).thenReturn(expected);

        AssetPairs result = unit.assetPairs(List.of("ETH/BTC", "ETH/USD"));

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((AssetPairEndpoint endpoint) -> endpoint.buildURL().getQuery().equals("pair=ETH%2FBTC%2CETH%2FUSD")));
    }

    @Test
    void should_route_asset_pairs_through_configured_requester_when_restricting_information() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        AssetPairs expected = new AssetPairs(Map.of());
        when(requester.execute(any(AssetPairEndpoint.class))).thenReturn(expected);

        AssetPairs result = unit.assetPairs(List.of("XBTUSD"), AssetPairParams.Info.LEVERAGE);

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((AssetPairEndpoint endpoint) -> Arrays.stream(endpoint.buildURL().getQuery().split("&"))
                        .map(entry -> entry.split("=", 2))
                        .collect(Collectors.toMap(entry -> entry[0], entry -> URLDecoder.decode(entry[1], StandardCharsets.UTF_8)))
                        .equals(Map.of("pair", "XBTUSD", "info", "leverage"))));
    }

    @Test
    void should_route_ticker_through_configured_requester_when_querying_pairs() {
        KrakenAPI unit = new KrakenAPI(null, requester);
        Map<String, Ticker> expected = Map.of();
        when(requester.execute(any(TickerEndpoint.class))).thenReturn(expected);

        Map<String, Ticker> result = unit.ticker(List.of("XBTUSD", "ETHUSD"));

        assertThat(result).isSameAs(expected);
        verify(requester).execute(argThat((TickerEndpoint endpoint) -> endpoint.buildURL().getQuery().equals("pair=XBTUSD%2CETHUSD")));
    }
}
