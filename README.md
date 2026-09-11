# Kraken API client for Java

[![Maven Central](https://img.shields.io/maven-central/v/dev.andstuff.kraken/kraken-api)](https://central.sonatype.com/artifact/dev.andstuff.kraken/kraken-api)

Query [Kraken's REST API][1] in Java.

## Maven

```xml
<dependency>
    <groupId>dev.andstuff.kraken</groupId>
    <artifactId>kraken-api</artifactId>
    <version>3.0.0</version>
</dependency>
```

## Examples

The `examples` folder contains real-world examples that make use of this library. For most examples, you'll need to provide your API keys: rename `api-keys.properties.example`, located in `examples/src/main/resources`, to `api-keys.properties` and fill in your API keys. The file is also picked up from the root of the checkout, or from any parent directory of the one the example is run from. The examples can be run directly from your IDE, or using the command line:

```shell
# clone and build project
git clone https://github.com/nyg/kraken-api-java.git
cd kraken-api-java
mvn clean install

# run the staking rewards summary example
mvn -pl examples exec:java -Dexec.mainClass=dev.andstuff.kraken.example.StakingRewardsSummaryExample
```

### Staking Rewards Summary

This example will generate the `rewards-summary.csv` file, showing how much crypto rewards have been earned since the creation of your account. A picture is worth a thousand words:

![staking-reward-summary](images/staking-rewards-example.png)

### Earn Overview

This example prints where the assets of your account are earning — which strategy holds them, its lock type and estimated APR, what it has paid so far — and which spot balances could still be allocated to a strategy that accepts them, best estimated APR first. Read-only API key permissions are enough, the example never allocates or deallocates anything.

```sh
mvn -pl examples exec:java -Dexec.mainClass=dev.andstuff.kraken.example.EarnOverviewExample
```

### End-of-Year Balance

This example will generate the `eoy-balance.csv` file, showing the balance of your account at a given point in time. If you run the example from your IDE, modify the `dateTo` in `EoyBalanceExample.java`. Otherwise, you can run the example from the command line:

```sh
mvn -pl examples exec:java -Dexec.mainClass=dev.andstuff.kraken.example.EoyBalanceExample -Dexec.args="2025-01-31T00:00:00Z"
```

## Library usage

### Public endpoints

Public endpoints that have been implemented by the library, can be queried in the following way:

```java
KrakenAPI api = new KrakenAPI();

Map<String, AssetInfo> assets = api.assetInfo(List.of("BTC", "ETH"));
// {BTC=AssetInfo[assetClass=currency, alternateName=XBT, maxDecimals=10, …

Map<String, AssetPair> pairs = api.assetPairs(List.of("ETH/BTC", "ETH/USD"));
// {ETH/BTC=AssetPair[alternateName=ETHXBT, webSocketName=ETH/XBT, …
```

If the endpoint has not yet been implemented (feel free to submit a PR!), the generic `query` method can be used, which will return a `JsonNode` of the [Jackson][2] deserialization library:

```java
JsonNode ticker = api.query(KrakenAPI.Public.TICKER, Map.of("pair", "XBTEUR"));
// {"XXBTZEUR":{"a":["62650.00000","1","1.000"],"b":["62649.90000","6","6.000"], …
```

It's also possible to specify the path directly, in case a new endpoint has been added by Kraken and not yet added in the library:

```java
JsonNode trades = api.queryPublic("Trades", Map.of("pair", "XBTUSD", "count", "1"));
// {"XXBTZUSD":[["68515.60000","0.00029628",1.7100231295628998E9,"s","m","",68007835]], …
```

### Market Data

All 12 Market Data endpoints in Kraken's current Spot REST specification have typed methods. In addition to `serverTime`, `systemStatus`, `assetInfo`, `assetPairs`, and `ticker`:

| Endpoint | Typed method | Response |
|---|---|---|
| `OHLC` | `ohlc(pair)` / `ohlc(params)` | `OhlcData`: candles by pair and a `last` cursor |
| `Depth` | `orderBook(pair)` / `orderBook(params)` | `Map<String, OrderBook>` |
| `GroupedBook` | `groupedOrderBook(pair)` / `groupedOrderBook(params)` | `GroupedOrderBook` |
| `Trades` | `recentTrades(pair)` / `recentTrades(params)` | `RecentTrades`: trades by pair and a `last` cursor |
| `Spread` | `recentSpreads(pair)` / `recentSpreads(params)` | `RecentSpreads`: spreads by pair and a `last` cursor |
| `Level3` (private) | `level3OrderBook(pair)` / `level3OrderBook(params)` | `Level3OrderBook` |
| `MaintenanceSchedule` | `maintenanceSchedule()` | `MaintenanceSchedule` |

Use parameter builders to set optional fields; omitted fields retain Kraken's defaults:

```java
OhlcData candles = api.ohlc(OhlcParams.builder()
        .pair("BTC/USD").interval(60).assetVersion(1).build());
List<OhlcData.Candle> hourly = candles.candles().get("BTC/USD");

RecentTrades trades = api.recentTrades(RecentTradesParams.builder()
        .pair("BTC/USD").count(10).build());
RecentTrades nextBatch = api.recentTrades(RecentTradesParams.builder()
        .pair("BTC/USD").since(trades.last()).count(10).build());
```

`OHLC`, `Depth`, `Trades`, and `Spread` accept `assetVersion(1)` for display pair keys such as `BTC/USD`; without it, Kraken returns internal keys such as `XXBTZUSD`. Their `assetClass("tokenized_asset")` option supports xStocks. Response maps preserve the keys Kraken returns.

The required `pair` field selects one asset pair. Optional numeric fields accept the following values; omit them to use Kraken's defaults:

| Endpoint | Option | Values | Default |
|---|---|---|---|
| `OHLC` | `interval` | 1, 5, 15, 30, 60, 240, 1440, 10080, 21600 minutes | 1 |
| `Depth` | `count` | 1–500 entries per side | 100 |
| `Trades` | `count` | 1–1000 trades | 1000 |
| `GroupedBook` | `depth` | 10, 25, 100, 250, 1000 levels per side | 10 |
| `GroupedBook` | `grouping` | 1, 5, 10, 25, 50, 100, 250, 500, 1000 ticks per level | 1 |
| `Level3` | `depth` | 0 (full book), 10, 25, 100, 250, 1000 levels per side | 100 |

OHLC candle times, L2 level times and spread times use `Instant`; the `since` fields for OHLC and spreads remain Unix seconds. Grouped books round asks up and bids down to the nearest grouped price level. `MaintenanceSchedule` returns scheduled events for the next seven days, ordered by expected start time; its times use `Instant`, and `cancelBefore` can be absent.

OHLC includes a final candle that is still forming and retains at most 720 entries. Reuse its `last()` cursor as `since` to poll for committed updates. Trade cursors are opaque strings: pass `last()` unchanged. Prices and quantities use `BigDecimal`; trade and Level3 times use `Instant`, retaining nanosecond precision. Level3 decodes Kraken's integer epoch nanoseconds explicitly.

Level3 requires credentials with **Orders and trades – Query open orders & trades** permission:

```java
KrakenAPI authenticated = new KrakenAPI("my key", "my secret");
Level3OrderBook book = authenticated.level3OrderBook(Level3OrderBookParams.builder()
        .pair("YFI/EUR").depth(10).build());
```

Run the public examples without credentials (after `mvn clean install`):

```sh
mvn -pl examples exec:java -Dexec.mainClass=dev.andstuff.kraken.example.MarketDataExample
```

### Private endpoints

Private endpoints can be queried in the same way as the public ones, but an API key and secret must be provided to the `KrakenAPI` instance:

```java
KrakenAPI api = new KrakenAPI("my key", "my secret");

JsonNode balance = api.query(KrakenAPI.Private.BALANCE);
// {"XXBT":"1234.8396278900", … :)
```

If the Kraken API call returns an error, an unchecked exception of type `KrakenException` is thrown:

```java
// API key doesn't have the permission to create orders
JsonNode order = api.query(KrakenAPI.Private.ADD_ORDER, Map.of(
        "ordertype", "limit", "type", "sell",
        "volume", "1", "pair", "XLTCZUSD",
        "price", "1000", "oflags", "post,fciq",
        "validate", "true"));
// Exception in thread "main" KrakenException(errors=[EGeneral:Permission denied])
```

### Account data

All Account Data operations have typed methods, including balances, credit lines, orders, amendments, trades, positions, fee tiers, API key information, wallets, ledgers, and report exports. Optional settings use the corresponding `...Params.builder()`; omitted values keep Kraken's defaults.

```java
Map<String, BigDecimal> balances = api.accountBalance();
OpenOrders orders = api.openOrders(OpenOrdersParams.builder().trades(true).build());
ClosedOrders page = api.closedOrders(ClosedOrdersParams.builder().offset(50).withoutCount(true).build());
Map<String, AccountTrade> trades = api.queryTrades(QueryTradesParams.builder().transactionIds(List.of("THVRQM-33VKH-UCI7BS")).build());
TradeVolume fees = api.tradeVolume(TradeVolumeParams.builder()
        .pairsWithClass(List.of(new TradeVolumeParams.Pair("TSLAx/USD", "equity_pair")))
        .feeSchedule(true).build());
```

`closedOrders` and `tradesHistory` expose the returned count, which is null when omitted by Kraken. Their `start` and `end` filters accept timestamp strings or transaction IDs. Monetary values use `BigDecimal`, timestamps use `Instant`, including fractional trade times and amendment times decoded from epoch nanoseconds. `creditLines` returns `Optional.empty()` when Kraken reports no credit lines. `accountBalance` can select a wallet using `AccountBalanceParams.accountId`, while `walletAccounts` lists the available wallets.

`TradeVolumeParams` encodes requests as JSON to support class-qualified pairs. Custom REST requesters should send `endpoint.encodedParamsWith(nonce)` unchanged with `endpoint.getContentType()` and use `endpoint.unwrapResponse(response)` to handle endpoint-specific nullable results.

Custom `KrakenNonceGenerator` implementations must produce increasing unsigned 64-bit integers as canonical decimal strings. `TradeVolume` encodes the nonce as a JSON number and rejects malformed, out-of-range or noncanonical values with an `IllegalStateException` that names the generator contract. Canonical formatting keeps the nonce used for signing identical to the number in the JSON body.

### Custom endpoints

You can also define typed endpoints outside the library. The following example demonstrates the same mechanism used by the built-in order book endpoint. Extend `PublicEndpoint<T>`, or `PrivateEndpoint<T>` for a private one, and pass your endpoint to `query`:

```java
public class MyOrderBookEndpoint extends PublicEndpoint<Map<String, OrderBook>> {

    public MyOrderBookEndpoint(String pair) {
        super("Depth", () -> Map.of("pair", pair), new TypeReference<>() {});
    }
}

KrakenAPI api = new KrakenAPI();

Map<String, OrderBook> books = api.query(new MyOrderBookEndpoint("XBTUSD"));
```

The endpoint is run through the same `KrakenRestRequester` as the built-in ones, and a `PrivateEndpoint` is signed with the credentials and nonce generator the `KrakenAPI` instance was built with. Querying one on an instance built without credentials throws an `IllegalStateException`.

Pull requests adding such an endpoint to the library are welcome, see the [architecture documentation](docs/ARCHITECTURE.md).

### Custom REST requester

The current implementation of the library uses the JDK's HttpsURLConnection to make HTTP request. If that doesn't suit your needs and wish to use something else (e.g. Spring RestTemplate, Apache HttpComponents, OkHttp), you can implement the KrakenRestRequester interface and pass it to the KrakenAPI constructor:

```java
public class MyRestTemplateRestRequester implements KrakenRestRequester {
    public <T> T execute(PublicEndpoint<T> endpoint) { /* your implementation */ }
    public <T> T execute(PrivateEndpoint<T> endpoint, KrakenCredentials credentials, KrakenNonceGenerator nonceGenerator) { /* your implementation */ }
}

KrakenAPI api = new KrakenAPI(new KrakenCredentials(key, secret), new MyRestTemplateRestRequester());
```

See `DefaultKrakenRestRequester` for the default implementation.

### Custom nonce generator

For private endpoint requests, the nonce value is set to `System.currentTimeMillis()`. If you wish to use another value, you can specify a custom nonce generator when creating the `KrakenAPI` instance:

```java
KrakenAPI api = new KrakenAPI(
        new KrakenCredentials(key, secret),
        () -> Long.toString(System.currentTimeMillis() / 1000));
```

The second parameter is of type `KrakenNonceGenerator`, an interface containing a single `generate()` method returning a string.


[1]: https://docs.kraken.com/rest/
[2]: https://github.com/FasterXML/jackson
[3]: https://github.com/nyg/kraken-api-java/blob/v1.0.0/examples/src/main/java/dev/andstuff/kraken/example/Examples.java
