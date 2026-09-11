package dev.andstuff.kraken.example;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.endpoint.market.params.GroupedOrderBookParams;
import dev.andstuff.kraken.api.endpoint.market.params.OhlcParams;
import dev.andstuff.kraken.api.endpoint.market.params.OrderBookParams;
import dev.andstuff.kraken.api.endpoint.market.params.RecentTradesParams;
import dev.andstuff.kraken.api.endpoint.market.response.OhlcData;
import dev.andstuff.kraken.api.endpoint.market.response.RecentTrades;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarketDataExample {

    static void main() {
        KrakenAPI api = new KrakenAPI();
        String pair = "BTC/USD";

        OhlcData candles = api.ohlc(OhlcParams.builder().pair(pair).interval(60).assetVersion(1).build());
        log.info("Hourly candles: {}", candles.candles().get(pair));

        log.info("L2 order book: {}", api.orderBook(OrderBookParams.builder().pair(pair).count(10).build()));
        log.info("Grouped book: {}", api.groupedOrderBook(GroupedOrderBookParams.builder()
                .pair(pair).depth(10).grouping(1000).build()));

        RecentTrades trades = api.recentTrades(RecentTradesParams.builder().pair(pair).count(2).build());
        log.info("Recent trades: {}", trades.trades());
        log.info("Next trade cursor: {}", trades.last());

        log.info("Recent spreads: {}", api.recentSpreads(pair));
        log.info("Upcoming maintenance: {}", api.maintenanceSchedule());
    }
}
