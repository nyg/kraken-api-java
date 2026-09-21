# Kraken API client for Java

[![Maven Central](https://img.shields.io/maven-central/v/dev.andstuff.kraken/kraken-api)](https://central.sonatype.com/artifact/dev.andstuff.kraken/kraken-api)

Query [Kraken's REST API][1] in Java.

## Maven

```xml
<dependency>
    <groupId>dev.andstuff.kraken</groupId>
    <artifactId>kraken-api</artifactId>
    <version>3.1.0</version>
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

Custom `KrakenNonceGenerator` implementations must produce increasing unsigned 64-bit integers as canonical decimal strings. `TradeVolume`, `AddOrderBatch` and `CancelOrderBatch` encode the nonce as a JSON number and reject malformed, out-of-range or noncanonical values with an `IllegalStateException` that names the parameter type and the generator contract. Canonical formatting keeps the nonce used for signing identical to the number in the JSON body.

### Trading

All nine Trading operations have typed methods: `addOrder`, `addOrderBatch`, `amendOrder`, `editOrder`, `cancelOrder`, `cancelOrderBatch`, `cancelAllOrders`, `cancelAllOrdersAfter`, and `webSocketsToken`. They require an API key with the order permissions Kraken documents for each operation; `webSocketsToken` requires the WebSocket interface permission.

```java
OrderAdded order = api.addOrder(AddOrderParams.builder()
        .pair("XBTUSD").side(OrderSide.BUY).orderType(OrderType.LIMIT)
        .volume(new BigDecimal("1.25")).price(new BigDecimal("27500"))
        .orderFlags(EnumSet.of(OrderFlag.POST))
        .close(new ConditionalClose(OrderType.STOP_LOSS, "25000"))
        .validate(true).build());

OrderAmended amend = api.amendOrder(AmendOrderParams.builder()
        .clientOrderId("my-order-1").limitPrice("+50").build());
OrderBatchAdded batch = api.addOrderBatch(AddOrderBatchParams.builder().pair("BTC/USD").orders(List.of(
        BatchOrder.builder().side(OrderSide.BUY).orderType(OrderType.LIMIT).volume(new BigDecimal("0.1")).price("40000").build(),
        BatchOrder.builder().side(OrderSide.SELL).orderType(OrderType.LIMIT).volume(new BigDecimal("0.1")).price("42000").build())).build());
OrderCancellation cancelled = api.cancelOrderBatch(CancelOrderBatchParams.builder()
        .transactionIds(List.of("OHYO67-6LP66-HMQ437")).userReferences(List.of(42)).build());
DeadMansSwitch timer = api.cancelAllOrdersAfter(CancelAllOrdersAfterParams.builder().timeout(Duration.ofSeconds(60)).build());
```

Prices accept a `BigDecimal` or a string, so relative prices such as `+1.5%` or `#10` are sent as Kraken documents them. With `validate(true)`, Kraken checks the order without submitting it and returns a description without transaction IDs. `amendOrder` changes an order in place, keeping its identifiers and, where possible, its queue priority; `editOrder` cancels the order and replaces it with a new one. `AmendOrderParams`, `EditOrderParams` and `CancelOrderParams` need exactly one order identifier and throw an `IllegalArgumentException` otherwise. In a batch, an order failing pre-match checks carries an `error()` while the other orders are still placed.

`cancelAllOrdersAfter` sets Kraken's dead man's switch: Kraken recommends calling it every 15 to 30 seconds with a 60-second timeout. `Duration.ZERO` disables the timer, and `triggerTime()` is then null. `AddOrderBatch` and `CancelOrderBatch` send JSON bodies; the other Trading operations use form encoding.

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

The endpoint is run through the same `KrakenRestRequester` as the built-in ones, and a `PrivateEndpoint` is signed with the credentials and nonce generator the `KrakenAPI` instance was built with. Querying one on an instance built without credentials throws an `IllegalStateException`. Funding (Beta) endpoints extend `FundingBetaEndpoint<T>` instead, taking the HTTP method and the path under `/funding`, e.g. `new FundingBetaEndpoint<>("GET", "v1/networks", new TypeReference<JsonNode>() {})`.

Pull requests adding such an endpoint to the library are welcome, see the [architecture documentation](docs/ARCHITECTURE.md).

### Funding

The ten Funding operations listed in issue #82 have typed methods: `depositMethods`, `depositAddresses`, `depositStatus`, `withdrawalMethods`, `withdrawalAddresses`, `withdrawalInfo`, `withdraw`, `withdrawalStatus`, `cancelWithdrawal`, and `walletTransfer`. These wrap Kraken's `/0/private` Funding endpoints, now grouped as [Funding (Legacy)](https://docs.kraken.com/api-reference/funding/get-deposit-methods); Funding (Beta) is a separate API group.

```java
List<DepositMethod> methods = api.depositMethods(DepositMethodsParams.builder().asset("XBT").build());
DepositStatus page = api.depositStatus(DepositStatusParams.builder().asset("XBT").cursor(true).limit(25).build());
if (page.nextCursor() != null && !page.nextCursor().isEmpty()) {
    DepositStatus nextPage = api.depositStatus(DepositStatusParams.builder().cursor(page.nextCursor()).limit(25).build());
}
WithdrawalInfo estimate = api.withdrawalInfo(WithdrawalInfoParams.builder()
        .asset("XBT").key("my-saved-withdrawal-key").amount(new BigDecimal("0.01")).build());
```

Status methods return the same response record for paginated objects and unpaginated arrays. Pass `cursor(true)` to start pagination and then pass each non-empty `nextCursor()` token to retrieve the next page. An unlimited deposit limit is represented by `DepositLimit.unlimited() == true`, with a null amount; a missing limit remains null. Amounts and fees use `BigDecimal`.

`withdraw` submits a withdrawal to a saved key, `cancelWithdrawal` requests cancellation, and `walletTransfer` moves assets from the Spot Wallet to the Futures Wallet. Withdrawal parameters support address confirmation and `maxFee`. A false cancellation result means Kraken did not accept the cancellation. Deposit address parameters support generating a new address and specifying the amount for Lightning invoices; responses preserve destination tags and memos.

### Funding (Beta)

All 15 [Funding (Beta)](https://docs.kraken.com/api-reference/funding-beta/list-funding-methods) operations have typed methods. They use stable method, network and address identifiers, withdrawal addresses saved for a method, a network or a network group, and fee quotes that pin the fee rate of a withdrawal.

| Operation | Typed method | Response |
|---|---|---|
| `GET /funding/v1/methods/{direction}` | `fundingMethods(direction)` / `fundingMethods(params)` | `FundingMethods` |
| `GET /funding/v1/assets/{direction}` | `fundingAssets(direction)` / `fundingAssets(params)` | `FundingAssets` |
| `GET /funding/v1/networks` | `fundingNetworks()` / `fundingNetworks(params)` | `FundingNetworks` |
| `GET /funding/v1/fees/{method_id}` | `fundingFees(params)` | `FundingFees` |
| `GET /funding/v1/limits/deposit/{asset_class}/{asset}` | `fundingDepositLimits(params)` | `FundingDepositLimits` |
| `GET /funding/v1/limits/withdrawal/{asset_class}/{asset}` | `fundingWithdrawalLimits(params)` | `FundingWithdrawalLimits` |
| `PUT /funding/v1/deposit/address` | `claimFundingDepositAddress(params)` | `ClaimedFundingDepositAddress` |
| `GET /funding/v2/deposit/addresses` | `fundingDepositAddresses()` / `fundingDepositAddresses(params)` | `FundingDepositAddresses` |
| `GET /funding/v1/deposits` | `fundingDeposits()` / `fundingDeposits(params)` | `FundingDeposits` |
| `GET /funding/v1/addresses` | `fundingAddresses()` / `fundingAddresses(params)` | `FundingAddresses` |
| `POST /funding/v1/addresses` | `createFundingAddress(params)` | `FundingAddressCreated` |
| `PUT /funding/v1/addresses/{id}` | `updateFundingAddress(params)` | `FundingAddressUpdated` |
| `DELETE /funding/v1/addresses/{id}` | `deleteFundingAddress(id)` / `deleteFundingAddress(params)` | `boolean` |
| `GET /funding/v1/withdrawals` | `fundingWithdrawals()` / `fundingWithdrawals(params)` | `FundingWithdrawals` |
| `POST /funding/v1/withdrawals` | `createFundingWithdrawal(params)` | `FundingWithdrawalCreated` |

```java
FundingMethods methods = api.fundingMethods(FundingMethodsParams.builder()
        .direction(Direction.WITHDRAW).asset(new Asset(AssetClass.CURRENCY, "USDC")).build());
String methodId = methods.methods().getFirst().methodId();

FundingAddressCreated address = api.createFundingAddress(CreateFundingAddressParams.builder()
        .scope(Scope.network(methods.methods().getFirst().network().networkId()))
        .address("0xBef7B36845cA31045E86D0B46DBCac4e6752").name("Hardware wallet").build());

FundingFees quote = api.fundingFees(FundingFeesParams.builder()
        .methodId(methodId).amount(new BigDecimal("5")).feeIncluded(true).build());
FundingWithdrawalCreated withdrawal = api.createFundingWithdrawal(CreateFundingWithdrawalParams.builder()
        .scope(Scope.method(methodId)).addressId(address.addressId())
        .amount(new AssetAmount(new Asset(AssetClass.CURRENCY, "USDC"), new BigDecimal("5")))
        .withdrawalFeeToken(quote.withdrawalFeeToken()).feeIncluded(true).build());
```

Pass a `withdrawalFeeToken` to pin the quoted fee rate for 5 minutes, or a `maxFee` to cap the current fee; `feeIncluded` must then be set and match the quote. List operations return a `nextCursor()`, null on the last page, to pass back as `cursor` without the other filters. Amounts use `BigDecimal`, times use `Instant`, and limit time windows use `Duration`; a limit value is a `count()` for attempt and success limits and `amounts()` otherwise. Every parameter builder accepts an `accountId`.

These endpoints live under `/funding` instead of `/0/private`: the nonce is sent in the `API-Nonce` header, the signed path includes the query string, nested query objects use bracket notation, e.g. `asset[class]=currency`, and bodies are JSON. Kraken answers errors with an HTTP error status, thrown as a `KrakenException` whose only error is the status code followed by the response body. The deposit `status` filter is not supported yet, as the specification doesn't define how its list and range forms are encoded.

### Custom REST requester

The current implementation of the library uses the JDK's HttpsURLConnection to make HTTP request. If that doesn't suit your needs and wish to use something else (e.g. Spring RestTemplate, Apache HttpComponents, OkHttp), you can implement the KrakenRestRequester interface and pass it to the KrakenAPI constructor:

```java
public class MyRestTemplateRestRequester implements KrakenRestRequester {
    public <T> T execute(PublicEndpoint<T> endpoint) { /* your implementation */ }
    public <T> T execute(PrivateEndpoint<T> endpoint, KrakenCredentials credentials, KrakenNonceGenerator nonceGenerator) { /* your implementation */ }
    public <T> T execute(FundingBetaEndpoint<T> endpoint, KrakenCredentials credentials, KrakenNonceGenerator nonceGenerator) { /* optional */ }
}

KrakenAPI api = new KrakenAPI(new KrakenCredentials(key, secret), new MyRestTemplateRestRequester());
```

The Funding (Beta) `execute` method has a default implementation throwing an `UnsupportedOperationException`, so existing requesters keep compiling; implement it to query Funding (Beta) endpoints: send `endpoint.encodedBody()` unchanged, sign it with `credentials.sign(url.getFile(), nonce, body)`, send the nonce in the `API-Nonce` header, and deserialize the whole response body into `endpoint.getResponseType()`. See `DefaultKrakenRestRequester` for the default implementation.

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
